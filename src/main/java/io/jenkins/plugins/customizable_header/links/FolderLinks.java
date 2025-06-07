package io.jenkins.plugins.customizable_header.links;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.customizable_header.AbstractLink;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

public class FolderLinks extends AbstractFolderProperty<AbstractFolder<?>> {

  private final List<AbstractLink> links;

  @DataBoundConstructor
  public FolderLinks(List<AbstractLink> links) {
    this.links = links;
  }

  public List<AbstractLink> getLinks() {
    return links;
  }

  @Extension
  public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Header Links";
    }
  }
}
