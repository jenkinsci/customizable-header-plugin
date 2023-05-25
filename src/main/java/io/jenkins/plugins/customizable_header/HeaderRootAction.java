package io.jenkins.plugins.customizable_header;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;

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

}
