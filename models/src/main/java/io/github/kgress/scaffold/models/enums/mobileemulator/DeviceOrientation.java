package io.github.kgress.scaffold.models.enums.mobileemulator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceOrientation {

  PORTRAIT("portrait"),
  LANDSCAPE("landscape");

  private String deviceOrientation;
}
