package io.github.kgress.scaffold.models.enums.desktop;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple enum to differentiate the many browsers that Selenium supports.
 */
@Getter
@AllArgsConstructor
public enum BrowserType {

  FIREFOX("Firefox"),
  CHROME("Chrome"),
  SAFARI("Safari"),
  INTERNET_EXPLORER("Internet Explorer"),
  EDGE("MicrosoftEdge"),
  OPERA("Opera");

  private String browserName;
}
