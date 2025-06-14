package io.jenkins.plugins.customizable_header.headers;

import hudson.model.TopLevelItem;
import hudson.model.User;
import io.jenkins.plugins.customizable_header.AbstractLink;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.SystemMessage;
import io.jenkins.plugins.customizable_header.UserHeader;
import io.jenkins.plugins.customizable_header.logo.Logo;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jenkins.model.Jenkins;
import jenkins.views.PartialHeader;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.Stapler;

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

  public String getItemContextFullName() {
    List<Ancestor> ancestors = new ArrayList<>(Stapler.getCurrentRequest2().getAncestors());
    Collections.reverse(ancestors);
    for (Ancestor ancestor: ancestors) {
      Object obj = ancestor.getObject();
      if (obj instanceof TopLevelItem item) {
        return item.getFullName();
      }
    }
    return null;
  }

  public List<SystemMessage> getSystemMessages() {
    return CustomHeaderConfiguration.get().getSystemMessages();
  }

  public boolean isClassicSearch() {
    User user = User.current();
    if (user != null) {
      UserHeader userHeader = user.getProperty(UserHeader.class);
      if (userHeader != null) {
        return userHeader.isClassicSearch();
      }
    }

    return false;
  }
}
