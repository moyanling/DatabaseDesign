package org.mo39.fmbh.databasedesign.utils;

import static org.mo39.fmbh.databasedesign.framework.SystemProperties.DELIMITER;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.LINE_BREAK;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.model.DBExceptions.InvalidNamingConventionException;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public abstract class NamingUtils {

  /**
   * Convenient reusable reference. In each method that uses regular expression, these will be
   * assigned and referred.
   */
  private static Matcher matcher;

  /**
   * The group index for regular expression.
   */
  private static final int TABLE = 1;
  private static final int COLUMN = 2;

  /**
   * A compiled regex pattern for naming convention.
   */
  private static final Pattern NAMING_CONVENTION =
      Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\_\\-]*?(?<!\\_)(?<!\\-)$");

  /**
   * A compiled regex pattern for .tbl file naming convention.
   */
  private static final Pattern TBL_FILE_NAMING_CONVENTION = Pattern.compile("(.*?)\\.tbl");

  /**
   * A compiled regex pattern for .ndx file naming convention.
   */
  private static final Pattern NDX_FILE_NAMING_CONVENTION = Pattern.compile("(.*?)\\.(.*?)\\.ndx");

  /**
   * Return a line of string joined by the {@link SystemProperties#DELIMITER}
   * 
   * @param objects
   * @return
   */
  public static String join(Object... objects) {
    return Joiner.on(DELIMITER).join(objects) + LINE_BREAK;
  }
  
  /**
   * infer table name from .tbl file
   *
   * @param tblFileName
   * @return
   */
  public static final String inferTableFromtbl(String tblFileName) {
    return inferFrom(tblFileName, TABLE, TBL_FILE_NAMING_CONVENTION);
  }

  /**
   * infer table name from .ndx file
   *
   * @param ndxFileName
   * @return
   */
  public static final String inferTableFromndx(String ndxFileName) {
    return inferFrom(ndxFileName, TABLE, NDX_FILE_NAMING_CONVENTION);
  }

  /**
   * infer column name from .ndx file
   *
   * @param ndxFileName
   * @return
   */
  public static final String inferColumnFromndx(String ndxFileName) {
    return inferFrom(ndxFileName, COLUMN, NDX_FILE_NAMING_CONVENTION);
  }

  /**
   * This function uses a regular expression to check the input name. The name could be schema,
   * table or column name. This should be used when the name is input by the user other than is read
   * from the archive.
   *
   * @param name
   * @return
   */
  public static final boolean checkNamingConventions(String name) {
    Preconditions.checkArgument(name != null);
    return NAMING_CONVENTION.matcher(name).matches();
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

  private static String inferFrom(String fileName, int groupNumber, Pattern pattern) {
    Preconditions.checkArgument(fileName != null && pattern != null);
    if ((matcher = pattern.matcher(fileName)).matches()) {
      if (checkNamingConventionsWithException(matcher.group(groupNumber))) {
        return matcher.group(groupNumber);
      }
    }
    return null;
  }

  /**
   * This function uses a regular expression to check the input name. The name could be schema,
   * table or column name. This should be used when the name is read from the archive other than
   * input by the user. When the name is an input from the user, use {@link checkNamingConventions}
   * instead.
   * <p>
   * By doing so, the input would follow the conventions so there should be no invalid name in
   * archive after saving the users' input. In other words, a input name not following a naming
   * convention is considered a bad usage and would be aborted. But if a invalid naming convention
   * is detected when reading files in the archive, it should be considered a server Error.
   *
   * @param name
   * @return
   */
  static final boolean checkNamingConventionsWithException(String name) {
    if (NAMING_CONVENTION.matcher(name).matches()) {
      return true;
    } else {
      throw new InvalidNamingConventionException("Invalid naming convention when checking " + name);
    }
  }

}
