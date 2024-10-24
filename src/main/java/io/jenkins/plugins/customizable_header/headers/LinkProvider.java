package io.jenkins.plugins.customizable_header.headers;

import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;

public interface LinkProvider {
  default boolean hasLinks() {
    return CustomHeaderConfiguration.get().hasLinks();
  }
}
