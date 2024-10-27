package io.jenkins.plugins.customizable_header;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import jenkins.model.Jenkins;

public abstract class AbstractLink extends AbstractDescribableImpl<AbstractLink> implements ExtensionPoint, Comparable<AbstractLink> {

  public static ExtensionList<AbstractLink> all() {
    return Jenkins.get().getExtensionList(AbstractLink.class);
  }

  public abstract String getType();

  @Override
  public LinkDescriptor getDescriptor() {
    return (LinkDescriptor) super.getDescriptor();
  }
}
