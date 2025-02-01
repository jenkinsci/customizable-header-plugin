package io.jenkins.plugins.customizable_header.headers;

import hudson.Extension;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import jenkins.views.FullHeader;

@Extension
public class JenkinsHeader extends FullHeader implements SystemMessageProvider, LinkProvider {

  @Override
  public boolean isEnabled() {
    return CustomHeaderConfiguration.get().getActiveHeader() instanceof JenkinsHeaderSelector;
  }
}
