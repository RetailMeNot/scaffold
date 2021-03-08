package io.github.kgress.scaffold.models.enums.desktop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple enum to differentiate the many run types that Selenium can handle.
 */
@Getter
@AllArgsConstructor
public enum RunType {
  UNIT("UNIT"),
  LOCAL("LOCAL"),
  GRID("GRID"),
  SAUCE("SAUCE"),
  SAUCE_MOBILE_EMULATOR("SAUCE MOBILE EMULATOR"),
  HEADLESS("HEADLESS");

  private final String runType;
}
