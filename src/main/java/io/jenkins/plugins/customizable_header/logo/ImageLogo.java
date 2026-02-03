package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.util.FormValidation;
import io.jenkins.plugins.customizable_header.HeaderRootAction;
import io.jenkins.plugins.customizable_header.RemoteAssetCache;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

public class ImageLogo extends Logo {

  private String logoUrl;

  @DataBoundConstructor
  public ImageLogo(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public String getUrl() {
    return proxiedUrl(logoUrl);
  }

  private String proxiedUrl(String remoteUrl) {
    RemoteAssetCache.addUrlToCache(remoteUrl);
    String enc = URLEncoder.encode(remoteUrl, StandardCharsets.UTF_8);
    return Jenkins.get().getRootUrl() + "customizable-header/fetch?u=" + enc;
  }

  @Extension
  @Symbol("image")
  public static class DescriptorImpl extends LogoDescriptor {
    @NonNull
    @Override
    public String getDisplayName() {
      return "Image";
    }

    @POST
    public FormValidation doCheckLogoUrl(@QueryParameter String value) throws Exception {
      if (value == null || value.isBlank()) {
        return FormValidation.error("Logo URL must not be empty");
      }
      try {
        URI uri = new URI(value);
        if (!uri.isAbsolute()) {
          String path = uri.getPath();
          Path filePath = HeaderRootAction.resolvePath(path);
          if (HeaderRootAction.isNotValidPath(filePath)) {
            return FormValidation.error("Relative path must be within the \"userContent\" directory under \"JENKINS_HOME\"");
          }
        } else {
          String scheme = uri.getScheme();
          if (!scheme.equals("http") && !scheme.equals("https")) {
            return FormValidation.error("Only HTTP and HTTPS URLs are supported");
          }
        }
        return FormValidation.ok();
      } catch (URISyntaxException e) {
        return FormValidation.error("Invalid URL: " + e.getMessage());
      }
    }
  }
}
