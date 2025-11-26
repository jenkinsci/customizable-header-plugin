package io.jenkins.plugins.customizable_header;

import static org.hamcrest.MatcherAssert.assertThat;

import hudson.ExtensionList;
import io.jenkins.plugins.customizable_header.logo.ImageLogo;
import java.io.IOException;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.jvnet.hudson.test.recipes.LocalData;
import org.xml.sax.SAXException;

@WithJenkins
public class CspTest {
    @Test
    void testBasicConfiguration(JenkinsRule j) throws IOException, SAXException {
        final CustomHeaderConfiguration configuration = ExtensionList.lookupSingleton(CustomHeaderConfiguration.class);
        configuration.setLogo(new ImageLogo("https://example.com/logo.png"));
        configuration.save();

        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            final HtmlPage htmlPage = wc.goTo("");
            assertThat(htmlPage.getWebResponse().getResponseHeaderValue("Content-Security-Policy"),
                    org.hamcrest.Matchers.containsString("img-src 'self' data: https://example.com/logo.png"));
        }

        configuration.setLogo(new ImageLogo("https://example.org/logo.svg"));
        configuration.save();

        // New configuration replaced existing CSP entry
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            final HtmlPage htmlPage = wc.goTo("");
            assertThat(htmlPage.getWebResponse().getResponseHeaderValue("Content-Security-Policy"),
                    org.hamcrest.Matchers.containsString("img-src 'self' data: https://example.org/logo.svg"));
        }
    }

    @Test
    @LocalData
    void testExistingConfiguration(JenkinsRule j) throws IOException, SAXException {
        try (JenkinsRule.WebClient wc = j.createWebClient()) {
            final HtmlPage htmlPage = wc.goTo("");
            assertThat(htmlPage.getWebResponse().getResponseHeaderValue("Content-Security-Policy"),
                    org.hamcrest.Matchers.containsString("img-src 'self' data: https://example.com/logo.png"));
        }
    }
}
