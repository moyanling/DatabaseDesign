package org.mo39.fmbh.databasedesign.model;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesign;

import com.google.common.base.Preconditions;

/**
 * Command class. The fields are injected by applicationContext except cmd. String cmd is set
 * manually if the input arg matches the regx.
 *
 * @author Jihan Chen
 *
 */
public class Cmd {

  private String cmd;
  private String name;
  private String regx;
  private String description;
  private String executorClassName;

  private static List<Cmd> supportedCmdList;

  /**
   * Check whether input string is a supported Cmd. If returns true, {@link DatabaseDesign#runCmd()}
   * can be called.
   *
   * @param arg
   * @return Returns true if supports. Otherwise false.
   */
  public static Cmd supports(String arg) {
    Preconditions.checkArgument(arg != null);
    for (Cmd cmd : Cmd.supportedCmdList) {
      Pattern regx = Pattern.compile(cmd.regx, Pattern.CASE_INSENSITIVE);
      Matcher matcher = regx.matcher(arg);
      if (matcher.matches()) {
        cmd.setCmdStr(arg);
        return cmd;
      }
    }
    return null;
  }

  public static List<Cmd> getCmdList() {
    return supportedCmdList;
  }

  public static void setCmdList(List<Cmd> supportedCmdList) {
    Cmd.supportedCmdList = Collections.unmodifiableList(supportedCmdList);
  }

  public String getCmdStr() {
    return cmd;
  }

  public void setCmdStr(String sqlStr) {
    cmd = sqlStr;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getExecutorClassName() {
    return executorClassName;
  }

  public void setExecutorClassName(String executorClassName) {
    this.executorClassName = executorClassName;
  }



}
