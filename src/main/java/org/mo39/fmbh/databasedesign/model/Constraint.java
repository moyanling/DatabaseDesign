package org.mo39.fmbh.databasedesign.model;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.utils.DBChecker;

import com.google.common.base.Preconditions;

/**
 * Constraint class. It currently only supports "PRIMARY KEY" and "NOT NULL".
 *
 * @author Jihan Chen
 *
 */
public abstract class Constraint {

  private String name;
  private String regx;
  private String description;

  private static List<Constraint> supportedConstraintList;

  /**
   * Impose the constraint to a certain column. The implementation varies according to the subclass.
   *
   * @param schema
   * @param table
   * @param col
   * @return {@code true} if the column observes the constraint, otherwise {@code false}
   */
  public abstract boolean impose(String schema, String table, Column col, String value);

  /**
   * A static factory method that checks whether input string is a supported constraint. Does not support more than one
   * constraint, i.e. Constraint#supports("PRIMARY KEY NOT NULL") returns false.
   *
   * @param arg
   * @return
   */
  public static Constraint valueOf(String arg) {
    Preconditions.checkArgument(arg != null);
    if (arg.equals("")) {
      Constraint nc = new NoConstraint();
      nc.setDescription("No constraint is specified for this column");
      nc.setName("NO CONSTRAINT");
      nc.setRegx("N\\\\A");
      return nc;
    }
    for (Constraint cons : Constraint.supportedConstraintList) {
      Pattern regx = Pattern.compile(cons.regx, Pattern.CASE_INSENSITIVE);
      Matcher matcher = regx.matcher(arg);
      if (matcher.matches()) {
        return cons;
      }
    }
    return null;
  }

  public static void setConstraintList(List<Constraint> supportedConstraintList) {
    Constraint.supportedConstraintList = Collections.unmodifiableList(supportedConstraintList);
  }

  public static List<Constraint> getConstraintList() {
    return supportedConstraintList;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRegx() {
    return regx;
  }

  public void setRegx(String regx) {
    this.regx = regx;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Indicates that the specified column does not allow null value.
   *
   * @author Jihan Chen
   *
   */
  public static class NotNull extends Constraint {

    @Override
    public boolean impose(String schema, String table, Column col, String value) {
      Pattern p = Pattern.compile("NULL", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(value);
      if (m.matches()) {
        return false;
      }
      return true;
    }

  }

  /**
   * Indicates that the specified column is the primary key.
   *
   * @author Jihan Chen
   *
   */
  public static class PrimaryKey extends Constraint {

    @Override
    public boolean impose(String schema, String table, Column col, String value) {
      return DBChecker.checkPrimaryKey(schema, table, col, value);
    }

  }

  /**
   * Indicates that no constraint is specified for the column.
   *
   * @author Jihan Chen
   *
   */
  public static class NoConstraint extends Constraint {

    @Override
    public boolean impose(String schema, String table, Column col, String value) {
      return true;
    }

  }

}
