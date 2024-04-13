package io.jenkins.plugins.customizable_header.headers;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;

public class LogoSelector extends HeaderSelector {

  @DataBoundConstructor
  public LogoSelector() {
  }

  @Extension
  @org.jenkinsci.Symbol("logo")
  public static class DescriptorImpl extends HeaderDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Logo";
    }
  }
}
