package io.jenkins.plugins.customizable_header;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;

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
        return CustomHeaderConfiguration.getInstance().getHeaderColor().getBackgroundColor();
    }

    public String getHoverColor() {
        return CustomHeaderConfiguration.getInstance().getHeaderColor().getHoverColor();
    }

    public String getColor() {
        return CustomHeaderConfiguration.getInstance().getHeaderColor().getColor();
    }

}
