package org.mo39.fmbh.databasedesign.model;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
   * Check whether input string is a supported constraint. Does not support more than one
   * constraint, i.e. Constraint#supports("PRIMARY KEY NOT NULL") returns false.
   *
   * @param arg
   * @return
   */
  public static Constraint supports(String arg) {
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

}
