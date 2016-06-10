package org.mo39.fmbh.databasedesign.framework;

/**
 * This presents the current status of database. Includes but not limited to the schema, table and
 * SQL currently using. The class is run in threads and have a thread local copy per thread.
 *
 * @author Jihan Chen
 *
 */
public class Status {

  private Status() {}

  private String currentCmd;
  private String currentSchema;

  private static ThreadLocal<Status> holder = ThreadLocal.<Status>withInitial(() -> {
    return new Status();
  });

  public static Status getInstance() {
    return holder.get();
    // TODO Try to implement your own thread local instance here.
    // return null;
  }

  public boolean hasActiveSchema() {
    return currentSchema != null;
  }

  public String getCurrentCmd() {
    return currentCmd;
  }

  public void setCurrentCmd(String currentCmd) {
    this.currentCmd = currentCmd;
  }

  public String getCurrentSchema() {
    return currentSchema;
  }

  public void setCurrentSchema(String currentSchema) {
    this.currentSchema = currentSchema;
  }

  /**
   * When the command run is finished, the currentCmd field is set back to null. This is called only
   * in framework.SupportedCmds after the execution of cmd.
   */
  protected void endRunCmd() {
    currentCmd = null;
  }

}
