package io.jenkins.plugins.customizable_header.headers;

import io.jenkins.plugins.customizable_header.AbstractLink;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.SystemMessage;
import io.jenkins.plugins.customizable_header.logo.Logo;
import java.util.List;
import jenkins.views.FullHeader;

public abstract class AbstractCustomHeader extends FullHeader {

  public boolean isCompatible() {
    return true;
  }

  public Logo getLogo() {
    return CustomHeaderConfiguration.get().getActiveLogo();
  }

  public String getLogoText() {
    return CustomHeaderConfiguration.get().getLogoText();
  }

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
