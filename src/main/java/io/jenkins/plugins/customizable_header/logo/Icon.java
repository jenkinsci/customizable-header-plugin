package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;

public class Icon extends Logo {
    private String clazz;

    public Icon(String clazz) {
        this.clazz = clazz;
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
