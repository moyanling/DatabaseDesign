package org.mo39.fmbh.databasedesign.framework;

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
