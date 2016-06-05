package org.mo39.fmbh.databasedesign.dao;

import org.mo39.fmbh.databasedesign.model.Status;

public enum DatabaseDaoImpl implements DatabaseDao {

  INSTANCE;

  // --------------------------------- Schema Operation ---------------------------------
  @Override
  public void showSchemas() {
    print("showSchemas" +  Status.INSTANCE.getCurrentSql());
  }

  @Override
  public void use() {
    print("use: " + Status.INSTANCE.getCurrentSql());
  }

  @Override
  public void createSchema() {
    print("createSchema" + Status.INSTANCE.getCurrentSql());
  }

  // --------------------------------- Table Operation ---------------------------------
  @Override
  public void showTables() {
    print("showTables");
  }

  @Override
  public void createTable() {
    print("createTable");

  }

  @Override
  public void insertIntoTable() {
    print("insertIntoTable" + Status.INSTANCE.getCurrentSql());

  }

  @Override
  public void dropTable() {
    print("dropTable: " +  Status.INSTANCE.getCurrentSql());

  }

  // --------------------------------- Sql Operation ---------------------------------
  @Override
  public void select() {
    print("select: " +  Status.INSTANCE.getCurrentSql());
  }

  // --------------------------------- EXIT ---------------------------------
  @Override
  public void exit() {
    System.exit(0);
  }

  private static void print(Object obj) {
    System.out.print(obj);
  }



}
