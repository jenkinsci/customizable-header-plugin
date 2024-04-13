package io.jenkins.plugins.customizable_header.headers;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Util;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class ContextSelector extends HeaderSelector {

  private static final Logger LOGGER = Logger.getLogger(ContextSelector.class.getName());
  private boolean showJobWeather;

  private boolean showFolderWeather;

  private String symbolMappingFile;

  private transient Properties customMapping;
  private transient long lastModified;
  private transient String mappingFile;

  @DataBoundConstructor
  public ContextSelector(boolean showJobWeather, boolean showFolderWeather) {
    this.showJobWeather = showJobWeather;
    this.showFolderWeather = showFolderWeather;
  }

  public boolean isShowJobWeather() {
    return showJobWeather;
  }

  public boolean isShowFolderWeather() {
    return showFolderWeather;
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

  @Extension
  @org.jenkinsci.Symbol("context")
  public static class DescriptorImpl extends HeaderDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Context Aware";
    }
  }
}
