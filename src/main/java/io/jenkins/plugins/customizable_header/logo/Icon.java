package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;

public class Icon extends Logo {
    private String description;

    private String clazz;

    public Icon(String clazz, String description) {
        this.description = description;
        this.clazz = clazz;
    }

    public String getDescription() {
        return description;
    }

    public String getClazz() {
        return clazz;
    }

    @Extension
    @org.jenkinsci.Symbol("icon")
    public static class DescriptorImpl extends LogoDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Icon";
        }
    }

}
