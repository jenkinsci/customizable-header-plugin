package io.jenkins.plugins.customizable_header.headers;

import hudson.Extension;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;

@Extension
public class JenkinsWrapperHeader extends AbstractCustomHeader {

  // TODO - rename this
  public boolean getCustomizeAllowed() {
    return CustomHeaderConfiguration.get().isEnabled();
  }

  @Override
  public boolean isEnabled() {
    CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
    return !config.isEnabled() || config.getActiveHeader() instanceof JenkinsWrapperHeaderSelector;
  }
}
