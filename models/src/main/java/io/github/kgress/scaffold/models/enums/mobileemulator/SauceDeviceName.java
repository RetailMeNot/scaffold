package io.github.kgress.scaffold.models.enums.mobileemulator;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple enum to differentiate device names for mobile emulation. These device names conform to
 * Sauce's platform configurator, found here https://wiki.saucelabs.com/display/DOCS/Platform+Configurator#/
 * <p>
 * As new devices are added, or existing device names change, we will have to add or correct them to
 * this list. Note: The devices listed here are emulators and NOT real devices.
 */
@Getter
@AllArgsConstructor
public enum SauceDeviceName {
  // Base Android emulators
  ANDROID_BASE("Android Emulator"),
  ANDROID_GOOGLE_API_BASE("Android GoogleAPI Emulator"),

  // Google Emulators
  GOOGLE_PIXEL_3A_XL("Google Pixel 3a XL GoogleAPI Emulator"),
  GOOGLE_PIXEL_3A("Google Pixel 3a GoogleAPI Emulator"),
  GOOGLE_PIXEL_3_XL("Google Pixel 3 XL GoogleAPI Emulator"),
  GOOGLE_PIXEL_3("Google Pixel 3 GoogleAPI Emulator"),
  GOOGLE_PIXEL_C("Google Pixel C GoogleAPI Emulator"),
  GOOGLE_PIXEL("Google Pixel GoogleAPI Emulator"),

  // Samsung Emulators
  SAMSUNG_GALAXY_TAB_S3("Samsung Galaxy Tab S3 GoogleAPI Emulator"),
  SAMSUNG_GALAXY_TAB_A_10("Samsung Galaxy Tab A 10 GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S9_WQHD("Samsung Galaxy S9 WQHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S9_PLUS_WQHD("Samsung Galaxy S9 Plus WQHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S9_PLUS_HD("Samsung Galaxy S9 Plus HD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S9_PLUS_FHD("Samsung Galaxy S9 Plus FHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S9_HD("Samsung Galaxy S9 HD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S9_FHD("Samsung Galaxy S9 FHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S8_WQHD("Samsung Galaxy S8 WQHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S8_PLUS_WQHD("Samsung Galaxy S8 Plus WQHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S8_PLUS_HD("Samsung Galaxy S8 Plus HD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S8_PLUS("Samsung Galaxy S8 Plus GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S8_PLUS_FHD("Samsung Galaxy S8 Plus FHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S8_HD("Samsung Galaxy S8 HD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S8("Samsung Galaxy S8 GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S8_FHD("Samsung Galaxy S8 FHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S7_WQHD("Samsung Galaxy S7 WQHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S7_HD("Samsung Galaxy S7 HD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S7("Samsung Galaxy S7 GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S7_FHD("Samsung Galaxy S7 FHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S7_EDGE_WQHD("Samsung Galaxy S7 Edge WQHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S7_EDGE_HD("Samsung Galaxy S7 Edge HD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S7_EDGE("Samsung Galaxy S7 Edge GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S7_EDGE_FHD("Samsung Galaxy S7 Edge FHD GoogleAPI Emulator"),
  SAMSUNG_GALAXY_S6("Samsung Galaxy S6 GoogleAPI Emulator"),

  // Apple Emulators
  IPHONE_XS("iPhone XS Simulator"),
  IPHONE_XS_MAX("iPhone XS Max Simulator"),
  IPHONE_XR("iPhone XR Simulator"),
  IPHONE_X("iPhone X Simulator"),
  IPHONE("iPhone Simulator"),
  IPHONE_SE("iPhone SE Simulator"),
  IPHONE_SE_2ND_GEN("iPhone SE (2nd generation) Simulator)"),
  IPHONE_SE_1ST_GEN("iPhone SE (1st generation) Simulator)"),
  IPHONE_8("iPhone 8 Simulator"),
  IPHONE_8_PLUS("iPhone 8 Plus Simulator"),
  IPHONE_7("iPhone 7 Simulator"),
  IPHONE_7_PLUS("iPhone 7 Plus Simulator"),
  IPHONE_6S("iPhone 6s Simulator"),
  IPHONE_6S_PLUS("iPhone 6s Plus Simulator"),
  IPHONE_6("iPhone 6 Simulator"),
  IPHONE_6_PLUS("iPhone 6 Plus Simulator"),
  IPHONE_5S("iPhone 5s Simulator"),
  IPHONE_5S_PLUS("iPhone 5s Plus Simulator"),
  IPHONE_12_MINI("iPhone 12 mini Simulator"),
  IPHONE_12("iPhone 12 Simulator"),
  IPHONE_12_PRO("iPhone 12 Pro Simulator"),
  IPHONE_12_PRO_MAX("iPhone 12 Pro Max Simulator"),
  IPHONE_11("iPhone 11 Simulator"),
  IPHONE_11_PRO("iPhone 11 Pro Simulator"),
  IPHONE_11_PRO_MAX("iPhone 11 Pro Max Simulator"),
  IPAD_MINI_4("iPad mini 4 Simulator"),
  IPAD_MINI_3("iPad mini 3 Simulator"),
  IPAD_MINI_2("iPad mini 2 Simulator"),
  IPAD_MINI_5TH_GEN("iPad mini (5th generation) Simulator"),
  IPAD("iPad Simulator"),
  IPAD_PRO_9_7_INCH("iPad Pro (9.7 inch) Simulator"),
  IPAD_PRO_12_9_INCH("iPad Pro (12.9 inch) Simulator"),
  IPAD_PRO_12_9_INCH_4TH_GEN("iPad Pro (12.9 inch) (4th generation) Simulator"),
  IPAD_PRO_12_9_INCH_3RD_GEN("iPad Pro (12.9 inch) (3rd generation) Simulator"),
  IPAD_PRO_12_9_INCH_2ND_GEN("iPad Pro (12.9 inch) (2nd generation) Simulator"),
  IPAD_PRO_12_9_INCH_1ST_GEN("iPad Pro (12.9 inch) (1st generation) Simulator"),
  IPAD_PRO_11_INCH("iPad Pro (11 inch) Simulator"),
  IPAD_PRO_11_INCH_2ND_GEN("iPad Pro (11 inch) (2nd generation) Simulator"),
  IPAD_PRO_10_5_INCH("iPad Pro (10.5 inch) Simulator"),
  IPAD_AIR("iPad Air Simulator"),
  IPAD_AIR_2("iPad Air 2 Simulator"),
  IPAD_AIR_4TH_GEN("iPad Air (4th generation) Simulator"),
  IPAD_AIR_3RD_GEN("iPad Air (3rd generation) Simulator"),
  IPAD_8TH_GEN("iPad (8th generation) Simulator"),
  IPAD_7TH_GEN("iPad (7th generation) Simulator"),
  IPAD_6TH_GEN("iPad (6th generation) Simulator"),
  IPAD_5TH_GEN("iPad (5th generation) Simulator");

  private String deviceName;
}
