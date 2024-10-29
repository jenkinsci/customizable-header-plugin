package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public class LinkSeparator extends AbstractLink {

  private String title;

  @DataBoundConstructor
  public LinkSeparator() {
  }

  @DataBoundSetter
  public void setTitle(String title) {
    this.title = title;
  }

  @Exported
  public String getTitle() {
    return title;
  }

  @Exported
  @Override
  public String getType() {
    return "separator";
  }

  @Extension
  public static class DescriptorImpl extends LinkDescriptor {
    @Override
    @NonNull
    public String getDisplayName() {
      return "Separator";
    }
  }
}
