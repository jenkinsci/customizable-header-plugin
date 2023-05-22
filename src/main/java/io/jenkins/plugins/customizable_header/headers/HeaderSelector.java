package io.jenkins.plugins.customizable_header.headers;

import hudson.model.AbstractDescribableImpl;

public class HeaderSelector extends AbstractDescribableImpl<HeaderSelector> {
    @Override
    public HeaderDescriptor getDescriptor() {
        return (HeaderDescriptor) super.getDescriptor();
    }
}
