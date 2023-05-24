package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.net.URI;

public class ImageLogo extends Logo {

    private String logoUrl;

    @DataBoundConstructor
    public ImageLogo(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getUrl() {
        URI uri = URI.create(logoUrl);
        if (!uri.isAbsolute()) {
            uri = URI.create(Jenkins.get().getRootUrl() + logoUrl);
        }
        return uri.toString();
    }

    @Extension
    @Symbol("image")
    public static class DescriptorImpl extends LogoDescriptor {
        @NonNull
        @Override
        public String getDisplayName() {
            return "Image";
        }
    }

}
