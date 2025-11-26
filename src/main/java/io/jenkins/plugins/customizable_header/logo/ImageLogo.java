package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import jenkins.security.csp.Contributor;
import jenkins.security.csp.CspBuilder;
import jenkins.security.csp.Directive;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.accmod.restrictions.suppressions.SuppressRestrictedWarnings;
import org.kohsuke.stapler.DataBoundConstructor;

public class ImageLogo extends Logo {

  private static final Logger LOGGER = Logger.getLogger(ImageLogo.class.getName());

  private String logoUrl;

  @DataBoundConstructor
  public ImageLogo(String logoUrl) {
    this.logoUrl = logoUrl;
    LogoUrlContributor.allowLogoUrl(logoUrl);
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public String getUrl() {
    URI uri = URI.create(logoUrl);
    if (!uri.isAbsolute()) {
      uri = URI.create(Jenkins.get().getRootUrl() + logoUrl);
    }
    return uri.toString();
  }

  private Object readResolve() {
    LogoUrlContributor.allowLogoUrl(logoUrl);
    return this;
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

  @Extension
  @Restricted(NoExternalUse.class)
  @SuppressRestrictedWarnings({ Contributor.class, CspBuilder.class })
  public static class LogoUrlContributor implements Contributor {
    private String url;

    @Override
    public void apply(CspBuilder cspBuilder) {
      if (url != null) {
        cspBuilder.add(Directive.IMG_SRC, url);
      }
    }

    public static void allowLogoUrl(String url) {
      try {
        URI uri = URI.create(url);
        if (uri.isAbsolute()) {
          // ImageLogo does not support scheme-relative URLs either
          ExtensionList.lookupSingleton(LogoUrlContributor.class).url = url;
        }
      } catch (RuntimeException ex) {
        LOGGER.log(Level.FINE, "Failed to allow logo URL: " + url, ex);
      }
    }
  }
}
