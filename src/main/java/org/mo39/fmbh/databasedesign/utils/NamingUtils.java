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
      Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\_\\-]*?(?<!\\_)(?<!\\-)$");

  private static final Pattern TBL_FILE_NAMING_CONSTRAINT = Pattern.compile("(.*?)\\.(.*?)\\.tbl");

  private static final Pattern NDX_FILE_NAMING_CONSTRAINT =
      Pattern.compile("(.*?)\\.(.*?)\\.(.*?)\\.ndx");

  public static String inferSchemaFromtbl(String tableFileName) {
    return inferFrom(tableFileName, SCHEMA, TBL_FILE_NAMING_CONSTRAINT);
  }

  public static String inferTableFromtbl(String tableFileName) {
    return inferFrom(tableFileName, TABLE, TBL_FILE_NAMING_CONSTRAINT);
  }

  public static String inferSchemaFromndx(String indexFileName) {
    return inferFrom(indexFileName, SCHEMA, NDX_FILE_NAMING_CONSTRAINT);
  }

  public static String inferTableFromndx(String indexFileName) {
    return inferFrom(indexFileName, TABLE, NDX_FILE_NAMING_CONSTRAINT);
  }

  public static String inferColumnFromndx(String indexFileName) {
    return inferFrom(indexFileName, COLUMN, NDX_FILE_NAMING_CONSTRAINT);
  }

  /**
   * This function uses a regular expression to check the input name. The name could be schema,
   * table or column name. This should be used when the name is read from the archive other than
   * input by the user. When the name is an input from the user, use
   * {@link checkNamingConventions} instead.
   * <p>
   * By doing so, the input would follow the conventions so there should be no invalid name in
   * archive after saving the users' input. In other words, a input name not following a naming
   * convention is considered a bad usage and would be aborted. But if a invalid naming convention
   * is detected when reading files in the archive, it should be considered a server Error.
   *
   * @param name
   * @return
   */
  public static boolean checkNamingConventionsWithException(String name) {
    if (NAMING_CONSTRAINT.matcher(name).matches()) {
      return true;
    } else {
      throw new InvalidNamingConventionException("Invalid naming convention when checking " + name);
    }
  }

  /**
   * This function uses a regular expression to check the input name. The name could be schema,
   * table or column name. This should be used when the name is input by the user other than is read
   * from the archive.
   *
   * @param name
   * @return
   */
  public static boolean checkNamingConventions(String name) {
    return NAMING_CONSTRAINT.matcher(name).matches();
  }

  /**
   * Extract the name from a string that contains the name using provided regex and group number. If
   * the name is extract successfully and it follows the naming convention, the name is return. Else
   * return null.
   * <p>
   * Note: In the process of extraction, it's <strong> case-insensitive.
   *
   * @param strContainingName
   * @param regex
   * @param groupNum
   * @return
   */
  public static String extractAndCheckName(String strContainingName, String regexStr,
      int groupNum) {
    Pattern regex = Pattern.compile(regexStr, Pattern.CASE_INSENSITIVE);
    Matcher matcher = regex.matcher(strContainingName);
    if (matcher.matches()) {
      String toRet = matcher.group(groupNum).trim();
      if (NamingUtils.checkNamingConventions(toRet)) {
        return toRet;
      }
    }
    return null;
  }

  public static Map<String, String> decomposeTableFileName(String tableFileName) {
    if ((matcher = TBL_FILE_NAMING_CONSTRAINT.matcher(tableFileName)).matches()) {
      Map<String, String> toRet = Maps.newHashMap();
      if (checkNamingConventionsWithException(group = matcher.group(1))) {
        toRet.put("schema", group);
        if (checkNamingConventionsWithException(group = matcher.group(2))) {
          toRet.put("table", group);
          return toRet;
        }
      }
    }
    return null;
  }

  public static Map<String, String> decomposeIndexFileName(String indexFileName) {
    if ((matcher = NDX_FILE_NAMING_CONSTRAINT.matcher(indexFileName)).matches()) {
      Map<String, String> toRet = Maps.newHashMap();
      if (checkNamingConventionsWithException(group = matcher.group(1))) {
        toRet.put("schema", group);
        if (checkNamingConventionsWithException(group = matcher.group(2))) {
          toRet.put("table", group);
          if (checkNamingConventionsWithException(group = matcher.group(3))) {
            toRet.put("column", group);
            return toRet;
          }
        }
      }
    }
    return null;
  }

  private static String inferFrom(String indexFileName, int groupNumber, Pattern pattern) {
    if ((matcher = pattern.matcher(indexFileName)).matches()) {
      if (checkNamingConventionsWithException(group = matcher.group(groupNumber))) {
        return matcher.group(groupNumber);
      }
    }
    return null;
  }



}
