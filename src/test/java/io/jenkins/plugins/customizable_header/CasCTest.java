package io.jenkins.plugins.customizable_header;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import io.jenkins.plugins.casc.misc.ConfiguredWithCode;
import io.jenkins.plugins.casc.misc.JenkinsConfiguredWithCodeRule;
import io.jenkins.plugins.casc.misc.junit.jupiter.WithJenkinsConfiguredWithCode;
import io.jenkins.plugins.customizable_header.headers.JenkinsWrapperHeaderSelector;
import io.jenkins.plugins.customizable_header.headers.SectionedHeaderSelector;
import io.jenkins.plugins.customizable_header.logo.SvgLogo;
import io.jenkins.plugins.customizable_header.logo.Symbol;
import org.junit.jupiter.api.Test;

@WithJenkinsConfiguredWithCode
class CasCTest {

  @Test
  @ConfiguredWithCode("casc-old-logo-header.yaml")
  void loadOldLogoHeader(JenkinsConfiguredWithCodeRule jcwcRule) {
    CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
    assertThat(config.getHeader(), instanceOf(SectionedHeaderSelector.class));
    assertThat(config.isEnabled(), is(true));
    assertThat(config.isThinHeader(), is(true));
    assertThat(config.getContextAwareLogo(), is(nullValue()));
  }

  @Test
  @ConfiguredWithCode("casc-old-context-aware-header.yaml")
  void loadOldContextAwareHeader(JenkinsConfiguredWithCodeRule jcwcRule) {
    CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
    assertThat(config.getHeader(), instanceOf(SectionedHeaderSelector.class));
    assertThat(config.isEnabled(), is(true));
    assertThat(config.isThinHeader(), is(true));
    assertThat(config.getContextAwareLogo().getShowFolderWeather(), is(true));
    assertThat(config.getContextAwareLogo().getShowJobWeather(), is(true));
  }

  @Test
  @ConfiguredWithCode("casc-old-jenkins-header.yaml")
  void loadOldJenkinsHeader(JenkinsConfiguredWithCodeRule jcwcRule) {
    CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
    assertThat(config.getHeader(), instanceOf(JenkinsWrapperHeaderSelector.class));
    assertThat(config.isEnabled(), is(true));
  }

  @Test
  @ConfiguredWithCode("casc-jenkins-header.yaml")
  void loadJenkinsHeader(JenkinsConfiguredWithCodeRule jcwcRule) {
    CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
    assertThat(config.getHeader(), instanceOf(JenkinsWrapperHeaderSelector.class));
    assertThat(config.isEnabled(), is(true));
    assertThat(config.getLogoText(), is("Jenkins"));
    assertThat(config.getTitle(), is("CasC"));
    assertThat(config.isThinHeader(), is(true));
    assertThat(config.getHeaderColor().getColor(), is("var(--white)"));
    assertThat(config.getHeaderColor().getBackgroundColor(), is("linear-gradient(90deg, rgba(28,99,190,1) 0%, rgba(53,224,192,1) 100%);"));
    assertThat(config.getLogo(), instanceOf(SvgLogo.class));
    assertThat(((SvgLogo) config.getLogo()).getLogoPath(), is("userContent/logo.svg"));
  }

  @Test
  @ConfiguredWithCode("casc-sectioned-header.yaml")
  void loadSectionedHeader(JenkinsConfiguredWithCodeRule jcwcRule) {
    CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
    assertThat(config.getHeader(), instanceOf(SectionedHeaderSelector.class));
    assertThat(config.isEnabled(), is(true));
    assertThat(config.getLogoText(), is("Jenkins"));
    assertThat(config.getTitle(), is("CasC"));
    assertThat(config.isThinHeader(), is(true));
  }

  @Test
  @ConfiguredWithCode("casc-sectioned-header.yaml")
  void loadLinks(JenkinsConfiguredWithCodeRule jcwcRule) {
    CustomHeaderConfiguration config = CustomHeaderConfiguration.get();
    assertThat(config.getLinks(), is(notNullValue()));
    assertThat(config.getLinks().size(), is(3));

    assertThat(config.getLinks().get(0), instanceOf(AppNavLink.class));
    assertThat(((AppNavLink) config.getLinks().get(0)).getLabel(), is("Google"));
    assertThat(((AppNavLink) config.getLinks().get(0)).getUrl(), is("https://google.com"));
    assertThat(((AppNavLink) config.getLinks().get(0)).getExternal(), is(false));
    assertThat(((AppNavLink) config.getLinks().get(0)).getLogo(), instanceOf(SvgLogo.class));

    assertThat(config.getLinks().get(1), instanceOf(LinkSeparator.class));
    assertThat(((LinkSeparator) config.getLinks().get(1)).getTitle(), is("External"));

    assertThat(config.getLinks().get(2), instanceOf(AppNavLink.class));
    assertThat(((AppNavLink) config.getLinks().get(2)).getLabel(), is("Youtube"));
    assertThat(((AppNavLink) config.getLinks().get(2)).getUrl(), is("https://youtube.com"));
    assertThat(((AppNavLink) config.getLinks().get(2)).getExternal(), is(true));
    assertThat(((AppNavLink) config.getLinks().get(2)).getLogo(), instanceOf(Symbol.class));
  }

}
