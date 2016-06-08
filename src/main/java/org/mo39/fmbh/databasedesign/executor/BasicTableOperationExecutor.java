package org.mo39.fmbh.databasedesign.executor;

import org.mo39.fmbh.databasedesign.framework.View.CliView;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;

public abstract class BasicTableOperationExecutor {

  public static class ShowTables implements Executable, Viewable {

    @Override
    @CliView
    public String getView() {
      // TODO Auto-generated method stub
      return "showTables";
    }

    @Override
    @TableOperation
    public void execute() {
      // TODO Auto-generated method stub
    }

  }

  public static class CreateTable implements Executable, Viewable {

    @Override
    @CliView
    public String getView() {
      // TODO Auto-generated method stub
      return "createTable";
    }

    @Override
    @TableOperation
    public void execute() {
      // TODO Auto-generated method stub

    }

  }

  public static class InsertIntoTable implements Executable, Viewable {

    @Override
    @TableOperation
    public String getView() {
      // TODO Auto-generated method stub
      return "insertIntoTable";
    }

    @Override
    @CliView
    public void execute() {
      // TODO Auto-generated method stub

    }


  }

  public static class DropTable implements Executable, Viewable {

    @Override
    @CliView
    public String getView() {
      // TODO Auto-generated method stub
      return "dropTable: ";
    }

    @Override
    @TableOperation
    public void execute() {
      // TODO Auto-generated method stub
    }

  }

}
