package io.jenkins.plugins.customizable_header.headers;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Selector that has no own header.
 * This means that the Jenkins own header is used or a header from
 * another plugin.
 */
public class JenkinsHeaderSelector extends HeaderSelector {

  @DataBoundConstructor
  public JenkinsHeaderSelector() {
  }

  @Extension
  @org.jenkinsci.Symbol("jenkins")
  public static class DescriptorImpl extends HeaderDescriptor {
    @NonNull
    @Override
    public String getDisplayName() {
      return "Jenkins Header";
    }
  }
}
