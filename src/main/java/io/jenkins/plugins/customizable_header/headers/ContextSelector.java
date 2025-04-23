package io.jenkins.plugins.customizable_header.headers;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class ContextSelector extends HeaderSelector {

  private boolean showJobWeather;
  private boolean showFolderWeather;
  private String symbolMappingFile;

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
  }

  public String getSymbolMappingFile() {
    return symbolMappingFile;
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
