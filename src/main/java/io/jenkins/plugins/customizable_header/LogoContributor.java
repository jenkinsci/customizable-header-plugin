package io.jenkins.plugins.customizable_header;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.User;
import io.jenkins.plugins.customizable_header.links.FolderLinks;
import io.jenkins.plugins.customizable_header.links.JobLinks;
import io.jenkins.plugins.customizable_header.logo.ImageLogo;
import io.jenkins.plugins.customizable_header.logo.Logo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import jenkins.security.csp.Contributor;
import jenkins.security.csp.CspBuilder;
import jenkins.security.csp.Directive;
import org.kohsuke.accmod.restrictions.suppressions.SuppressRestrictedWarnings;

/**
 * Add the image logo URLs to CSP. This is permission-aware, i.e., you only get
 * added to your CSP header the logos of the links you have access to.
 */
@SuppressRestrictedWarnings({Contributor.class, CspBuilder.class})
@Extension
public class LogoContributor implements Contributor {

    public static final Logger LOGGER = Logger.getLogger(LogoContributor.class.getName());

    @Override
    public void apply(CspBuilder csp) {
        Jenkins.get().allItems(AbstractFolder.class).forEach(f -> {
            final FolderLinks linksProperty = (FolderLinks) f.getProperties().get(FolderLinks.class);
            if (linksProperty == null) {
                return;
            }
            final List<AbstractLink> links = linksProperty.getLinks();
            allowLinks(csp, links);
        });

        Jenkins.get().allItems(Job.class).forEach(j -> {
            final JobProperty<?> property = j.getProperty(JobLinks.class);
            if (property == null) {
                return;
            }
            final List<AbstractLink> links = ((JobLinks) property).getLinks();
            allowLinks(csp, links);
        });

        User current = User.current();
        if (current != null) {
            final UserHeader userHeader = current.getProperty(UserHeader.class);
            if (userHeader != null) {
                final List<AbstractLink> links = userHeader.getLinks();
                allowLinks(csp, links);
            }
        }

        final CustomHeaderConfiguration configuration = ExtensionList.lookupSingleton(CustomHeaderConfiguration.class);
        allowLogo(csp, configuration.getActiveLogo());

        if (Jenkins.get().hasPermission(Jenkins.READ)) {
            allowLinks(csp, configuration.getLinks());
        }
    }

    private void allowLinks(CspBuilder csp, List<AbstractLink> links) {
        links.stream().filter(l -> l instanceof AppNavLink).map(l -> (AppNavLink) l).map(AppNavLink::getLogo).forEach(logo -> allowLogo(csp, logo));
    }

    private void allowLogo(CspBuilder csp, Logo logo) {
        String url = mapToLogoUrl(logo);
        if (url != null) {
            csp.add(Directive.IMG_SRC, url);
        }
    }

    private String mapToLogoUrl(Logo logo) {
        if (logo instanceof ImageLogo imageLogo) {
            final String rawUrl = imageLogo.getLogoUrl();
            if (rawUrl == null || rawUrl.isEmpty()) {
                return null;
            }
            try {
                URI uri = new URI(rawUrl);
                if (uri.isAbsolute()) {
                    return uri.toASCIIString();
                }
                // ImageLogo doesn't support scheme-relative URLs, so ignore that here too
            } catch (URISyntaxException ex) {
                LOGGER.log(Level.FINE, "Failed to parse URI: " + rawUrl, ex);
            }
        }
        return null;
    }
}
