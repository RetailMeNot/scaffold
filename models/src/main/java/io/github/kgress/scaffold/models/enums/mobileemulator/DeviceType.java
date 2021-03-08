package io.github.kgress.scaffold.models.enums.mobileemulator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceType {

  TABLET("Tablet"),
  PHONE("Phone");

  private String deviceType;
}
