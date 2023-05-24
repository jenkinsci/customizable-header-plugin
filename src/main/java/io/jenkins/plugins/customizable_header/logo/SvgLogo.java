package io.jenkins.plugins.customizable_header.logo;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import jenkins.model.Jenkins;
import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.filters.StringInputStream;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SvgLogo extends Logo {
    private static final Logger LOGGER = Logger.getLogger(SvgLogo.class.getName());

    private String logoPath;
    private boolean forceFile;

    private static final transient Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();

    @DataBoundConstructor
    public SvgLogo(String logoPath) {
        this.logoPath = logoPath;
    }

    public SvgLogo(String logoPath, boolean forceFile) {
        this.logoPath = logoPath;
        this.forceFile = forceFile;
    }

    public String getContent() {
        return cache.get(logoPath, key -> getSymbol(logoPath));
    }

    public String getLogoPath() {
        return logoPath;
    }

    @Extension
    @Symbol("svg")
    public static class DescriptorImpl extends LogoDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "SVG Logo";
        }
    }

    @CheckForNull
    private static String getFileLogoContent(String logoPath) {
        File file = new File(logoPath);
        if (!file.isAbsolute()) {
            file = new File(Jenkins.get().getRootDir(), logoPath);
        }
        if (!file.isFile()) {
            return null;
        }
        try {
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Failed to read logo file", ioe);
        }
        return null;
    }

    @CheckForNull
    private String getSymbolContent(String logoPath) {
        String symbol = getFileLogoContent(logoPath);
        if (symbol != null || forceFile) {
            return symbol;
        }
        return getUrlLogoContent(logoPath);
    }

    @CheckForNull
    private String getUrlLogoContent(String logoPath) {
        URI uri = URI.create(logoPath);
        if (!uri.isAbsolute()) {
            uri = URI.create(Jenkins.get().getRootUrl() + logoPath);
        }
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request,  HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e){
            LOGGER.log(Level.WARNING, "Failed to read url with logo", e);
        }
        return null;
    }

    @CheckForNull
    private String getSymbol(String logoFilePath) {
        LOGGER.log(Level.FINER, "logo url {0}", logoFilePath);
        if (Util.fixEmptyAndTrim(logoFilePath) == null) {
            return null;
        }
        String content = getSymbolContent(logoFilePath);
        if (content == null) {
            return null;
        }
        if (!validate(content)) {
            return null;
        }
        content = content.replaceAll("(<title>)[^&]*(</title>)", "")
                .replaceAll("(tooltip=\")[^&]*?(\")", "")
                .replaceAll("(data-html-tooltip=\").*?(\")", "")
                .replaceAll("<svg", "<svg aria-hidden=\"true\"")
                .replaceAll("<svg", "<svg class=\"custom-header__logo\"")
                .replaceAll("<svg", "<svg alt=\"[Jenkins]\"")
                .replace("stroke:#000", "stroke:currentColor");

        LOGGER.log(Level.FINEST, "Logo {0}", content);
        return content;
    }

    /*
    Perform some simple check that the given string is a valid xml and has "svg" as it's root element name
    This check is required to avoid that an attacker that has access to the file system can inject arbitrary html
     */
    private static boolean validate(String src) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new StringInputStream(src));
            if (!"svg".equals(doc.getDocumentElement().getNodeName())) {
                LOGGER.log(Level.WARNING, "The given src for the svg doesn't seem to have 'svg' as it's root element");
                return false;
            }
            return true;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.log(Level.WARNING, "The given src for the svg is not a valid xml document");
        }
        return false;
    }

}
