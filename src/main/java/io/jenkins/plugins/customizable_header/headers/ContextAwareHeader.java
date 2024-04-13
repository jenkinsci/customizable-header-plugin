package io.jenkins.plugins.customizable_header.headers;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.FolderIcon;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.HealthReport;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.labels.LabelAtom;
import hudson.model.labels.LabelExpression;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.logo.Icon;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.SvgLogo;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;

@Extension(ordinal = 99998)
public class ContextAwareHeader extends LogoHeader {

  private static final Logger LOGGER = Logger.getLogger(ContextAwareHeader.class.getName());

  private static final Map<String, Logo> defaultLogoMapping = new HashMap<>();
  private static final List<String> knownPathes = new ArrayList<>();

  @Override
  public boolean isEnabled() {
    return CustomHeaderConfiguration.get().getActiveHeader() instanceof ContextSelector;
  }

  @Override
  public Logo getLogo() {
    List<Ancestor> ancestors = new ArrayList<>(Stapler.getCurrentRequest().getAncestors());
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
      if (obj instanceof Run) {
        Run<?, ?> run = (Run<?, ?>) obj;
        String symbol = translateSymbol(run.getBuildStatusIconClassName());
        if (symbol != null) {
          return new Symbol(symbol);
        }
        return new Icon(run.getBuildStatusIconClassName());
      }
      if (obj instanceof Job) {
        Job<?, ?> job = (Job<?, ?>) obj;
        if (contextSelector != null && contextSelector.isShowJobWeather()) {
          HealthReport health = job.getBuildHealth();
          return getLogoOrDefault(health.getIconClassName());
        }
        String symbol = translateSymbol(job.getBuildStatusIconClassName());
        if (symbol != null) {
          return new Symbol(symbol);
        }
        return new Icon(job.getBuildStatusIconClassName());
      }
      if (obj instanceof AbstractFolder) {
        AbstractFolder<?> folder = (AbstractFolder<?>) obj;
        if (contextSelector != null && contextSelector.isShowFolderWeather()) {
          HealthReport health = folder.getBuildHealth();
          return getLogoOrDefault(health.getIconClassName());
        }
        FolderIcon folderIcon = folder.getIcon();
        return new Icon(folderIcon.getIconClassName());
      }
      if (obj instanceof Computer) {
        Computer computer = (Computer) obj;
        Logo logo = handleComputerUrl("hudson.model.Computer");
        return Objects.requireNonNullElseGet(logo, () -> new Icon(computer.getIconClassName()));
      }
      if (obj instanceof LabelAtom || obj instanceof LabelExpression) {
        return new Symbol("symbol-pricetag-outline");
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
    return super.getLogo();
  }

  private Logo handleComputerUrl(String context) {
    String path = Stapler.getCurrentRequest().getPathInfo();
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
    return new Symbol(name);
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
        "hudson.LocalPluginManager", new Symbol("symbol-extension-puzzle-outline"));
    defaultLogoMapping.put(
        "hudson.model.ManageJenkinsAction", new Symbol("symbol-settings-outline"));
    defaultLogoMapping.put(
        "hudson.security.GlobalSecurityConfiguration", new Symbol("symbol-lock-closed-outline"));
    defaultLogoMapping.put(
        "jenkins.tools.GlobalToolConfiguration", new Symbol("symbol-hammer-outline"));
    defaultLogoMapping.put("hudson.model.ComputerSet", new Symbol("symbol-cloud-outline"));
    defaultLogoMapping.put("hudson.cli.CLIAction", new Symbol("symbol-terminal-outline"));
    defaultLogoMapping.put(
        "hudson.diagnosis.OldDataMonitor", new Symbol("symbol-trash-bin-outline"));
    defaultLogoMapping.put("jenkins.management.ShutdownLink", new Symbol("symbol-power-outline"));
    defaultLogoMapping.put(
        "hudson.logging.LogRecorderManager", new Symbol("symbol-journal-outline"));
    defaultLogoMapping.put("hudson.AboutJenkins", new Symbol("symbol-jenkins"));
    defaultLogoMapping.put("hudson.model.User", new Symbol("symbol-person"));
    defaultLogoMapping.put(
        "org.jenkinsci.plugins.configfiles.ConfigFilesManagement",
        new Symbol("symbol-cfg-logo plugin-config-file-provider"));
    defaultLogoMapping.put(
        "com.cloudbees.plugins.credentials.ViewCredentialsAction$RootActionImpl",
        new Symbol("symbol-id-card-outline"));
    defaultLogoMapping.put(
        "com.cloudbees.plugins.credentials.ViewCredentialsAction",
        new Symbol("symbol-id-card-outline"));
    defaultLogoMapping.put(
        "org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval",
        new Symbol("symbol-document-text-outline"));
    defaultLogoMapping.put(
        "com.cloudbees.plugins.credentials.GlobalCredentialsConfiguration",
        new Symbol("symbol-credential-providers plugin-credentials"));
    defaultLogoMapping.put("hudson.model.Hudson/script", new Symbol("symbol-code-working-outline"));
    defaultLogoMapping.put("hudson.model.Hudson/systemInfo", new Symbol("symbol-server-outline"));
    defaultLogoMapping.put(
        "hudson.model.Hudson/load-statistics", new Symbol("symbol-analytics-outline"));
    defaultLogoMapping.put(
        "hudson.model.Hudson/builds", new Symbol("symbol-file-tray-full-outline"));
    defaultLogoMapping.put(
        "hudson.model.Computer/script", new Symbol("symbol-code-working-outline"));
    defaultLogoMapping.put("hudson.model.Computer/systemInfo", new Symbol("symbol-server-outline"));
    defaultLogoMapping.put(
        "hudson.model.Computer/load-statistics", new Symbol("symbol-analytics-outline"));
    defaultLogoMapping.put(
        "hudson.model.Computer/builds", new Symbol("symbol-file-tray-full-outline"));
    defaultLogoMapping.put("hudson.model.Computer/log", new Symbol("symbol-clipboard-outline"));
    defaultLogoMapping.put(
        "icon-health-80plus", new Symbol("symbol-weather-icon-health-80plus plugin-core"));
    defaultLogoMapping.put(
        "icon-health-00to19", new Symbol("symbol-weather-icon-health-00to19 plugin-core"));
    defaultLogoMapping.put(
        "icon-health-20to39", new Symbol("symbol-weather-icon-health-20to39 plugin-core"));
    defaultLogoMapping.put(
        "icon-health-40to59", new Symbol("symbol-weather-icon-health-40to59 plugin-core"));
    defaultLogoMapping.put(
        "icon-health-60to79", new Symbol("symbol-weather-icon-health-60to79 plugin-core"));
  }
}
