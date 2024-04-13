package io.jenkins.plugins.customizable_header.logo;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

public abstract class LogoDescriptor extends Descriptor<Logo> {
  public static DescriptorExtensionList<Logo, LogoDescriptor> all() {
    return Jenkins.get().getDescriptorList(Logo.class);
  }
}
