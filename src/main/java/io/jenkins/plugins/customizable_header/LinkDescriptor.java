package io.jenkins.plugins.customizable_header;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

public class LinkDescriptor extends Descriptor<AbstractLink> {
  public static DescriptorExtensionList<AbstractLink, LinkDescriptor> all() {
    return Jenkins.get().getDescriptorList(AbstractLink.class);
  }
}
