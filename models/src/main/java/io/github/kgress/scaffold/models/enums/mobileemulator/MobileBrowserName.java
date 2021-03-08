package io.github.kgress.scaffold.models.enums.mobileemulator;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An enum that defines the available browser types for mobile emulation.
 */
@Getter
@AllArgsConstructor
public enum MobileBrowserName {

  CHROME("Chrome"),
  SAFARI("Safari"),
  ANDROID("Android");

  private String browserName;
}
