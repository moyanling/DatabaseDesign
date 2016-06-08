package org.mo39.fmbh.databasedesign.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

public abstract class NamingUtils {

  private static Matcher matcher;
  private static String group;

  private static final Pattern NAMING_CONSTRAINT =
      Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\_]*?(?<!\\_)$");

  private static final Pattern TABLE_FILE_NAMING_CONSTRAINT =
      Pattern.compile("(.*?)\\.(.*?)\\.tbl");

  private static final Pattern SCHEMA_FILE_NAMING_CONSTRAINT =
      Pattern.compile("(.*?)\\.(.*?)\\.(.*?)\\.ndx");

  public static Map<String, String> decomposeTableFileName(String tableFileName) {
    if ((matcher = TABLE_FILE_NAMING_CONSTRAINT.matcher(tableFileName)).matches()) {
      Map<String, String> toRet = Maps.newHashMap();
      if (checkNamingConventions(group = matcher.group(1))) {
        toRet.put("schema", group);
      }
      if (checkNamingConventions(group = matcher.group(2))) {
        toRet.put("table", group);
      }

      return toRet.size() == 2 ? Maps.immutableEnumMap(toRet) : null;
    } else {
      return null;
    }
  }

  public static Map<String, String> decomposeIndexFileName(String indexFileName) {
    return null;
  }

  public static boolean checkNamingConventions(String name) {
    return NAMING_CONSTRAINT.matcher(name).matches();
  }



}
