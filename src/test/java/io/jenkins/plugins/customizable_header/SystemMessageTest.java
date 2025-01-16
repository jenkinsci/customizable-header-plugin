package io.jenkins.plugins.customizable_header;

import static io.jenkins.plugins.customizable_header.SystemMessage.DATE_INPUT_FORMATTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.markup.MarkupFormatter;
import hudson.markup.RawHtmlMarkupFormatter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class SystemMessageTest {

@Test
void escapedMessage(@SuppressWarnings("unused") JenkinsRule r) {
  // save message
  SystemMessage saveMessage = new SystemMessage("Save message", SystemMessage.SystemMessageColor.success, null);
  assertEquals("Save message", saveMessage.getEscapedMessage());

  // expired message
  SystemMessage expiredMessage = new SystemMessage("Expired message", SystemMessage.SystemMessageColor.warning, null);
  expiredMessage.setExpireDate(LocalDateTime.now().minusHours(1).format(DATE_INPUT_FORMATTER));
  assertEquals("", expiredMessage.getEscapedMessage());

  // dangerous message with global formatter
  SystemMessage dangerousMessage  = new SystemMessage("<script>alert('PWND!')</script>", SystemMessage.SystemMessageColor.danger, null);
  assertEquals("&lt;script&gt;alert(&#039;PWND!&#039;)&lt;/script&gt;", dangerousMessage.getEscapedMessage());

  // dangerous message with OWASP formatter
  r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
  assertEquals("", dangerousMessage.getEscapedMessage());

  // save message with broken formatter
  MarkupFormatter formatter = new MarkupFormatter() {
  @Override
  public void translate(String markup, @NonNull Writer output) throws IOException {
    throw new IOException("Oh no!");
  }
  };
  r.jenkins.setMarkupFormatter(formatter);
  assertEquals("", saveMessage.getEscapedMessage());
}

}
