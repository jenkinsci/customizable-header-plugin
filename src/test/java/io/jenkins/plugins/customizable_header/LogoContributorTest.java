package io.jenkins.plugins.customizable_header;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.ExtensionList;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import io.jenkins.plugins.customizable_header.links.FolderLinks;
import io.jenkins.plugins.customizable_header.links.JobLinks;
import io.jenkins.plugins.customizable_header.logo.ImageLogo;
import java.util.List;
import java.util.logging.Level;
import jenkins.model.Jenkins;
import jenkins.security.csp.CspBuilder;
import org.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
public class LogoContributorTest {

    @Test
    void basics(JenkinsRule j) throws Exception {
        final CustomHeaderConfiguration configuration = ExtensionList.lookupSingleton(CustomHeaderConfiguration.class);
        configuration.setEnabled(true);
        configuration.setLogo(new ImageLogo("https://logo.example.com/logo.png"));
        configuration.setLinks(List.of(new AppNavLink("https://example.com", "Example", new ImageLogo("https://link.example.org/logo.png"))));
        final FreeStyleProject freeStyleProject = j.createFreeStyleProject();
        freeStyleProject.addProperty(new JobLinks(List.of(new AppNavLink("https://joblink.example.com", "JobLink", new ImageLogo("https://joblink.example.org/logo.png")))));
        final Folder folder = j.jenkins.createProject(Folder.class, "Folder1");
        folder.addProperty(new FolderLinks(List.of(new AppNavLink("https://folderlink.example.com", "FolderLink", new ImageLogo("https://folderlink.example.org/logo.png")))));

        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(
                new MockAuthorizationStrategy()
                        .grant(Jenkins.ADMINISTER).everywhere().to("alice")
                        .grant(Jenkins.READ).everywhere().to("bob")
                        .grant(Item.READ).onItems(freeStyleProject).to("bob"));

        try (JenkinsRule.WebClient webClient = j.createWebClient().login("alice")) {
            // alice seems everything
            final HtmlPage htmlPage = webClient.goTo("");
            final String csp = htmlPage.getWebResponse().getResponseHeaderValue("Content-Security-Policy");
            assertThat(csp, containsString("img-src 'self' data: https://folderlink.example.org/logo.png https://joblink.example.org/logo.png https://link.example.org/logo.png https://logo.example.com/logo.png;"));
        }

        try (JenkinsRule.WebClient webClient = j.createWebClient().login("bob")) {
            // Bob gets basic logos plus the job link logo, but not the folder link logo
            final HtmlPage htmlPage = webClient.goTo("");
            final String csp = htmlPage.getWebResponse().getResponseHeaderValue("Content-Security-Policy");
            assertThat(csp, containsString("img-src 'self' data: https://joblink.example.org/logo.png https://link.example.org/logo.png https://logo.example.com/logo.png;"));
        }

        try (JenkinsRule.WebClient webClient = j.createWebClient().withJavaScriptEnabled(false)) {
            // anon gets only the header logo
            final HtmlPage htmlPage = webClient.goTo("whoAmI");
            final String csp = htmlPage.getWebResponse().getResponseHeaderValue("Content-Security-Policy");
            assertThat(csp, containsString("img-src 'self' data: https://logo.example.com/logo.png;"));
        }
    }

    @Issue("https://github.com/jenkinsci/customizable-header-plugin/issues/304")
    @Test
    void logSpam(JenkinsRule j) throws Exception {
        final CustomHeaderConfiguration configuration = ExtensionList.lookupSingleton(CustomHeaderConfiguration.class);
        configuration.setEnabled(true);
        configuration.setLogo(new ImageLogo("https://logo.example.com/logo.png"));
        configuration.setContextAwareLogo(new ContextAwareLogo());

        LoggerRule loggerRule = new LoggerRule().record(CspBuilder.class, Level.WARNING).capture(100);

        try (JenkinsRule.WebClient webClient = j.createWebClient().withJavaScriptEnabled(false)) {
            final HtmlPage htmlPage = webClient.goTo("whoAmI");
            final String csp = htmlPage.getWebResponse().getResponseHeaderValue("Content-Security-Policy");
            assertThat(csp, containsString("https://logo.example.com/logo.png"));
        }

        assertThat(loggerRule.getMessages(), is(empty()));
    }
}
