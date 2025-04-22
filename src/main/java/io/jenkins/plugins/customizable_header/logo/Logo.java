package io.jenkins.plugins.customizable_header.logo;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Describable;
import jenkins.model.Jenkins;

public abstract class Logo implements Describable<Logo>, ExtensionPoint {

  public static ExtensionList<Logo> all() {
    return Jenkins.get().getExtensionList(Logo.class);
  }
}
