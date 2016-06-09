package org.mo39.fmbh.databasedesign.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.InvalidNamingConventionException;

import com.google.common.collect.Maps;

public abstract class NamingUtils {

  private static Matcher matcher;
  private static String group;

  private static final int SCHEMA = 1;
  private static final int TABLE = 2;
  private static final int COLUMN = 3;

  private static final Pattern NAMING_CONSTRAINT =
      Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\_]*?(?<!\\_)$");

  private static final Pattern TABLE_FILE_NAMING_CONSTRAINT =
      Pattern.compile("(.*?)\\.(.*?)\\.tbl");

  private static final Pattern INDEX_FILE_NAMING_CONSTRAINT =
      Pattern.compile("(.*?)\\.(.*?)\\.(.*?)\\.ndx");

  public static String inferSchemaFromtbl(String tableFileName) {
    return inferFromTableFileName(tableFileName, SCHEMA);
  }

  public static String inferTableFromtbl(String tableFileName) {
    return inferFromTableFileName(tableFileName, TABLE);
  }

  public static String inferSchemaFromndx(String indexFileName) {
    return inferFromIndexFileName(indexFileName, SCHEMA);
  }

  public static String inferTableFromndx(String indexFileName) {
    return inferFromIndexFileName(indexFileName, TABLE);
  }

  public static String inferColumnFromndx(String indexFileName) {
    return inferFromIndexFileName(indexFileName, COLUMN);
  }

  public static Map<String, String> decomposeTableFileName(String tableFileName) {
    if ((matcher = TABLE_FILE_NAMING_CONSTRAINT.matcher(tableFileName)).matches()) {
      Map<String, String> toRet = Maps.newHashMap();
      if (checkNamingConventions(group = matcher.group(1))) {
        toRet.put("schema", group);
        if (checkNamingConventions(group = matcher.group(2))) {
          toRet.put("table", group);
          return toRet;
        }
      }
    }
    return null;
  }

  public static Map<String, String> decomposeIndexFileName(String indexFileName) {
    if ((matcher = INDEX_FILE_NAMING_CONSTRAINT.matcher(indexFileName)).matches()) {
      Map<String, String> toRet = Maps.newHashMap();
      if (checkNamingConventions(group = matcher.group(1))) {
        toRet.put("schema", group);
        if (checkNamingConventions(group = matcher.group(2))) {
          toRet.put("table", group);
          if (checkNamingConventions(group = matcher.group(3))) {
            toRet.put("column", group);
            return toRet;
          }
        }
      }
    }
    return null;
  }

  private static String inferFromTableFileName(String tableFileName, int groupNumber) {
    if ((matcher = TABLE_FILE_NAMING_CONSTRAINT.matcher(tableFileName)).matches()) {
      if (checkNamingConventions(group = matcher.group(groupNumber))) {
        return matcher.group(groupNumber);
      }
    }
    return null;
  }

  private static String inferFromIndexFileName(String indexFileName, int groupNumber) {
    if ((matcher = INDEX_FILE_NAMING_CONSTRAINT.matcher(indexFileName)).matches()) {
      if (checkNamingConventions(group = matcher.group(groupNumber))) {
        return matcher.group(groupNumber);
      }
    }
    return null;
  }

  public static boolean checkNamingConventions(String name) {
    if (NAMING_CONSTRAINT.matcher(name).matches()) {
      return true;
    } else {
      throw new InvalidNamingConventionException("Invalid naming convention when checking " + name);
    }
  }



}
