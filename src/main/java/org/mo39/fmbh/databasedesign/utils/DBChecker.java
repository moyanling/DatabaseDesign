package org.mo39.fmbh.databasedesign.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.ClassNotFound;

import com.google.common.base.Preconditions;

public class DBChecker {
  private static final Pattern NAMING_CONVENTION =
      Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\_\\-]*?(?<!\\_)(?<!\\-)$");

  /**
   * This function uses a regular expression to check the input name. The name could be schema,
   * table or column name. This should be used when the name is input by the user other than is read
   * from the archive.
   *
   * @param name
   * @return
   */
  public static boolean checkNamingConventions(String name) {
    Preconditions.checkArgument(name != null);
    return NAMING_CONVENTION.matcher(name).matches();
  }

  /**
   * Check if a Matcher object matches. If not, throw {@link BadUsageException}
   * 
   * @param m
   * @throws BadUsageException
   */
  public static void checkSyntax(Matcher m) throws BadUsageException {
    if (!m.matches()) {
      throw new BadUsageException("Syntax not valid.");
    }
  }

  public static String checkName(Matcher m, int group) throws BadUsageException {
    String toRet = m.group(group);
    if (!checkNamingConventions(toRet)) {
      throw new BadUsageException("Bad naming convention: " + toRet);
    }
    return toRet;
  }

  /**
   * Check if there's no duplicate value for a primary key.
   *
   * @param schema
   * @param table
   * @param col
   * @param value
   * @return
   */
  public static boolean checkPrimaryKey(String schema, String table, Column col, String value) {
    try {
      List<NdxUtils.Ndx> ndxList = NdxUtils.getNdxList(schema, table, col);
      return NdxUtils.findNdx(col, value, ndxList) == null;
    } catch (Exception e) {
      DBExceptions.newError(e);
      return false;
    }
  }

  public static Class<?> checkClass(Matcher m, int group) throws ClassNotFound {
    String className = m.group(group);
    Class<?> beanClass;
    try {
      beanClass = Class.forName(className);
      return beanClass;
    } catch (ClassNotFoundException e) {
      throw new ClassNotFound("Class '" + e.getMessage() + "' is not Found.");
    }
  }

}
