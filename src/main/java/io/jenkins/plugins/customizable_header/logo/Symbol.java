package io.jenkins.plugins.customizable_header.logo;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;

public class Symbol extends Logo {

    private String symbol = "symbol-jenkins";

    @DataBoundConstructor
    public Symbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSize() {
        if (CustomHeaderConfiguration.get().isThinHeader()) {
            return "icon-lg";
        }
        return "icon-xlg";
    }

    @Extension
    @org.jenkinsci.Symbol("symbol")
    public static class DescriptorImpl extends LogoDescriptor {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Symbol";
        }
    }
}
