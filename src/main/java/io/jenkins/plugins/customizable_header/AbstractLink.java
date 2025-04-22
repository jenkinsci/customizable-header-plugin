package io.jenkins.plugins.customizable_header;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import jenkins.model.Jenkins;

public abstract class AbstractLink implements Describable<AbstractLink>, ExtensionPoint {

  public static ExtensionList<AbstractLink> all() {
    return Jenkins.get().getExtensionList(AbstractLink.class);
  }

  public abstract String getType();
}
