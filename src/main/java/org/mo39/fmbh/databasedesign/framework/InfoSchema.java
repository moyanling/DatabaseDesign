package org.mo39.fmbh.databasedesign.framework;

import static org.mo39.fmbh.databasedesign.framework.SystemProperties.COLUMNS;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.INFO_SCHEMA;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.SCHEMAS;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.TABLES;
import static org.mo39.fmbh.databasedesign.utils.FileUtils.tblRef;
import static org.mo39.fmbh.databasedesign.utils.NamingUtils.join;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.InvalidInformationSchemaException;
import org.mo39.fmbh.databasedesign.utils.FileUtils;

import com.google.common.io.Files;

/**
 * The system info schema related operation should be only invoked with utils. All invoking methods
 * would not check preconditions such as whether the file has already exists. These preconditions
 * should assured before doing updates to info schema. <br>
 * //TODO Kill the {@link SystemProperties#DELIMITER} and {@link SystemProperties#LINE_BREAK}.
 * Create column definition for information schema, other than plain string.
 *
 * @author Jihan Chen
 *
 */
public abstract class InfoSchema {

  private static String SCHEMATA_DEFINITION = "SCHEMA_NAME VARCHAR(20)";
  private static String TABLES_DEFINITION = "TABLE_SCHEMA | TABLE_NAME | TABLE_ROWS";
  private static String COLUMNS_DEFINITION = "TABLE_SCHEMA | TABLE_NAME | COLUMN NAME | ORDINAL_POSITION | COLUMN_TYPE | IS_NULLABLE | COLUMN_KEY";

  private static File schemata = tblRef(INFO_SCHEMA, SCHEMAS);
  private static File tables = tblRef(INFO_SCHEMA, TABLES);
  private static File columns = tblRef(INFO_SCHEMA, COLUMNS);

  /**
   * Initiate the information schema. If all three tables already exist, return. Otherwise create
   * three tables.
   *
   */
  protected static final void init() {
    if (exists()) {
      return;
    }
    try {
      createSchemata();
      createTables();
      createColumns();
    } catch (IOException e) {
      DBExceptions.newError(e);
    }
  }

  /**
   * Validate SCHEMATA and TABLES.
   *
   */
  protected static final void validate() {
    String message;
    if (FileUtils.validateSchemas()) {
      for (String schema : FileUtils.getSchemas()) {
        if (!FileUtils.validateTables(schema)) {
          message = "Invalid table in schema - '" + schema + "'.";
          break;
        }
      }
      return;
    } else {
      message = "Invalid SCHEMATA.";
    }
    throw new InvalidInformationSchemaException(message);
  }

  /**
   * Check if three tables of information schema exist.
   *
   * @return
   */
  protected static final boolean exists() {
    if (schemata.exists() && tables.exists() && columns.exists()) {
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
  protected static final void clear() {
    try {
      if (schemata.exists()) {
        java.nio.file.Files.delete(schemata.toPath());
      }
      if (tables.exists()) {
        java.nio.file.Files.delete(tables.toPath());
      }
      if (columns.exists()) {
        java.nio.file.Files.delete(columns.toPath());
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
  }

  /**
   * SCHEMATA
   * +-------------+
   * | SCHEMA_NAME |
   * +-------------+
   *
   * @throws IOException
   */
  private static final void createSchemata() throws IOException {
    schemata.createNewFile();
    BufferedWriter out = Files.newWriter(schemata, SystemProperties.getCharset());
    out.write(join(INFO_SCHEMA));
    out.close();
  }

  /**
   * TABLES
   * +--------------+------------+------------+
   * | TABLE_SCHEMA | TABLE_NAME | TABLE_ROWS |
   * +--------------+------------+------------+
   *
   * @throws IOException
   */
  private static final void createTables() throws IOException {
    tables.createNewFile();
    BufferedWriter out = Files.newWriter(tables, SystemProperties.getCharset());
    out.write(join(INFO_SCHEMA, SCHEMAS, 1));
    out.write(join(INFO_SCHEMA, TABLES, 3));
    out.write(join(INFO_SCHEMA, COLUMNS, 4));
    out.close();
  }

  /**
   * COLUMNS
   * +--------------+------------+-------------+------------------+-------------+-------------+------------+
   * | TABLE_SCHEMA | TABLE_NAME | COLUMN NAME | ORDINAL_POSITION | COLUMN_TYPE | IS_NULLABLE | COLUMN_KEY |
   * +--------------+------------+-------------+------------------+-------------+-------------+------------+
   *
   * @throws IOException
   */
  private static final void createColumns() throws IOException {
    columns.createNewFile();
    BufferedWriter out = Files.newWriter(columns, SystemProperties.getCharset());
    out.write(join(INFO_SCHEMA, SCHEMAS, "SCHEMA_NAME", "1", "varchar(64)", "NO", ""));
    out.write(join(INFO_SCHEMA, TABLES, "TABLE_SCHEMA", "1", "varchar(64)", "NO", ""));
    out.write(join(INFO_SCHEMA, TABLES, "TABLE_NAME", "2", "varchar(64)", "NO", ""));
    out.write(join(INFO_SCHEMA, TABLES, "TABLE_ROWS", "3", "int", "NO", ""));
    out.close();
  }
}
