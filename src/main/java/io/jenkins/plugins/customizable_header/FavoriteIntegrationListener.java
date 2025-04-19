package io.jenkins.plugins.customizable_header;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.User;
import hudson.plugins.favorite.Favorites;
import hudson.plugins.favorite.listener.FavoriteListener;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension(optional = true)
public class FavoriteIntegrationListener extends FavoriteListener {
    
    private static final Logger LOGGER = Logger.getLogger(FavoriteIntegrationListener.class.getName());
    private static final String FAVORITES_ID = "favorites-nav";
    
    @Override
    public void onAddFavourite(Item item, User user) {
        updateFavoritesNavLink(user, true);
        // Trigger the favorite status refresh event for UI
        triggerAppNavRefresh();
    }
    
    @Override
    public void onRemoveFavourite(Item item, User user) {
        // Check if the user has any favorites left
        boolean hasFavorites = !isEmpty(Favorites.getFavorites(user));
        updateFavoritesNavLink(user, hasFavorites);
        // Trigger the favorite status refresh event for UI
        triggerAppNavRefresh();
    }
    
    private void updateFavoritesNavLink(User user, boolean hasFavorites) {
        try {
            // Get user header
            UserHeader headerProp = user.getProperty(UserHeader.class);
            if (headerProp == null) {
                return;
            }
            
            List<AbstractLink> links = headerProp.getLinks();
            
            // Find existing favorites link
            AppNavLink favoritesLink = null;
            for (AbstractLink link : links) {
                if (link instanceof AppNavLink && FAVORITES_ID.equals(((AppNavLink)link).getId())) {
                    favoritesLink = (AppNavLink) link;
                    break;
                }
            }
            
            if (hasFavorites) {
                // Add favorites link if it doesn't exist and there are no other links
                if (favoritesLink == null && links.isEmpty()) {
                    AppNavLink newLink = createFavoritesLink(user);
                    List<AbstractLink> newLinks = new ArrayList<>();
                    newLinks.add(newLink);
                    headerProp.setLinks(newLinks);
                    user.save();
                    LOGGER.fine("Added favorites link for user: " + user.getId());
                }
            } else {
                // Remove favorites link if it exists and it's the only link
                if (favoritesLink != null && links.size() == 1) {
                    headerProp.setLinks(new ArrayList<>());
                    user.save();
                    LOGGER.fine("Removed favorites link for user: " + user.getId());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to update favorites link for user: " + user.getId(), e);
        }
    }
    
    /**
     * Triggers a refresh of the app-nav button visibility in the UI
     * Called after favorite status changes to update the UI in real-time
     */
    private void triggerAppNavRefresh() {
        try {
            // Use Jenkins.getInstance() to get the Jenkins instance
            Jenkins jenkins = Jenkins.get();
            if (jenkins != null) {
                // Notify the frontend about the favorite status change
                jenkins.getExtensionList(HeaderRootAction.class)
                    .get(0)
                    .notifyFavoriteStatusChanged();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to trigger app-nav refresh after favorite change", e);
        }
    }
    
    private AppNavLink createFavoritesLink(User user) {
        // Create a new App Nav Link for favorites
        AppNavLink link = new AppNavLink(
            "user/" + user.getId() + "/favorites", 
            "Favorites",
            new io.jenkins.plugins.customizable_header.logo.Symbol("symbol-star")
        );
        link.setId(FAVORITES_ID);
        return link;
    }
    
    private boolean isEmpty(Iterable<?> iterable) {
        return !iterable.iterator().hasNext();
    }
}