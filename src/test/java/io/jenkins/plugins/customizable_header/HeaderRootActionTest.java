package io.jenkins.plugins.customizable_header;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jenkins.plugins.customizable_header.logo.ImageLogo;
import org.htmlunit.Page;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.jvnet.hudson.test.recipes.LocalData;

@WithJenkins
public class HeaderRootActionTest {

  private JenkinsRule r;

  @BeforeEach
  void setUp(JenkinsRule r) {
    this.r = r;
    CustomHeaderConfiguration config = r.jenkins.getDescriptorByType(CustomHeaderConfiguration.class);
    ImageLogo imageLogo = new ImageLogo("/userContent/beekeeper.png");
    config.setEnabled(true);
    config.setLogo(imageLogo);
  }

  @Test
  void notAllowedReturns404() throws Exception {
    try (JenkinsRule.WebClient w = r.createWebClient()) {
      HtmlPage page = w.withThrowExceptionOnFailingStatusCode(false).goTo("customizable-header/fetch?u=config.xml");
      assertEquals(404, page.getWebResponse().getStatusCode());
    }
  }

  @Test
  @LocalData
  void canAccessLogo() throws Exception {
    try (JenkinsRule.WebClient w = r.createWebClient()) {
      w.withThrowExceptionOnFailingStatusCode(false).goTo("");
      Page image = w.withThrowExceptionOnFailingStatusCode(false).goTo("customizable-header/fetch?u=%2FuserContent%2Fbeekeeper.png", "image/png");
      assertEquals(200, image.getWebResponse().getStatusCode());
    }
  }
}
