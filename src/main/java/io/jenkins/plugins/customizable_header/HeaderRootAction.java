package io.jenkins.plugins.customizable_header;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import hudson.model.User;
import io.jenkins.plugins.customizable_header.headers.LogoSelector;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

  public String getBackgroundColor() {
    return CustomHeaderConfiguration.get().getActiveHeaderColor().getBackgroundColor();
  }

  public String getHoverColor() {
    return CustomHeaderConfiguration.get().getActiveHeaderColor().getHoverColor();
  }

  public String getColor() {
    return CustomHeaderConfiguration.get().getActiveHeaderColor().getColor();
  }

  public boolean isThinHeader() {
    return CustomHeaderConfiguration.get().isThinHeader() && CustomHeaderConfiguration.get().getHeader() instanceof LogoSelector;
  }

  public boolean hasLinks() {
    return CustomHeaderConfiguration.get().hasLinks();
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

  @ExportedBean
  static class Links implements HttpResponse {
    @Exported(inline = true)
    public List<AbstractLink> getLinks() {
      List<AbstractLink> links = new ArrayList<>(CustomHeaderConfiguration.get().getLinks());
      List<AbstractLink> userLinks = CustomHeaderConfiguration.get().getUserLinks();
      if (!userLinks.isEmpty()) {
        if (!links.isEmpty() && !(links.get(links.size() - 1) instanceof LinkSeparator) && !(userLinks.get(0) instanceof LinkSeparator)) {
          links.add(new LinkSeparator());
        }
        links.addAll(userLinks);
      }
      List<AppNavLink> favorites = CustomHeaderConfiguration.get().getFavorites();
      if (!favorites.isEmpty()) {
        if (!links.isEmpty() && !(links.get(links.size() - 1) instanceof LinkSeparator)) {
          links.add(new LinkSeparator());
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
  public Links doGetLinks() throws Exception {
    return new Links();
  }
}
