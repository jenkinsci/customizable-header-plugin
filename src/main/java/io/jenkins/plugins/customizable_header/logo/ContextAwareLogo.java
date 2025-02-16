package io.jenkins.plugins.customizable_header.logo;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.FolderIcon;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.HealthReport;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.labels.LabelAtom;
import hudson.model.labels.LabelExpression;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.headers.ContextSelector;
import io.jenkins.plugins.customizable_header.headers.HeaderSelector;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ContextAwareLogo extends Logo {

    @DataBoundConstructor
    public ContextAwareLogo() {
    }

    @Extension
    @Symbol("contextAware")
    public static class DescriptorImpl extends LogoDescriptor {
        @NonNull
        @Override
        public String getDisplayName() {
            return "Context aware";
        }
    }


    private static final Logger LOGGER = Logger.getLogger(ContextAwareLogo.class.getName());

    private static final Map<String, Logo> defaultLogoMapping = new HashMap<>();
    private static final List<String> knownPathes = new ArrayList<>();

    public Logo getLogo() {
        List<Ancestor> ancestors = new ArrayList<>(Stapler.getCurrentRequest2().getAncestors());
        Collections.reverse(ancestors);
        HeaderSelector header = CustomHeaderConfiguration.get().getActiveHeader();
        ContextSelector contextSelector = null;
        if (header instanceof ContextSelector) {
            contextSelector = (ContextSelector) header;
        }
        // LOGGER.log(Level.FINE, "Path: {0}", Stapler.getCurrentRequest().getPathInfo());
        for (Ancestor ancestor : ancestors) {
            Object obj = ancestor.getObject();
            LOGGER.log(Level.FINE, "Context: {0}", obj.getClass().getName());
            if (obj instanceof Run<?, ?> run) {
                // TODO - Validate this
                return defaultLogoMapping.get(run.getBuildStatusIconClassName());
            }
            if (obj instanceof Job<?, ?> job) {
                if (contextSelector != null && contextSelector.isShowJobWeather()) {
                    HealthReport health = job.getBuildHealth();
                    return getLogoOrDefault(health.getIconClassName());
                }
                // TODO - Validate this
                return defaultLogoMapping.get(job.getBuildStatusIconClassName());
            }
            if (obj instanceof AbstractFolder<?> folder) {
                if (contextSelector != null && contextSelector.isShowFolderWeather()) {
                    HealthReport health = folder.getBuildHealth();
                    return getLogoOrDefault(health.getIconClassName());
                }
                FolderIcon folderIcon = folder.getIcon();
                return new Icon(folderIcon.getIconClassName());
            }
            if (obj instanceof Computer computer) {
                Logo logo = handleComputerUrl("hudson.model.Computer");
                return Objects.requireNonNullElseGet(logo, () -> new Icon(computer.getIconClassName()));
            }
            if (obj instanceof LabelAtom || obj instanceof LabelExpression) {
                return new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-pricetag-outline");
            }
            Logo logo = getLogoOrDefault(obj.getClass().getName());
            if (logo != null) {
                return logo;
            }
            if (obj instanceof Hudson) {
                logo = handleComputerUrl("hudson.model.Hudson");
                if (logo != null) {
                    return logo;
                }
            }
        }
        return new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-jenkins");
    }

    private Logo handleComputerUrl(String context) {
        String path = Stapler.getCurrentRequest2().getPathInfo();
        path = path.substring(path.lastIndexOf('/'));
        if (knownPathes.contains(path)) {
            return getLogoOrDefault(context + path);
        }
        return null;
    }

    private Logo getLogoOrDefault(String mapping) {
        String symbol = translateSymbol(mapping);
        if (symbol != null) {
            return getTranslatedLogo(symbol);
        }
        return defaultLogoMapping.get(mapping);
    }

    private Logo getTranslatedLogo(String name) {
        if (name.startsWith("file-")) {
            return new SvgLogo(name.substring(5), true);
        }
        return new io.jenkins.plugins.customizable_header.logo.Symbol(name);
    }

    @CheckForNull
    private String translateSymbol(String name) {
        HeaderSelector header = CustomHeaderConfiguration.get().getHeader();
        if (header instanceof ContextSelector) {
            return ((ContextSelector) header).getSymbolMapping().getProperty(name);
        }
        return null;
    }

    static {
        knownPathes.add("/script");
        knownPathes.add("/systemInfo");
        knownPathes.add("/builds");
        knownPathes.add("/log");
        knownPathes.add("/load-statistics");
        defaultLogoMapping.put(
                "hudson.LocalPluginManager", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-extension-puzzle-outline"));
        defaultLogoMapping.put(
                "hudson.model.ManageJenkinsAction", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-settings-outline"));
        defaultLogoMapping.put(
                "hudson.security.GlobalSecurityConfiguration", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-lock-closed-outline"));
        defaultLogoMapping.put(
                "jenkins.tools.GlobalToolConfiguration", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-hammer-outline"));
        defaultLogoMapping.put("hudson.model.ComputerSet", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-cloud-outline"));
        defaultLogoMapping.put("hudson.cli.CLIAction", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-terminal-outline"));
        defaultLogoMapping.put(
                "hudson.diagnosis.OldDataMonitor", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-trash-bin-outline"));
        defaultLogoMapping.put("jenkins.management.ShutdownLink", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-power-outline"));
        defaultLogoMapping.put(
                "hudson.logging.LogRecorderManager", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-journal-outline"));
        defaultLogoMapping.put("hudson.AboutJenkins", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-jenkins"));
        defaultLogoMapping.put("hudson.model.User", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-person"));
        defaultLogoMapping.put(
                "org.jenkinsci.plugins.configfiles.ConfigFilesManagement",
                new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-cfg-logo plugin-config-file-provider"));
        defaultLogoMapping.put(
                "com.cloudbees.plugins.credentials.ViewCredentialsAction$RootActionImpl",
                new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-id-card-outline"));
        defaultLogoMapping.put(
                "com.cloudbees.plugins.credentials.ViewCredentialsAction",
                new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-id-card-outline"));
        defaultLogoMapping.put(
                "org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval",
                new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-document-text-outline"));
        defaultLogoMapping.put(
                "com.cloudbees.plugins.credentials.GlobalCredentialsConfiguration",
                new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-credential-providers plugin-credentials"));
        defaultLogoMapping.put("hudson.model.Hudson/script", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-code-working-outline"));
        defaultLogoMapping.put("hudson.model.Hudson/systemInfo", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-server-outline"));
        defaultLogoMapping.put(
                "hudson.model.Hudson/load-statistics", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-analytics-outline"));
        defaultLogoMapping.put(
                "hudson.model.Hudson/builds", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-file-tray-full-outline"));
        defaultLogoMapping.put(
                "hudson.model.Computer/script", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-code-working-outline"));
        defaultLogoMapping.put("hudson.model.Computer/systemInfo", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-server-outline"));
        defaultLogoMapping.put(
                "hudson.model.Computer/load-statistics", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-analytics-outline"));
        defaultLogoMapping.put(
                "hudson.model.Computer/builds", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-file-tray-full-outline"));
        defaultLogoMapping.put("hudson.model.Computer/log", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-clipboard-outline"));

        defaultLogoMapping.put(
                "icon-aborted", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-aborted"));
        defaultLogoMapping.put(
                "icon-aborted-anime", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-aborted-anime"));
        defaultLogoMapping.put(
                "icon-blue", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-blue"));
        defaultLogoMapping.put(
                "icon-blue-anime", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-blue-anime"));
        defaultLogoMapping.put(
                "icon-disabled", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-disabled"));
        defaultLogoMapping.put(
                "icon-disabled-anime", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-disabled-anime"));
        defaultLogoMapping.put(
                "icon-nobuilt", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-nobuilt"));
        defaultLogoMapping.put(
                "icon-nobuilt-anime", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-nobuilt-anime"));
        defaultLogoMapping.put(
                "icon-red", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-red"));
        defaultLogoMapping.put(
                "icon-red-anime", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-red-anime"));
        defaultLogoMapping.put(
                "icon-yellow", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-yellow"));
        defaultLogoMapping.put(
                "icon-yellow-anime", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-status-yellow-anime"));

        defaultLogoMapping.put(
                "icon-health-80plus", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-weather-icon-health-80plus plugin-core"));
        defaultLogoMapping.put(
                "icon-health-00to19", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-weather-icon-health-00to19 plugin-core"));
        defaultLogoMapping.put(
                "icon-health-20to39", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-weather-icon-health-20to39 plugin-core"));
        defaultLogoMapping.put(
                "icon-health-40to59", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-weather-icon-health-40to59 plugin-core"));
        defaultLogoMapping.put(
                "icon-health-60to79", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-weather-icon-health-60to79 plugin-core"));
    }
}

