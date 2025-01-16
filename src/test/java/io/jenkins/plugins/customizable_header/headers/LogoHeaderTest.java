package io.jenkins.plugins.customizable_header.headers;

import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class LogoHeaderTest extends AbstractLogoHeaderTest {

  @Override
  LogoHeader getHeader() {
    return new LogoHeader();
  }
}
