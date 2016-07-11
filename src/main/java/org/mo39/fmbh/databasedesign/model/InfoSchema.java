package org.mo39.fmbh.databasedesign.model;

import java.util.List;

/**
 *
 * @author Jihan Chen
 *
 */
public class InfoSchema {

  private static String archiveRoot;
  private static String infoSchema;
  private static String schemata;
  private static String tables;
  private static String columns;
  private static String fileLock;

  private static String createSchemata;
  private static String createTables;
  private static String createColumns;

  private static List<String> schemataValues;
  private static List<String> tablesValues;
  private static List<String> columnsValues;

  public static String getArchiveRoot() {
    return archiveRoot;
  }

  public void setArchiveRoot(String archiveRoot) {
    InfoSchema.archiveRoot = archiveRoot;
  }

  public static String getInfoSchema() {
    return infoSchema;
  }

  public void setInfoSchema(String infoSchema) {
    InfoSchema.infoSchema = infoSchema;
  }

  public static String getSchemata() {
    return schemata;
  }

  public void setSchemata(String schemata) {
    InfoSchema.schemata = schemata;
  }

  public static String getTables() {
    return tables;
  }

  public void setTables(String tables) {
    InfoSchema.tables = tables;
  }

  public static String getColumns() {
    return columns;
  }

  public void setColumns(String columns) {
    InfoSchema.columns = columns;
  }

  public static String getFileLock() {
    return fileLock;
  }

  public static void setFileLock(String fileLock) {
    InfoSchema.fileLock = fileLock;
  }

  public static String getCreateSchemata() {
    return createSchemata;
  }

  public void setCreateSchemata(String createSchemata) {
    InfoSchema.createSchemata = createSchemata;
  }

  public static String getCreateTables() {
    return createTables;
  }

  public void setCreateTables(String createTables) {
    InfoSchema.createTables = createTables;
  }

  public static String getCreateColumns() {
    return createColumns;
  }

  public void setCreateColumns(String createColumns) {
    InfoSchema.createColumns = createColumns;
  }

  public static List<String> getSchemataValues() {
    return schemataValues;
  }

  public void setSchemataValues(List<String> schemataValues) {
    InfoSchema.schemataValues = schemataValues;
  }

  public static List<String> getTablesValues() {
    return tablesValues;
  }

  public void setTablesValues(List<String> tablesValues) {
    InfoSchema.tablesValues = tablesValues;
  }

  public static List<String> getColumnsValues() {
    return columnsValues;
  }

  public void setColumnsValues(List<String> columnsValues) {
    InfoSchema.columnsValues = columnsValues;
  }

}
