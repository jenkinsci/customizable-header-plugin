package io.jenkins.plugins.customizable_header.headers;

import hudson.Extension;
import hudson.markup.RawHtmlMarkupFormatter;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.logo.Logo;
import java.io.IOException;
import java.io.StringWriter;
import jenkins.views.PartialHeader;

@Extension(ordinal = 99999)
public class LogoHeader extends PartialHeader implements SystemMessageProvider {

  @Override
  public boolean isEnabled() {
    return CustomHeaderConfiguration.get().getActiveHeader() instanceof LogoSelector;
  }

  public String getTitle() {
    StringWriter writer = new StringWriter();
    try {
      RawHtmlMarkupFormatter.INSTANCE.translate(CustomHeaderConfiguration.get().getTitle(), writer);
      return writer.toString();
    } catch (IOException e) {
      return "";
    }
  }

  public String getLogoText() {
    return CustomHeaderConfiguration.get().getLogoText();
  }

  public boolean hasLinks() {
    return CustomHeaderConfiguration.get().hasLinks();
  }

  public Logo getLogo() {
    return CustomHeaderConfiguration.get().getLogo();
  }

  @Override
  public int getSupportedHeaderVersion() {
    return 1;
  }
}
