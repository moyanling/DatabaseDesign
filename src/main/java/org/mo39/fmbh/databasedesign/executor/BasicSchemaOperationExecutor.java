package org.mo39.fmbh.databasedesign.executor;

import org.mo39.fmbh.databasedesign.model.Status;
import org.mo39.fmbh.databasedesign.view.View.Viewable;

public abstract class BasicSchemaOperationExecutor {

  public static class ShowSchemas implements Executable, Viewable {

    @Override
    @SchemaOperation
    public void execute() {}

    @Override
    public String getView() {
      return "showSchemas" + Status.INSTANCE.getCurrentSql();
    }

  }

  public static class Use implements Executable, Viewable {

    @Override
    @SchemaOperation
    public void execute() {}

    @Override
    public String getView() {
      return "use: " + Status.INSTANCE.getCurrentSql();
    }
  }

  public static class CreateSchema implements Executable, Viewable {

    @Override
    @SchemaOperation
    public void execute() {}

    @Override
    public String getView() {
      return "createSchema" + Status.INSTANCE.getCurrentSql();
    }
  }
}
