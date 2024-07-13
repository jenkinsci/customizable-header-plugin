package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.BulkChange;
import hudson.Extension;
import hudson.Util;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import io.jenkins.plugins.customizable_header.color.HeaderColor;
import io.jenkins.plugins.customizable_header.headers.HeaderSelector;
import io.jenkins.plugins.customizable_header.headers.JenkinsHeaderSelector;
import io.jenkins.plugins.customizable_header.logo.Icon;
import io.jenkins.plugins.customizable_header.logo.Logo;
import io.jenkins.plugins.customizable_header.logo.LogoDescriptor;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jenkins.appearance.AppearanceCategory;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

@Extension
@org.jenkinsci.Symbol("customHeader")
public class CustomHeaderConfiguration extends GlobalConfiguration {

  private static final Logger LOGGER = Logger.getLogger(CustomHeaderConfiguration.class.getName());
  private String title = "";

  private String cssResource;

  private String logoText = "Jenkins";

  private Logo logo = new Symbol("symbol-jenkins");

  private HeaderSelector header = new JenkinsHeaderSelector();

  private boolean enabled = true;

  private transient String cssResourceUrl;

  private HeaderColor headerColor = new HeaderColor("black", "grey", "white");

  private boolean thinHeader;

  private transient SystemMessage systemMessage;

  private final List<SystemMessage> systemMessages = new ArrayList<>();

  private List<AppNavLink> links = new ArrayList<>();

  private static final transient Symbol star = new Symbol("symbol-star plugin-ionicons-api");

  @DataBoundConstructor
  public CustomHeaderConfiguration() {
    load();
  }

  @NonNull
  @Override
  public GlobalConfigurationCategory getCategory() {
    return GlobalConfigurationCategory.get(AppearanceCategory.class);
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
    boolean result = false;
    try (BulkChange bc = new BulkChange(this)) {
      links.clear();
      synchronized (systemMessages) {
        systemMessages.clear();
        result = super.configure(req, json);
      }
      bc.commit();
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Failed to save " + getConfigFile(), e);
    }
    return result;
  }

  public Object readResolve() {
    if (systemMessage != null) {
      systemMessages.add(systemMessage);
    }
    return this;
  }

  private static List<AppNavLink> getFavorites(User user) {
    String rootUrl = Jenkins.get().getRootUrl();
    Iterable<Item> items = Favorites.getFavorites(user);
    List<AppNavLink> favorites = new ArrayList<>();
    items.forEach(
        item -> {
          AppNavLink fav = new AppNavLink(rootUrl + item.getUrl(), item.getFullDisplayName(), star);
          fav.setColor("jenkins-!-color-yellow");
          favorites.add(fav);
        });
    Collections.sort(favorites);
    return favorites;
  }

  public List<AppNavLink> getFavorites() {
    if (Jenkins.get().getPlugin("favorite") != null) {
      User user = User.current();
      if (user != null) {
        return getFavorites(user);
      }
    }
    return Collections.emptyList();
  }

  @Deprecated
  public SystemMessage getSystemMessage() {
    return systemMessage;
  }

  @Deprecated
  @DataBoundSetter
  public void setSystemMessage(SystemMessage systemMessage) {
    this.systemMessage = systemMessage;
  }

  public List<SystemMessage> getSystemMessages() {
    synchronized (systemMessages) {
      Set<String> currentUids = systemMessages.stream().map(SystemMessage::getUid).collect(Collectors.toSet());
      User user = User.current();
      if (user != null) {
        UserHeader userHeader = user.getProperty(UserHeader.class);
        if (userHeader != null) {
          if (userHeader.getDismissedMessages().removeIf(dismissedUid -> !currentUids.contains(dismissedUid))) {
            try {
              user.save();
            } catch (IOException e) {
              LOGGER.log(Level.WARNING, "Failed to save user properties", e);
            }
          };
        }
      }
      if (systemMessages.removeIf(sm -> sm.isExpired() || sm.getMessage() == null)) {
        save();
      }
    }
    return Collections.unmodifiableList(systemMessages);
  }

  @DataBoundSetter
  public void setSystemMessages(List<SystemMessage> systemMessages) {
    synchronized (this.systemMessages) {
      this.systemMessages.clear();
      this.systemMessages.addAll(systemMessages);
    }
    save();
  }

  public void addSystemMessage(SystemMessage message) {
    synchronized (systemMessages) {
      systemMessages.add(message);
    }
    save();
  }

  public void deleteSystemMessage(String id) {
    synchronized (systemMessages) {
      systemMessages.removeIf(sm -> sm.getUid().equals(id));
    }
    save();
  }

  public List<AppNavLink> getLinks() {
    return links;
  }

  @DataBoundSetter
  public void setLinks(List<AppNavLink> links) {
    this.links = links;
    save();
  }

  private boolean hasFavorites() {
    if (Jenkins.get().getPlugin("favorite") != null) {
      User user = User.current();
      if (user != null) {
        Iterable<Item> items = Favorites.getFavorites(user);
        return items.iterator().hasNext();
      }
    }
    return false;
  }

  private boolean hasAppLinks() {
    if (links == null) {
      return false;
    }
    return links.size() != 0;
  }

  private boolean hasUserLinks() {
    User user = User.current();
    if (user != null) {
      UserHeader userHeader = user.getProperty(UserHeader.class);
      return userHeader.getLinks() != null && userHeader.getLinks().size() != 0;
    }
    return false;
  }

  public boolean hasLinks() {
    return hasFavorites() || hasAppLinks() || hasUserLinks();
  }

  public List<AppNavLink> getUserLinks() {
    User user = User.current();
    List<AppNavLink> links = null;
    if (user != null) {
      UserHeader userHeader = user.getProperty(UserHeader.class);
      links = userHeader.getLinks();
    }
    if (links != null) {
      return links;
    }
    return Collections.emptyList();
  }

  public boolean isThinHeader() {
    return thinHeader;
  }

  @DataBoundSetter
  public void setThinHeader(boolean thinHeader) {
    this.thinHeader = thinHeader;
    save();
  }

  @DataBoundSetter
  public void setHeader(HeaderSelector header) {
    this.header = header;
    save();
  }

  public HeaderSelector getHeader() {
    return header;
  }

  @DataBoundSetter
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    save();
  }

  public boolean isEnabled() {
    return enabled;
  }

  @DataBoundSetter
  public void setLogo(Logo logo) {
    this.logo = logo;
    save();
  }

  public Logo getLogo() {
    return logo;
  }

  @DataBoundSetter
  public void setHeaderColor(HeaderColor headerColor) {
    this.headerColor = headerColor;
    save();
  }

  /**
   * The globally configured Headercolor.
   *
   * @return global headercolor.
   */
  public HeaderColor getHeaderColor() {
    return headerColor;
  }

  /**
   * The active header color. If the user has overwritten the colors those colors are used.
   *
   * @return
   */
  public HeaderColor getActiveHeaderColor() {
    User user = User.current();
    if (user != null) {
      UserHeader userHeader = user.getProperty(UserHeader.class);
      if (userHeader != null && userHeader.isOverwriteColors()) {
        return userHeader.getHeaderColor();
      }
    }
    return headerColor;
  }

  public HeaderSelector getActiveHeader() {
    if (enabled) {
      User user = User.current();
      if (user != null) {
        UserHeader userHeader = user.getProperty(UserHeader.class);
        if (userHeader != null && userHeader.isOverwriteHeader()) {
          return userHeader.getHeaderSelector();
        }
      }
      return header;
    }
    return new JenkinsHeaderSelector();
  }

  @DataBoundSetter
  public void setLogoText(String logoText) {
    this.logoText = logoText;
    save();
  }

  public String getLogoText() {
    return logoText;
  }

  public static CustomHeaderConfiguration get() {
    return GlobalConfiguration.all().get(CustomHeaderConfiguration.class);
  }

  public String getTitle() {
    return title;
  }

  @DataBoundSetter
  public void setTitle(String title) {
    this.title = title;
    save();
  }

  public String getCssResource() {
    return Util.fixEmptyAndTrim(cssResource);
  }

  @DataBoundSetter
  public void setCssResource(String cssResource) {
    this.cssResource = cssResource;
    setCssResourceUrl();
    save();
  }

  private void setCssResourceUrl() {
    if (Util.fixEmptyAndTrim(cssResource) != null) {
      try {
        URI uri = URI.create(cssResource);
        if (uri.isAbsolute()) {
          cssResourceUrl = cssResource;
        } else {
          cssResourceUrl = Jenkins.get().getRootUrl() + cssResource;
        }
      } catch (IllegalArgumentException iae) {
        LOGGER.log(Level.WARNING, "The given css resource is not a valid uri", iae);
        cssResourceUrl = "";
      }
    } else {
      cssResourceUrl = "";
    }
  }

  public String getCssResourceUrl() {
    if (cssResourceUrl == null) {
      setCssResourceUrl();
    }
    return cssResourceUrl;
  }

  public Logo defaultLogo() {
    return new Symbol("symbol-jenkins");
  }

  public List<Descriptor<Logo>> getLogoDescriptors() {
    return LogoDescriptor.all().stream()
        .filter(d -> !(d instanceof Icon.DescriptorImpl))
        .collect(Collectors.toList());
  }
}
