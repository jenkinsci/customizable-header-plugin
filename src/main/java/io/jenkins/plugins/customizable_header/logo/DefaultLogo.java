package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class DefaultLogo extends Logo {

  @DataBoundConstructor
  public DefaultLogo() {
  }

  @Extension
  @Symbol("default")
  public static class DescriptorImpl extends LogoDescriptor {
    @NonNull
    @Override
    public String getDisplayName() {
      return "Default";
    }
  }
}
