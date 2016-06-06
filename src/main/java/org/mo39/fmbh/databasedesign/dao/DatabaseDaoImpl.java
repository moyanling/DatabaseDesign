package org.mo39.fmbh.databasedesign.dao;

import org.mo39.fmbh.databasedesign.model.Status;

public enum DatabaseDaoImpl  {

  INSTANCE;

  // --------------------------------- Schema Operation ---------------------------------


  // --------------------------------- Table Operation ---------------------------------



  // --------------------------------- Sql Operation ---------------------------------

  public void select() {
    print("select: " +  Status.INSTANCE.getCurrentSql());
  }

  // --------------------------------- EXIT ---------------------------------

  public void exit() {
    System.exit(0);
  }

  private static void print(Object obj) {
    System.out.print(obj);
  }



}
