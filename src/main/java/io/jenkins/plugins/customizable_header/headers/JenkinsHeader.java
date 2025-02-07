package io.jenkins.plugins.customizable_header.headers;

import hudson.Extension;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import jenkins.views.PartialHeader;

@Extension
public class JenkinsHeader extends PartialHeader implements SystemMessageProvider, LinkProvider {
  @Override
  public boolean isEnabled() {
    return CustomHeaderConfiguration.get().isEnabled() &&
        CustomHeaderConfiguration.get().getActiveHeader() instanceof JenkinsHeaderSelector;
  }

  @Override
  public int getSupportedHeaderVersion() {
    return 1;
  }

}
