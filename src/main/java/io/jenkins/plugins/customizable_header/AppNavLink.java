package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import io.jenkins.plugins.customizable_header.logo.ImageLogo;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.LogoDescriptor;
import io.jenkins.plugins.customizable_header.logo.NoLogo;
import io.jenkins.plugins.customizable_header.logo.SvgLogo;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jenkins.ui.symbol.SymbolRequest;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public class AppNavLink extends AbstractDescribableImpl<AppNavLink> implements Comparable<AppNavLink> {

  private String url;
  private String label;
  private Logo logo;

  private boolean external;

  private transient String color = "";

  @DataBoundConstructor
  public AppNavLink(String url, String label, Logo logo) {
    this.url = url;
    this.label = label;
    this.logo = logo;
  }

  @Exported
  public boolean getExternal() {
    return external;
  }

  @DataBoundSetter
  public void setExternal(boolean external) {
    this.external = external;
  }

  @Exported
  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Exported
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Logo getLogo() {
    return logo;
  }

  public void setLogo(Logo logo) {
    this.logo = logo;
  }


  @Exported
  public String getLinkUrl() {
    try {
      URI uri = new URI(url);
      if (!uri.isAbsolute()) {
        if (url.startsWith("/")) {
          return url;
        }
        StaplerRequest currentRequest = Stapler.getCurrentRequest();
        String rootURL = currentRequest.getContextPath();
        return rootURL + "/" + url;
      }
    } catch (URISyntaxException e) {
      return url;
    }
    return url;
  }

  @Exported
  public String getIconXml() {
    if (logo instanceof SvgLogo) {
      return ((SvgLogo) logo).getContent();
    }
    if (logo instanceof Symbol) {
      String symbol = ((Symbol) logo).getSymbol();
      return org.jenkins.ui.symbol.Symbol.get(new SymbolRequest.Builder()
          .withName(symbol.split(" ")[0].substring(7))
          .withPluginName(extractPluginNameFromIconSrc(symbol))
          .withClasses("icon-md")
          .build()
      );
    }
    return null;
  }

  @Exported
  public String getIconUrl() {
    if (logo instanceof ImageLogo) {
      return ((ImageLogo) logo).getUrl();
    }
    if (logo instanceof NoLogo) {
      return "";
    }
    return "svg";
  }

  private static String extractPluginNameFromIconSrc(String iconSrc) {
    if (iconSrc == null) {
      return "";
    }

    if (!iconSrc.contains("plugin-")) {
      return "";
    }

    String[] arr = iconSrc.split(" ");
    for (String element : arr) {
      if (element.startsWith("plugin-")) {
        return element.replaceFirst("plugin-", "");
      }
    }

    return "";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AppNavLink that = (AppNavLink) o;
    return Objects.equals(url, that.url) && Objects.equals(label, that.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url, label);
  }

  @Override
  public int compareTo(AppNavLink other) {
    int labelCompare = label.compareToIgnoreCase(other.label);
    if (labelCompare != 0) {
      return labelCompare;
    }
    return url.compareTo(other.url);
  }

  @Extension
  public static class DescriptorImpl extends Descriptor<AppNavLink> {
    @Override
    @NonNull
    public String getDisplayName() {
      return "";
    }

    public List<Descriptor<Logo>> getLogoDescriptors() {
      return LogoDescriptor.all().stream().filter(
          d -> d instanceof Symbol.DescriptorImpl ||
              d instanceof SvgLogo.DescriptorImpl ||
              d instanceof ImageLogo.DescriptorImpl ||
              d instanceof NoLogo.DescriptorImpl
      ).collect(Collectors.toList());
    }
  }
}
