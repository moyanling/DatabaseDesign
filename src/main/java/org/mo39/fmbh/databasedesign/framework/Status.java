package org.mo39.fmbh.databasedesign.framework;

import org.mo39.fmbh.databasedesign.model.Cmd;

/**
 * This presents the current status of database. Includes but not limited to the schema, table and
 * SQL currently using. The class is run in threads and have a thread local copy per thread.
 *
 * @author Jihan Chen
 *
 */
public class Status {

  private Status() {}

  private static Cmd currentCmd;
  private static String currentSchema;

  public static String getCurrentCmdStr() {
    return Status.currentCmd.getCmdStr();
  }

  public static boolean hasActiveSchema() {
    return currentSchema != null;
  }

  public static Cmd getCurrentCmd() {
    return currentCmd;
  }

  public static void setCurrentCmd(Cmd currentCmd) {
    Status.currentCmd = currentCmd;
  }

  public static String getCurrentSchema() {
    return currentSchema;
  }

  public static void setCurrentSchema(String currentSchema) {
    Status.currentSchema = currentSchema;
  }

  // ------------------------------- protected -------------------------------

  /**
   * When the command run is finished, the currentCmd field is set back to null. This is called only
   * in framework.SupportedCmds after the execution of cmd.
   */
  protected static void endRunCmd() {
    currentCmd = null;
  }

}
