package io.jenkins.plugins.customizable_header.headers;

import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.SystemMessage;
import java.util.List;

public interface SystemMessageProvider {
  default List<SystemMessage> getSystemMessages() {
    return CustomHeaderConfiguration.get().getSystemMessages();
  }
}
