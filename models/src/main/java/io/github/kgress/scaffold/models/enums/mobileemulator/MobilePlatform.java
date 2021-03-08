package io.github.kgress.scaffold.models.enums.mobileemulator;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An enum that defines the possible mobile platforms for mobile emulation
 */
@Getter
@AllArgsConstructor
public enum MobilePlatform {

  ANDROID("Android"),
  IOS("iOS");

  private final String platform;
}
