package io.jenkins.plugins.customizable_header.headers;

import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ContextAwareHeaderTest extends AbstractLogoHeaderTest {

  @Override
  LogoHeader getHeader() {
    return new ContextAwareHeader();
  }
}
