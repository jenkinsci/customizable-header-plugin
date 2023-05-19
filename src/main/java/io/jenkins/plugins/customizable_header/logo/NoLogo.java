package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class NoLogo extends Logo {

    @DataBoundConstructor
    public NoLogo() {
    }

    @Extension
    @Symbol("nologo")
    public static class DescriptorImpl extends LogoDescriptor {
        @NonNull
        @Override
        public String getDisplayName() {
            return "No Logo";
        }
    }

}
