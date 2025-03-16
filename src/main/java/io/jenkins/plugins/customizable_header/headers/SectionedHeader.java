package io.jenkins.plugins.customizable_header.headers;

import hudson.Extension;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;

@Extension
public class SectionedHeader extends AbstractCustomHeader {

  public String getTitle() {
    return CustomHeaderConfiguration.get().getTitle();
  }

  @Override
  public boolean isEnabled() {
    return CustomHeaderConfiguration.get().isEnabled() &&
        CustomHeaderConfiguration.get().getActiveHeader() instanceof SectionedHeaderSelector;
  }
}
