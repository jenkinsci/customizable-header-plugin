package io.jenkins.plugins.customizable_header.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.markup.MarkupFormatter;
import hudson.markup.RawHtmlMarkupFormatter;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import java.io.IOException;
import java.io.Writer;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
abstract class AbstractLogoHeaderTest {

  abstract LogoHeader getHeader();

  @Test
  void title(@SuppressWarnings("unused") JenkinsRule r) {
    LogoHeader header = getHeader();

    // save title
    CustomHeaderConfiguration.get().setTitle("Save title");
    assertEquals("Save title", header.getTitle());

    // dangerous title with global formatter
    CustomHeaderConfiguration.get().setTitle("<script>alert('PWND!')</script>");
    assertEquals("&lt;script&gt;alert(&#039;PWND!&#039;)&lt;/script&gt;", header.getTitle());

    // dangerous title with OWASP formatter
    r.jenkins.setMarkupFormatter(RawHtmlMarkupFormatter.INSTANCE);
    assertEquals("", header.getTitle());

    // save title with broken formatter
    MarkupFormatter formatter = new MarkupFormatter() {
      @Override
      public void translate(String markup, @NonNull Writer output) throws IOException {
        throw new IOException("Oh no!");
      }
    };
    r.jenkins.setMarkupFormatter(formatter);
    assertEquals("", header.getTitle());
  }
}
