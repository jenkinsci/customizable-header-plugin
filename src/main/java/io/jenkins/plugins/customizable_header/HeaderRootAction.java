package io.jenkins.plugins.customizable_header;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.model.UnprotectedRootAction;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import io.jenkins.plugins.customizable_header.headers.JenkinsWrapperHeaderSelector;
import io.jenkins.plugins.customizable_header.links.FolderLinks;
import io.jenkins.plugins.customizable_header.links.JobLinks;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.kohsuke.stapler.export.ExportConfig;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import org.kohsuke.stapler.export.Flavor;
import org.kohsuke.stapler.verb.GET;
import org.kohsuke.stapler.verb.POST;

/**
 * Allows to access the css for anonymous users.
 */
@Extension
public class HeaderRootAction implements UnprotectedRootAction {

  private static final Symbol star = new Symbol("symbol-star plugin-ionicons-api");

  @Override
  public String getIconFileName() {
    return null;
  }

  @Override
  public String getDisplayName() {
    return null;
  }

  @Override
  public String getUrlName() {
    return "customizable-header";
  }

  public boolean isEnabled() {
    return CustomHeaderConfiguration.get().isEnabled();
  }

  public boolean isJenkinsHeader() {
    return CustomHeaderConfiguration.get().getHeader() instanceof JenkinsWrapperHeaderSelector;
  }

  public String getBackgroundColor() {
    return CustomHeaderConfiguration.get().getActiveHeaderColor().getActiveBackgroundColor();
  }

  public String getColor() {
    return CustomHeaderConfiguration.get().getActiveHeaderColor().getActiveColor();
  }

  public boolean isThinHeader() {
    return  CustomHeaderConfiguration.get().isEnabled() && CustomHeaderConfiguration.get().isThinHeader();
  }

  @POST
  public HttpResponse doAddSystemMessage(@QueryParameter(fixEmpty = true) String message, @QueryParameter(fixEmpty = true) String level,
                                @QueryParameter String expireDate, @QueryParameter(fixEmpty = true) String id, @QueryParameter(fixEmpty = true) Boolean dismissible) throws IOException {
    Jenkins.get().checkPermission(Jenkins.ADMINISTER);
    if (message == null || level == null) {
      throw HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters: message and level are mandatory");
    }
    try {
      SystemMessage.SystemMessageColor lvl = SystemMessage.SystemMessageColor.valueOf(level);
      SystemMessage msg = new SystemMessage(message, lvl, id);
      msg.setExpireDate(expireDate);
      msg.setDismissible(dismissible);
      CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
      config.addSystemMessage(msg);
      return HttpResponses.text(msg.getUid());
    } catch (DateTimeParseException e) {
      throw HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "expireDate is not properly formatted: " + expireDate);
    } catch (IllegalArgumentException e) {
      throw HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Unknown level: " + level);
    }
  }

  @POST
  public void doDeleteSystemMessage(@QueryParameter(fixEmpty = true) String id) throws IOException {
    Jenkins.get().checkPermission(Jenkins.ADMINISTER);
    if (id == null) {
      throw HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters: id is mandatory");
    }
    CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
    config.deleteSystemMessage(id);
  }


  @POST
  public void doDismissMessage(@QueryParameter String uid) throws IOException {
    User user = User.current();
    if (user != null) {
      UserHeader userHeader = user.getProperty(UserHeader.class);
      if (userHeader == null) {
        userHeader = new UserHeader();
        user.addProperty(userHeader);
      }
      userHeader.getDismissedMessages().add(uid);
      user.save();
    }
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
    favorites.sort(new Comparator<AppNavLink>() {
      @Override
      public int compare(AppNavLink o1, AppNavLink o2) {
        int labelCompare = o1.getLabel().compareToIgnoreCase(o2.getLabel());
        if (labelCompare != 0) {
          return labelCompare;
        }
        return o1.getUrl().compareTo(o2.getUrl());
      }
    });
    return favorites;
  }

  private static List<AppNavLink> getFavorites() {
    if (Jenkins.get().getPlugin("favorite") != null) {
      User user = User.current();
      if (user != null) {
        return getFavorites(user);
      }
    }
    return Collections.emptyList();
  }

  public static List<AbstractLink> getUserLinks() {
    User user = User.current();
    List<AbstractLink> links = null;
    if (user != null) {
      UserHeader userHeader = user.getProperty(UserHeader.class);
      if (userHeader != null) {
        links = userHeader.getLinks();
      }
    }
    if (links != null) {
      return links;
    }
    return Collections.emptyList();
  }

  public static List<AbstractLink> getContextLinks(String item) {
    List<AbstractLink> links = new ArrayList<>();
    if (item != null && !item.isEmpty()) {
      TopLevelItem topLevelItem = Jenkins.get().getItemByFullName(item, TopLevelItem.class);
      while (topLevelItem != null) {
        if (topLevelItem instanceof Job<?, ?> job) {
          JobLinks jobLinks = job.getProperty(JobLinks.class);
          if (jobLinks != null) {
            links.addAll(jobLinks.getLinks());
          }
        }
        if (topLevelItem instanceof AbstractFolder<?> folder) {
          for (AbstractFolderProperty<?> prop : folder.getProperties()) {
            if (prop instanceof FolderLinks folderLinks) {
              links.addAll(folderLinks.getLinks());
            }
          }
        }
        ItemGroup<?> parent = topLevelItem.getParent();
        if (parent instanceof TopLevelItem p) {
          topLevelItem = p;
        } else {
          break;
        }
      }
    }
    return links;
  }

  @ExportedBean
  static class Links implements HttpResponse {
    private String item;

    public Links(String item) {
      this.item = item;
    }

    @Exported(inline = true)
    public List<AbstractLink> getLinks() {

      List<AbstractLink> links = new ArrayList<>(CustomHeaderConfiguration.get().getLinks());
      List<AbstractLink> jobLinks = new ArrayList<>(getContextLinks(item));
      links.addAll(jobLinks);
      List<AbstractLink> userLinks = getUserLinks();
      if (!userLinks.isEmpty()) {
        if (!links.isEmpty() && !(links.get(links.size() - 1) instanceof LinkSeparator) && !(userLinks.get(0) instanceof LinkSeparator)) {
          links.add(new LinkSeparator());
        }
        links.addAll(userLinks);
      }
      List<AppNavLink> favorites = getFavorites();
      if (!favorites.isEmpty()) {
        if (!links.isEmpty() && !(links.get(links.size() - 1) instanceof LinkSeparator)) {
          LinkSeparator sep = new LinkSeparator();
          sep.setTitle("Favorites");
          links.add(sep);
        }
        links.addAll(favorites);
      }
      return links;
    }

    @Override
    public void generateResponse(StaplerRequest2 req, StaplerResponse2 rsp, Object o)
        throws IOException, ServletException {
      rsp.serveExposedBean(req, this, new ExportConfig().withFlavor(Flavor.JSON));
    }
  }

  @GET
  public Links doGetLinks(@QueryParameter String item) throws Exception {
    return new Links(item);
  }
}
