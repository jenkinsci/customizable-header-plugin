package io.jenkins.plugins.customizable_header.logo;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import jenkins.model.Jenkins;

public abstract class Logo extends AbstractDescribableImpl<Logo> implements ExtensionPoint {

  public static ExtensionList<Logo> all() {
    return Jenkins.get().getExtensionList(Logo.class);
  }

  @Override
  public LogoDescriptor getDescriptor() {
    return (LogoDescriptor) super.getDescriptor();
  }
}
