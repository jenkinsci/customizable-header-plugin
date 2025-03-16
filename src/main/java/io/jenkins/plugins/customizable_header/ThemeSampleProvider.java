package io.jenkins.plugins.customizable_header;

import java.util.List;

public interface ThemeSampleProvider {
  default List<ThemeSample> getSamples() {
    return List.of(
        new ThemeSample("Default", null, null),
        new ThemeSample("Classic", "color-mix(in srgb, var(--black) 85%, transparent)", "var(--white)"),
        new ThemeSample("Hudson", "linear-gradient(#3465A4, #89A3DC calc(100% - 4px), #FCAF3E calc(100% - 4px), #FCAF3E) no-repeat",  "var(--white)"),
        new ThemeSample("Accent", "var(--accent-color)", "var(--white)"),
        new ThemeSample("Rainbow", "linear-gradient(45deg in oklch, var(--red), var(--orange), var(--yellow), var(--green), var(--blue), var(--indigo), var(--purple))", "var(--white)"),
        new ThemeSample("Gradient", "linear-gradient(90deg, rgba(28,99,190,1) 0%, rgba(53,224,192,1) 100%);", "var(--white)")
    );
  }
}
