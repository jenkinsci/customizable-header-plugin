package io.jenkins.plugins.customizable_header.headers;


import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.FolderIcon;
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.HealthReport;
import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.logo.Icon;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class ContextAwareHeader extends LogoHeader {

    private static final Logger LOGGER = Logger.getLogger(ContextAwareHeader.class.getName());

    private static Map<String, String> symbolMapping = new HashMap<>();
    @Override
    public boolean isEnabled() {
        return CustomHeaderConfiguration.getInstance().getHeader() instanceof ContextSelector;
    }

    @Override
    public Logo getLogo() {
        List<Ancestor> ancestors = new ArrayList<>(Stapler.getCurrentRequest().getAncestors());
        Collections.reverse(ancestors);
        for (Ancestor ancestor: ancestors) {
            Object obj = ancestor.getObject();
            if (obj instanceof Run) {
                Run<?, ?> run = (Run<?, ?>) obj;
                return new Icon(run.getBuildStatusIconClassName(), run.getIconColor().getImage());
            }
            if (obj instanceof Job) {
                Job<?, ?> job = (Job<?, ?>) obj;
                HeaderSelector header = CustomHeaderConfiguration.getInstance().getHeader();
                if (header instanceof ContextSelector) {
                    if (((ContextSelector) header).isShowJobWeather()) {
                        HealthReport health = job.getBuildHealth();
                        return new Symbol("symbol-weather-" + health.getIconClassName() + " plugin-core");
                    }
                }
                return new Icon(job.getBuildStatusIconClassName(), job.getIconColor().getImage());
            }
            if (obj instanceof AbstractFolder) {
                AbstractFolder<?> folder = (AbstractFolder<?>) obj;
                HeaderSelector header = CustomHeaderConfiguration.getInstance().getHeader();
                if (header instanceof ContextSelector) {
                    if (((ContextSelector) header).isShowFolderWeather()) {
                        HealthReport health = folder.getBuildHealth();
                        return new Symbol("symbol-weather-" + health.getIconClassName() + " plugin-core");
                    }
                }
                FolderIcon folderIcon = folder.getIcon();
                return new Icon(folderIcon.getIconClassName(), folderIcon.getDescription());
            }
            if (obj instanceof Computer) {
                Computer computer = (Computer) obj;
                return new Icon(computer.getIconClassName(), null);
            }
            String symbol = symbolMapping.get(obj.getClass().getName());
            if (symbol != null) {
                return new Symbol(symbol);
            }
        }
        return super.getLogo();
    }

    static {
        symbolMapping.put("hudson.LocalPluginManager", "symbol-extension-puzzle-outline");
        symbolMapping.put("hudson.model.ManageJenkinsAction", "symbol-settings-outline");
        symbolMapping.put("hudson.security.GlobalSecurityConfiguration", "symbol-lock-closed-outline");
        symbolMapping.put("jenkins.tools.GlobalToolConfiguration", "symbol-hammer-outline");
        symbolMapping.put("hudson.model.ComputerSet", "symbol-cloud-outline");
        symbolMapping.put("hudson.cli.CLIAction", "symbol-terminal-outline");
        symbolMapping.put("hudson.diagnosis.OldDataMonitor", "symbol-trash-bin-outline");
        symbolMapping.put("jenkins.management.ShutdownLink", "symbol-power-outline");
        symbolMapping.put("hudson.logging.LogRecorderManager", "symbol-journal-outline");
        symbolMapping.put("hudson.AboutJenkins", "symbol-jenkins");
        symbolMapping.put("org.jenkinsci.plugins.configfiles.ConfigFilesManagement", "symbol-cfg-logo plugin-config-file-provider");
        symbolMapping.put("com.cloudbees.plugins.credentials.ViewCredentialsAction$RootActionImpl", "symbol-id-card-outline");
        symbolMapping.put("org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval", "symbol-document-text-outline");
    }

}
