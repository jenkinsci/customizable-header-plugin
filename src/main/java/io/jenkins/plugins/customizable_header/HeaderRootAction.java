package io.jenkins.plugins.customizable_header;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import org.kohsuke.stapler.export.Flavor;
import org.kohsuke.stapler.verb.GET;

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
        return CustomHeaderConfiguration.get().isThinHeader();
    }

    public boolean hasLinks() {
        return CustomHeaderConfiguration.get().hasLinks();
    }

    @ExportedBean
    static class Links implements HttpResponse {
        @Exported(inline = true)
        public List<AppNavLink> getItems() {
            return CustomHeaderConfiguration.get().getLinks();
        }

        @Override
        public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object o) throws IOException, ServletException {
            rsp.serveExposedBean(req, this, Flavor.JSON);
        }
    }

    @GET
    public Links doGetLinks(StaplerRequest request, StaplerResponse response) throws Exception {
        return new Links();
    }

}
