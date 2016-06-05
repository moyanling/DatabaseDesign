package org.mo39.fmbh.databasedesign.dao;

import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.ExitOperation;
import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.SchemaOperation;
import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.SqlOperation;
import org.mo39.fmbh.databasedesign.dao.OperationAnnotation.TableOperation;

/**
 * @author Jihan Chen
 *
 */
public interface DatabaseDao {

  @SchemaOperation
  void showSchemas();

  @SchemaOperation
  void use();

  @SchemaOperation
  void createSchema();

  @TableOperation
  void showTables();

  @TableOperation
  void createTable();

  @TableOperation
  void dropTable();

  @SqlOperation
  void insertIntoTable();

  @SqlOperation
  void select();

  @ExitOperation
  void exit();
  
}
