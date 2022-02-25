package io.github.kgress.scaffold.models.enums.desktop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple enum to differentiate the many OS's that Selenium supports.
 */
@Getter
@AllArgsConstructor
public enum Platform {

  WINDOWS("Windows"),
  WINDOWS_7("Windows 7"),
  WINDOWS_8("Windows 8"),
  WINDOWS_8_1("Windows 8.1"),
  WINDOWS_10("Windows 10"),
  XP("XP"),
  Vista("VISTA"),
  Mac("MAC"),
  Monterey("macOS 12"),
  Linux("LINUX"),
  Unix("UNIX");

  private final String platform;
}
