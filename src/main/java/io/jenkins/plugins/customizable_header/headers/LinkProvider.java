package io.jenkins.plugins.customizable_header.headers;

import io.jenkins.plugins.customizable_header.AbstractLink;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import java.util.List;

public interface LinkProvider {
  default boolean hasLinks() {
    return CustomHeaderConfiguration.get().hasLinks();
  }

  default List<AbstractLink> getLinks() {
    return CustomHeaderConfiguration.get().getLinks();
  }
}
