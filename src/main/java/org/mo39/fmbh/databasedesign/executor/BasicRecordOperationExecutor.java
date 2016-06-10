package org.mo39.fmbh.databasedesign.executor;

import org.mo39.fmbh.databasedesign.framework.DatabaseDesignExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;

public abstract class BasicRecordOperationExecutor {

  public static class InsertIntoTable implements Executable, Viewable {

    @Override
    public String getView() {
      // TODO Auto-generated method stub
      return "insertIntoTable";
    }

    @Override
    @RequiresActiveSchema
    public void execute() {
      // TODO Auto-generated method stub

    }
  }

  public static class Select implements Executable, Viewable {

    @Override
    public String getView() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    @RequiresActiveSchema
    public void execute() throws BadUsageException {
      // TODO Auto-generated method stub

    }

  }

}
