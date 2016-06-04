package org.mo39.fmbh.databasedesign.dao;


/**
 * @author Jihan Chen
 *
 */
interface databaseDao {

  void showSchemas();

  void use(String schemaName);

  void showTables();

  void createSchema();

  void createTable();

  void insertIntoTable();

  void dropTable();

  void select();

}
