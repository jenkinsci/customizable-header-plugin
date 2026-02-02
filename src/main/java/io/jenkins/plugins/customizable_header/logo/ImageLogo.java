package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.customizable_header.RemoteAssetCache;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

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
  }

}
