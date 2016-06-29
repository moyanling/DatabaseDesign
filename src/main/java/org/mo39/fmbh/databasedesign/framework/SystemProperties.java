package org.mo39.fmbh.databasedesign.framework;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public abstract class SystemProperties {

  private static Map<String, String> systemProperties;

  public static void setSystemProperties(Map<String, String> systemProperties) {
    SystemProperties.systemProperties = Collections.unmodifiableMap(systemProperties);
  }

  public static String get(String key) {
    return systemProperties.get(key);
  }

  public static Charset getCharset() {
    return StandardCharsets.US_ASCII;
  }

}
