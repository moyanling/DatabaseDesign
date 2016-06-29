package org.mo39.fmbh.databasedesign.framework;

import static com.google.common.base.Preconditions.checkArgument;
import static org.mo39.fmbh.databasedesign.utils.FileUtils.tblRef;
import static org.mo39.fmbh.databasedesign.utils.NamingUtils.join;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.Constraint;
import org.mo39.fmbh.databasedesign.model.Constraint.NoConstraint;
import org.mo39.fmbh.databasedesign.model.Constraint.NotNull;
import org.mo39.fmbh.databasedesign.model.Constraint.PrimaryKey;
import org.mo39.fmbh.databasedesign.model.DBExceptions;
import org.mo39.fmbh.databasedesign.model.DBExceptions.InvalidInformationSchemaException;
import org.mo39.fmbh.databasedesign.utils.FileUtils;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;
import org.mo39.fmbh.databasedesign.utils.TblUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
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

  private static final String INFO_SCHEMA = SystemProperties.get("InfoSchema");
  private static final String SCHEMATA = SystemProperties.get("schemata");
  private static final String TABLES = SystemProperties.get("tables");
  private static final String COLUMNS = SystemProperties.get("columns");


  private static File schemata = tblRef(INFO_SCHEMA, SCHEMATA);
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
    out.write(join(INFO_SCHEMA, SCHEMATA, 1));
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
    out.write(join(INFO_SCHEMA, SCHEMATA, "SCHEMA_NAME", "1", "varchar(64)", "NO", ""));
    out.write(join(INFO_SCHEMA, TABLES, "TABLE_SCHEMA", "1", "varchar(64)", "NO", ""));
    out.write(join(INFO_SCHEMA, TABLES, "TABLE_NAME", "2", "varchar(64)", "NO", ""));
    out.write(join(INFO_SCHEMA, TABLES, "TABLE_ROWS", "3", "int", "NO", ""));
    out.close();
  }

  /**
   * Get schemas from SCHEMATA.
   * 
   * @return
   */
  public static Set<String> getSchemas() {
    try {
      List<String> lines = Files.readLines(schemata, SystemProperties.getCharset());
      lines.remove(0);// Remove Information Schema itself.
      return Collections.unmodifiableSet(Sets.newHashSet(lines));
    } catch (IOException e) {
      throw new Error(e.getMessage());
    }
  }

  /**
   * Get tables from TABLES.
   * 
   * @param schema
   * @return
   */
  public static Set<String> getTables(String schema) {
    checkArgument(schema != null);
    try {
      List<String> lines = Files.readLines(tables, SystemProperties.getCharset());
      Collection<String> filtered =
          Collections2.filter(lines, Predicates.containsPattern("^" + schema));
      Set<String> tables = Sets.newHashSet();
      for (String table : filtered) {
        tables.add(table.split(",")[1].trim());
      }
      return Collections.unmodifiableSet(Sets.newHashSet(tables));
    } catch (IOException e) {
      throw new Error(e.getMessage());
    }
  }

  public static List<Column> getColumns(String schema, String table) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Update information schema when new schema is created.
   * 
   * @param schema
   */
  public static void updateAtCreatingSchema(String schema) {
    try {
      Files.append(schema + SystemProperties.get("lineBreak"), schemata,
          SystemProperties.getCharset());
    } catch (IOException e) {
      throw new Error(e.getMessage());
    }
  }

  /**
   * Update information schema when new table is created.
   * 
   * @param schema
   * @param table
   * @param cols
   */
  public static void updateAtCreatingTable(String schema, String table, List<Column> cols) {
    try {
      Constraint con;
      StringBuilder sb = new StringBuilder();
      Files.append(NamingUtils.join(schema, table, 0), tables, SystemProperties.getCharset());
      String delimiter = SystemProperties.get("delimiter");
      for (Column col : cols) {
        sb.append(Joiner.on(delimiter).join(schema, table, col.getName(), cols.indexOf(col) + 1));
        con = col.getConstraint();
        sb.append(delimiter);
        if (con instanceof NoConstraint) {
          sb.append(Joiner.on(delimiter).join("YES", ""));
        } else if (con instanceof NotNull) {
          sb.append(Joiner.on(delimiter).join("NO", ""));
        } else if (con instanceof PrimaryKey) {
          sb.append(Joiner.on(delimiter).join("NO", "PRI"));
        }
        sb.append(SystemProperties.get("lineBreak"));
      }
      Files.append(sb.toString(), columns, SystemProperties.getCharset());
    } catch (IOException e) {
      throw new Error(e.getMessage());
    }

  }

  /**
   * Update information schema when table is dropped.
   * 
   * @param schema
   * @param table
   * @return
   */
  public static boolean updateAtDroppingTable(String schema, String table) {
    try {
      String pattern = "^" + schema + SystemProperties.get("delimiter") + table;
      return TblUtils.deleteLines(tables, Predicates.containsPattern(pattern))
          && TblUtils.deleteLines(columns, Predicates.containsPattern(pattern));
    } catch (IOException e) {
      throw new Error(e.getMessage());
    }
  }

  /**
   * Update information schema when schema is deleted.
   * 
   * @param schema
   * @return
   */
  public static boolean updateAtDeletingSchema(String schema) {
    try {
      String pattern = "^" + schema;
      return TblUtils.deleteLines(schemata, Predicates.containsPattern(pattern))
          && TblUtils.deleteLines(tables, Predicates.containsPattern(pattern))
          && TblUtils.deleteLines(columns, Predicates.containsPattern(pattern));
    } catch (IOException e) {
      throw new Error(e.getMessage());
    }
  }

}
