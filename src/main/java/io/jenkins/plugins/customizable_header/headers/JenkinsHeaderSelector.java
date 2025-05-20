package io.jenkins.plugins.customizable_header.headers;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;

public class JenkinsHeaderSelector extends HeaderSelector {

  @DataBoundConstructor
  public JenkinsHeaderSelector() {
  }

  @Extension(ordinal = -9999)
  public static class DescriptorImpl extends HeaderDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Jenkins Header";
    }
  }
}
