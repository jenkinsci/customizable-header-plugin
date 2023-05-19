package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import jenkins.model.Jenkins;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SvgLogo extends Logo {
    private static final Logger LOGGER = Logger.getLogger(SvgLogo.class.getName());

    private String logoPath;

    private transient String content;

    @DataBoundConstructor
    public SvgLogo(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getContent() {
        if (content == null) {
            content = getSymbol(logoPath);
        }
        return content;
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
        if (!file.isFile()) {
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
        }
        return null;
    }

    @CheckForNull
    private static String getSymbolContent(String logoPath) {
        String symbol = getFileLogoContent(logoPath);
        if (symbol != null) {
            return symbol;
        }
        return getUrlLogoContent(logoPath);
    }

    @CheckForNull
    private static String getUrlLogoContent(String logoPath) {
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
    private static String getSymbol(String logoFilePath) {
        LOGGER.log(Level.FINER, "logo url {0}", logoFilePath);
        if (Util.fixEmptyAndTrim(logoFilePath) == null) {
            return null;
        }
        String symbol = getSymbolContent(logoFilePath);
        if (symbol == null) {
            return null;
        }
        symbol = symbol.replaceAll("(<title>)[^&]*(</title>)", "");
        //symbol = symbol.replaceAll("(class=\")[^&]*?(\")", "");
        symbol = symbol.replaceAll("(tooltip=\")[^&]*?(\")", "");
        //symbol = symbol.replaceAll("(id=\")[^&]*?(\")", "");
        symbol = symbol.replaceAll("<svg", "<svg aria-hidden=\"true\"");
        symbol = symbol.replaceAll("<svg", "<svg class=\"custom-header__logo\"");
        symbol = symbol.replace("stroke:#000", "stroke:currentColor");

        LOGGER.log(Level.FINEST, "Logo {0}", symbol);
        return symbol;
    }

}
