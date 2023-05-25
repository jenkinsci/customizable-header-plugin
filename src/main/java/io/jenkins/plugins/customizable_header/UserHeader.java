package io.jenkins.plugins.customizable_header;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import io.jenkins.plugins.customizable_header.color.HeaderColor;
import io.jenkins.plugins.customizable_header.headers.HeaderSelector;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class UserHeader extends UserProperty {

    private final boolean overwriteHeader;

    private final boolean overwriteColors;
    private HeaderColor headerColor;

    private HeaderSelector headerSelector;

    @DataBoundConstructor
    public UserHeader(boolean overwriteHeader, boolean overwriteColors) {
        this.overwriteHeader = overwriteHeader;
        this.overwriteColors = overwriteColors;
    }

    public boolean isOverwriteColors() {
        return overwriteColors;
    }

    @DataBoundSetter
    public void setHeaderColor(HeaderColor headerColor) {
        this.headerColor = headerColor;
    }

    @DataBoundSetter
    public void setHeaderSelector(HeaderSelector headerSelector) {
        this.headerSelector = headerSelector;
    }

    public HeaderColor getHeaderColor() {
        return headerColor;
    }

    public HeaderSelector getHeaderSelector() {
        return headerSelector;
    }

    public boolean isOverwriteHeader() {
        return overwriteHeader;
    }

    @Extension
    @Symbol("customHeader")
    public static class DescriptorImpl extends UserPropertyDescriptor {

        @Override
        public UserProperty newInstance(User user) {
            UserHeader userHeader = new UserHeader(false, false);
            HeaderColor globalHeaderColor = CustomHeaderConfiguration.get().getHeaderColor();
            userHeader.setHeaderColor(new HeaderColor(globalHeaderColor));
            return userHeader;
        }

        @Override
        public boolean isEnabled() {
            return CustomHeaderConfiguration.get().isEnabled();
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Customizable Header";
        }
    }
}
