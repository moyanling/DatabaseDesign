package org.mo39.fmbh.databasedesign.executor;

import org.mo39.fmbh.databasedesign.framework.Status;
import org.mo39.fmbh.databasedesign.framework.View.CliView;
import org.mo39.fmbh.databasedesign.framework.View.Viewable;

public abstract class BasicSchemaOperationExecutor {

  public static class ShowSchemas implements Executable, Viewable {

    @Override
    @SchemaOperation
    public void execute() {}

    @Override
    @CliView
    public String getView() {
      return "showSchemas";
    }

  }

  public static class Use implements Executable, Viewable {

    @Override
    @SchemaOperation
    public void execute() {
      Status.INSTANCE.setCurrentSchema("My_schema");

    }

    @Override
    @CliView
    public String getView() {
      return Status.INSTANCE.getCurrentSchema();
    }
  }

  public static class CreateSchema implements Executable, Viewable {

    @Override
    @SchemaOperation
    public void execute() {}

    @Override
    @CliView
    public String getView() {
      return Status.INSTANCE.getCurrentSchema();
    }
  }

}


