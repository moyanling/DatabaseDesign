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

  private Cmd currentCmd;
  private String currentSchema;

  public static String getCurrentCmdStr() {
    return holder.get().currentCmd.getCmdStr();
  }

  public static boolean hasActiveSchema() {
    return holder.get().currentSchema != null;
  }

  public static Cmd getCurrentCmd() {
    return holder.get().currentCmd;
  }

  public static void setCurrentCmd(Cmd currentCmd) {
    holder.get().currentCmd = currentCmd;
  }

  public static String getCurrentSchema() {
    return holder.get().currentSchema;
  }

  public static void setCurrentSchema(String currentSchema) {
    holder.get().currentSchema = currentSchema;
  }

  private static ThreadLocal<Status> holder = ThreadLocal.<Status>withInitial(() -> {
    return new Status();
  });


  // ------------------------------- protected -------------------------------

  /**
   * When the command run is finished, the currentCmd field is set back to null. This is called only
   * after the execution of cmd.
   */
  protected static void endRunCmd() {
    holder.get().currentCmd = null;
  }

}
