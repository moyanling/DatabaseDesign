package org.mo39.fmbh.databasedesign.framework;

import static org.mo39.fmbh.databasedesign.utils.FileUtils.tblRef;

import java.io.IOException;
import java.util.List;

import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.InvalidInformationSchemaException;
import org.mo39.fmbh.databasedesign.model.Table;
import org.mo39.fmbh.databasedesign.utils.FileUtils;

/**
 * The system info schema related operation should be only invoked with utils. All invoking methods
 * would not check preconditions such as whether the file has already exists. These preconditions
 * should assured before doing updates to info schema.
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

  private static String createSchemata;
  private static String createTables;
  private static String createColumns;

  private static List<String> schemataValues;
  private static List<String> tablesValues;
  private static List<String> columnsValues;

  /**
   * Initiate the information schema. If all three tables already exist, return. Otherwise create
   * three tblRef(infoSchema, tables).
   *
   */
  static void init() {
    if (exists()) {
      return;
    }
    try {
      createInformationTable(createSchemata, infoSchema, schemata, schemataValues);
      createInformationTable(createTables, infoSchema, tables, tablesValues);
      createInformationTable(createColumns, infoSchema, columns, columnsValues);
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
  }

  /**
   * Validate schemata and tables.
   *
   */
  static void validate() {
    String message;
    if (FileUtils.validateSchemas()) {
      for (String schema : FileUtils.getSchemaSet()) {
        if (!FileUtils.validateTables(schema)) {
          message = "Invalid table in schema - '" + schema + "'.";
          break;
        }
      }
      return;
    } else {
      message = "Invalid schemata.";
    }
    throw new InvalidInformationSchemaException(message);
  }

  /**
   * Check if three tables of information schema exist.
   *
   * @return
   */
  static boolean exists() {
    if (tblRef(infoSchema, schemata).exists() && tblRef(infoSchema, tables).exists()
        && tblRef(infoSchema, columns).exists()) {
      return true;
    } else {
      clear();
      return false;
    }
  }

  /**
   * Clear all three tables in information_schema;
   *
   */
  static void clear() {
    try {
      if (tblRef(infoSchema, schemata).exists()) {
        java.nio.file.Files.delete(tblRef(infoSchema, schemata).toPath());
      }
      if (tblRef(infoSchema, tables).exists()) {
        java.nio.file.Files.delete(tblRef(infoSchema, tables).toPath());
      }
      if (tblRef(infoSchema, columns).exists()) {
        java.nio.file.Files.delete(tblRef(infoSchema, columns).toPath());
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
  }

  /**
   * Create SCHEMATA, TABLES, and COLUMNS table in INFORMATION_SCHEMA.
   *
   * @throws IOException
   * @throws DBExceptions
   */
  private static void createInformationTable(String create, String schema, String table,
      List<String> values) throws IOException, DBExceptions {
    List<Column> cols = Column.newColumnDefinition(create);
    tblRef(schema, table).createNewFile();
    for (Column col : cols) {
      FileUtils.ndxRef(schema, table, col.getName()).createNewFile();
    }
    Table t = Table.init(schema, table, cols);
    for (String arg : values) {
      t.addRecord(arg);
    }
    t.writeToDB();
  }

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

  public void setCreateSchemata(String createSchemata) {
    InfoSchema.createSchemata = createSchemata;
  }

  public void setCreateTables(String createTables) {
    InfoSchema.createTables = createTables;
  }

  public void setCreateColumns(String createColumns) {
    InfoSchema.createColumns = createColumns;
  }

  public void setSchemataValues(List<String> schemataValues) {
    InfoSchema.schemataValues = schemataValues;
  }

  public void setTablesValues(List<String> tablesValues) {
    InfoSchema.tablesValues = tablesValues;
  }

  public void setColumnsValues(List<String> columnsValues) {
    InfoSchema.columnsValues = columnsValues;
  }

  public static void main(String[] args) {
    new DatabaseDesign();
    InfoSchema.init();
    InfoSchema.validate();
    // System.out.println(createColumns);
    // System.out.println();
    // List<Column> cols = Column.newColumnDefinition(createColumns);
    // System.out.println();
    // System.out.println();
    // for (Column col : cols) {
    // System.out.println(col.getName());
    // System.out.println(col.getConstraint().getName());
    // System.out.println(col.getDataType().getArg());
    // System.out.println();
    // }
  }

}
