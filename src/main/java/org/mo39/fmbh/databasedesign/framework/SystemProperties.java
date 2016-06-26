package org.mo39.fmbh.databasedesign.framework;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public abstract class SystemProperties {

  public static final String INFO_SCHEMA = "Information_Schema";
  
  public static final String SCHEMAS = "SCHEMATA";
  public static final String TABLES = "TABLES";
  public static final String COLUMNS = "COLUMNS";
  
  public static final String ARCHIVE_ROOT = ".\\archive";
  public static final String DELIMITER = ",";
  public static final String LINE_BREAK = "\n";

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
