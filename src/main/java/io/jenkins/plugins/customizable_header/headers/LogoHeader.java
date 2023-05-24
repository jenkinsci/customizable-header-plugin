package io.jenkins.plugins.customizable_header.headers;

import hudson.Extension;
import hudson.markup.RawHtmlMarkupFormatter;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.logo.Logo;
import jenkins.views.PartialHeader;

import java.io.IOException;
import java.io.StringWriter;

@Extension
public class LogoHeader extends PartialHeader {

  @Override
  public boolean isEnabled() {
    return CustomHeaderConfiguration.getInstance().getHeader() instanceof LogoSelector;
  }

  public String getTitle() {
    StringWriter writer = new StringWriter();
    try {
      RawHtmlMarkupFormatter.INSTANCE.translate(CustomHeaderConfiguration.getInstance().getTitle(), writer);
      return writer.toString();
    } catch (IOException e) {
      return "";
    }
  }

  public String getLogoText() {
    return CustomHeaderConfiguration.getInstance().getLogoText();
  }

  public String getCssResourceUrl() {

    return CustomHeaderConfiguration.getInstance().getCssResourceUrl();
  }

  public Logo getLogo() {
    return CustomHeaderConfiguration.getInstance().getLogo();
  }

  @Override
  public int getSupportedHeaderVersion() {
    return 1;
  }
}
