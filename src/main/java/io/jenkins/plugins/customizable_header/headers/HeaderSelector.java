package io.jenkins.plugins.customizable_header.headers;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;

public abstract class HeaderSelector extends AbstractDescribableImpl<HeaderSelector> implements ExtensionPoint {
  @Override
  public HeaderDescriptor getDescriptor() {
    return (HeaderDescriptor) super.getDescriptor();
  }
}
