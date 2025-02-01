package io.jenkins.plugins.customizable_header.headers;

import hudson.Extension;
import hudson.model.Action;
import io.jenkins.plugins.customizable_header.CustomHeaderConfiguration;
import jenkins.model.Jenkins;
import jenkins.views.FullHeader;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import java.util.List;
import java.util.stream.Collectors;

@Extension
public class JenkinsHeader extends FullHeader implements SystemMessageProvider, LinkProvider {
  @Override
  public boolean isEnabled() {
    return CustomHeaderConfiguration.get().getActiveHeader() instanceof JenkinsHeaderSelector;
  }

  /**
   * @return a list of {@link Action} to show in the header, defaults to {@link hudson.model.RootAction} extensions
   */
  @Restricted(NoExternalUse.class)
  public List<Action> getActions() {
    return Jenkins.get()
            .getActions()
            .stream()
            .filter(e -> e.getIconFileName() != null)
            .collect(Collectors.toList());
  }
}
