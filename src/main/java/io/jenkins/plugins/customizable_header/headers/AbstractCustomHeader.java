package io.jenkins.plugins.customizable_header.headers;

import io.jenkins.plugins.customizable_header.AbstractLink;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.SystemMessage;
import io.jenkins.plugins.customizable_header.logo.Logo;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import jenkins.model.Jenkins;
import jenkins.views.PartialHeader;

public abstract class AbstractCustomHeader extends PartialHeader {

  @Override
  public int getSupportedHeaderVersion() {
    return 2;
  }

  public Logo getLogo() {
    return CustomHeaderConfiguration.get().getActiveLogo();
  }

  public String getLogoText() {
    return CustomHeaderConfiguration.get().getLogoText();
  }

  public String getTitle() {
    StringWriter writer = new StringWriter();
    try {
      Jenkins.get().getMarkupFormatter().translate(CustomHeaderConfiguration.get().getTitle(), writer);
      return writer.toString();
    } catch (IOException e) {
      return "";
    }  }

  public boolean hasLinks() {
    return CustomHeaderConfiguration.get().hasLinks();
  }

  public  List<AbstractLink> getLinks() {
    return CustomHeaderConfiguration.get().getLinks();
  }

  public List<SystemMessage> getSystemMessages() {
    return CustomHeaderConfiguration.get().getSystemMessages();
  }
}
