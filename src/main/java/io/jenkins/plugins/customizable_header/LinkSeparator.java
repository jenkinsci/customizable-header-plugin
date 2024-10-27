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
  public String getType() {
    return "separator";
  }

  @Override
  public int compareTo(AbstractLink other) {
    if (!(other instanceof LinkSeparator)) {
      return 1;
    }
    return title.compareTo(((LinkSeparator) other).title);
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
