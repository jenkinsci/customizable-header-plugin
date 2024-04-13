package io.jenkins.plugins.customizable_header.headers;

import hudson.markup.RawHtmlMarkupFormatter;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import io.jenkins.plugins.customizable_header.SystemMessage;
import java.io.IOException;
import java.io.StringWriter;

public interface SystemMessageProvider {
  default String getSystemMessage() {
    SystemMessage systemMessage = CustomHeaderConfiguration.get().getSystemMessage();
    if (systemMessage == null) {
      return "";
    }
    StringWriter writer = new StringWriter();
    try {
      RawHtmlMarkupFormatter.INSTANCE.translate(systemMessage.getMessage(), writer);
      return writer.toString();
    } catch (IOException e) {
      return "";
    }
  }

  default String getSystemMessageColor() {
    SystemMessage systemMessage = CustomHeaderConfiguration.get().getSystemMessage();
    if (systemMessage == null) {
      return "info";
    }
    return systemMessage.getLevel().name();
  }
}
