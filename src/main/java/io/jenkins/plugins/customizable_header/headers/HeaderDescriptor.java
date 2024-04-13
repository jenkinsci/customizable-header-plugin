package io.jenkins.plugins.customizable_header.headers;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

public abstract class HeaderDescriptor extends Descriptor<HeaderSelector> {

  public static DescriptorExtensionList<HeaderSelector, HeaderDescriptor> all() {
    return Jenkins.get().getDescriptorList(HeaderSelector.class);
  }
}
