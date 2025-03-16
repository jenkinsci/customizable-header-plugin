package io.jenkins.plugins.customizable_header;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.FolderIcon;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.HealthReport;
import hudson.model.Hudson;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.User;
import hudson.model.labels.LabelAtom;
import hudson.model.labels.LabelExpression;
import io.jenkins.plugins.customizable_header.logo.Icon;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.SvgLogo;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.Stapler;

public class ContextAwareLogo extends AbstractDescribableImpl<ContextAwareLogo> {

  private boolean showJobWeather;
  private boolean showFolderWeather;
  private String symbolMappingFile;

  private transient Properties customMapping;
  private transient long lastModified;
  private transient String mappingFile;

  @DataBoundConstructor
  public ContextAwareLogo() {
  }

  public boolean getShowJobWeather() {
    return showJobWeather;
  }

  @DataBoundSetter
  public void setShowJobWeather(boolean showJobWeather) {
    this.showJobWeather = showJobWeather;
  }

  public boolean getShowFolderWeather() {
    return showFolderWeather;
  }

  @DataBoundSetter
  public void setShowFolderWeather(boolean showFolderWeather) {
    this.showFolderWeather = showFolderWeather;
  }

  @DataBoundSetter
  public void setSymbolMappingFile(String symbolMappingFile) {
    this.symbolMappingFile = symbolMappingFile;
    lastModified = 0;
  }

  public String getSymbolMappingFile() {
    return symbolMappingFile;
  }

  private void updateMappingFile() {
    if (Util.fixEmptyAndTrim(symbolMappingFile) != null) {
      File file = new File(symbolMappingFile);
      if (!file.isAbsolute()) {
        file = new File(Jenkins.get().getRootDir(), symbolMappingFile);
      }
      if (file.isFile()) {
        mappingFile = file.getAbsolutePath();
      } else {
        mappingFile = null;
      }
    } else {
      mappingFile = null;
    }
    lastModified = -1;
  }

  @Restricted(NoExternalUse.class)
  public Properties getSymbolMapping() {
    if (customMapping == null) {
      customMapping = new Properties();
    }
    if (lastModified == 0) {
      updateMappingFile();
    }
    if (mappingFile != null) {
      File file = new File(mappingFile);
      if (file.lastModified() != lastModified) {
        lastModified = file.lastModified();
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8)) {
          customMapping.clear();
          customMapping.load(reader);
        } catch (IOException e) {
          LOGGER.log(Level.WARNING, "Failed to read custom mapping.", e);
        }
      }
    }
    return customMapping;
  }
  private static final Logger LOGGER = Logger.getLogger(ContextAwareLogo.class.getName());

  private static final Map<String, Logo> defaultLogoMapping = new HashMap<>();
  private static final List<String> knownPathes = new ArrayList<>();

  public Logo getLogo() {
    List<Ancestor> ancestors = new ArrayList<>(Stapler.getCurrentRequest2().getAncestors());
    Collections.reverse(ancestors);
    // LOGGER.log(Level.FINE, "Path: {0}", Stapler.getCurrentRequest2().getPathInfo());
    for (Ancestor ancestor : ancestors) {
      Object obj = ancestor.getObject();
      LOGGER.log(Level.FINE, "Context: {0}", obj.getClass().getName());
      if (obj instanceof Run<?, ?> run) {
        // TODO - Validate this
        return defaultLogoMapping.get(run.getBuildStatusIconClassName());
      }
      if (obj instanceof Job<?, ?> job) {
        if (showJobWeather) {
          HealthReport health = job.getBuildHealth();
          return getLogoOrDefault(health.getIconClassName());
        }
        // TODO - Validate this
        return defaultLogoMapping.get(job.getBuildStatusIconClassName());
      }
      if (obj instanceof AbstractFolder<?> folder) {
        if (showFolderWeather) {
          HealthReport health = folder.getBuildHealth();
          return getLogoOrDefault(health.getIconClassName());
        }
        FolderIcon folderIcon = folder.getIcon();
        return new Icon(folderIcon.getIconClassName());
      }
      if (obj instanceof Computer computer) {
        Logo logo = handleComputerUrl("hudson.model.Computer");
        return Objects.requireNonNullElseGet(logo, () -> new io.jenkins.plugins.customizable_header.logo.Symbol(computer.getIconClassName()));
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
    return null;
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
    return getSymbolMapping().getProperty(name);
  }

  static {
    knownPathes.add("/script");
    knownPathes.add("/systemInfo");
    knownPathes.add("/builds");
    knownPathes.add("/log");
    knownPathes.add("/load-statistics");
    defaultLogoMapping.put(
        "jenkins.appearance.AppearanceGlobalConfiguration", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-brush-outline"));
    defaultLogoMapping.put(
        "hudson.LocalPluginManager", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-extension-puzzle-outline"));
    defaultLogoMapping.put(
        "hudson.model.ManageJenkinsAction", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-settings-outline"));
    defaultLogoMapping.put(
        "hudson.security.GlobalSecurityConfiguration", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-lock-closed-outline"));
    defaultLogoMapping.put(
        "jenkins.tools.GlobalToolConfiguration", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-hammer-outline"));
    defaultLogoMapping.put("hudson.model.ComputerSet", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-computer"));
    defaultLogoMapping.put("jenkins.agents.CloudSet", new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-cloud-outline"));
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
    defaultLogoMapping.put(
        "io.jenkins.plugins.casc.ConfigurationAsCode",
        new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-logo plugin-configuration-as-code"));
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

  @Extension
  public static class DescriptorImpl extends Descriptor<ContextAwareLogo> {

    @Override
    @NonNull
    public String getDisplayName() {
      return "";
    }

    public boolean isRoot() {
      List<Ancestor> ancestors = new ArrayList<>(Stapler.getCurrentRequest2().getAncestors());
      return ancestors.stream().noneMatch(it -> it.getObject() instanceof User);
    }

  }
}
