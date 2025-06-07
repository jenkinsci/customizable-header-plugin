package io.jenkins.plugins.customizable_header.links;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Job;
import io.jenkins.plugins.customizable_header.AbstractLink;
import java.util.List;
import jenkins.model.OptionalJobProperty;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest2;

public class JobLinks extends OptionalJobProperty<Job<?, ?>> {

  private final List<AbstractLink> links;

  @DataBoundConstructor
  public JobLinks(List<AbstractLink> links) {
    this.links = links;
  }

  public List<AbstractLink> getLinks() {
    return links;
  }

  @Extension
  public static class DescriptorImpl extends OptionalJobPropertyDescriptor {

    @NonNull
    @Override
    public String getDisplayName() {
      return "Header Links";
    }

    @Override
    public JobLinks newInstance(StaplerRequest2 req, JSONObject formData) throws FormException {
      JobLinks prop = (JobLinks) super.newInstance(req, formData);
      if (prop == null || prop.links == null || prop.links.isEmpty()) {
        return null;
      }
      return prop;
    }
  }
}
