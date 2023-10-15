package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.Functions;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import io.jenkins.plugins.customizable_header.logo.ImageLogo;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.LogoDescriptor;
import io.jenkins.plugins.customizable_header.logo.NoLogo;
import io.jenkins.plugins.customizable_header.logo.SvgLogo;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.jenkins.ui.symbol.SymbolRequest;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean
public class AppNavLink extends AbstractDescribableImpl<AppNavLink> {

  private static final Logger LOGGER = Logger.getLogger(AppNavLink.class.getName());
  private String url;
  private String label;
  private Logo logo;

  @DataBoundConstructor
  public AppNavLink(String url, String label, Logo logo) {
    this.url = url;
    this.label = label;
    this.logo = logo;
  }

  @Exported
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
  public String getIconXml() {
    if (logo instanceof SvgLogo) {
      LOGGER.log(Level.INFO, "Serving logo {0}", ((SvgLogo) logo).getLogoPath());
      return ((SvgLogo) logo).getContent();
    }
    if (logo instanceof Symbol) {
      String symbol = ((Symbol) logo).getSymbol();
      LOGGER.log(Level.INFO, "Serving Symbol {0}", symbol);
      return org.jenkins.ui.symbol.Symbol.get(new SymbolRequest.Builder()
        .withName(symbol.split(" ")[0].substring(7))
        .withPluginName(extractPluginNameFromIconSrc(symbol))
        .withClasses("icon-md")
        .build()
      );
    }
    LOGGER.log(Level.INFO, "Not a Symbol or SVG");
    return null;
  }

  @Exported
  public String getIconUrl() {
    if (logo instanceof ImageLogo) {
      LOGGER.log(Level.INFO, "Serving icon {0}", ((ImageLogo) logo).getLogoUrl());
      return ((ImageLogo) logo).getLogoUrl();
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
  @Extension
  public static class DescriptorImpl extends Descriptor<AppNavLink> {
    @Override
    @NonNull
    public String getDisplayName()
    {
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
