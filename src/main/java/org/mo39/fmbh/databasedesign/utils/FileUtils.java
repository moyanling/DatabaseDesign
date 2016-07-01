package org.mo39.fmbh.databasedesign.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static org.mo39.fmbh.databasedesign.utils.NamingUtils.inferTableFromtbl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.InfoSchema;
import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.Constraint;
import org.mo39.fmbh.databasedesign.model.Constraint.NoConstraint;
import org.mo39.fmbh.databasedesign.model.Constraint.NotNull;
import org.mo39.fmbh.databasedesign.model.Constraint.PrimaryKey;
import org.mo39.fmbh.databasedesign.model.DBExceptions;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

/**
 * This class handles file level process within the archive. It helps to analyze certain file name,
 * create file, acquire file names, delete files and etc. It does not help to write or read the DB.
 *
 * @author Jihan Chen
 *
 */
public abstract class FileUtils {


  static final String ARCHIVE_ROOT = SystemProperties.get("archiveRoot");

  static final Pattern NDX_FILE = Pattern.compile(".*\\.ndx");
  static final Pattern TBL_FILE = Pattern.compile(".*\\.tbl");

  private static String delimiter = SystemProperties.get("delimiter");

  private static final String INFO_SCHEMA = SystemProperties.get("InfoSchema");
  private static final String SCHEMATA = SystemProperties.get("schemata");
  private static final String TABLES = SystemProperties.get("tables");
  private static final String COLUMNS = SystemProperties.get("columns");

  private static File schemata = tblRef(INFO_SCHEMA, SCHEMATA);
  private static File tables = tblRef(INFO_SCHEMA, TABLES);
  private static File columns = tblRef(INFO_SCHEMA, COLUMNS);

  /**
   * Get table file list. i.e. the file ends with .tbl.
   *
   * @return A unmodifiable list containing strings as the tbl file name.
   *         {@link Collections#unmodifiableList}
   */
  public static List<String> gettblFileList(String schema) {
    return getFileList(schema, TBL_FILE);
  }

  /**
   * Get index file list. i.e. the file ends with .ndx
   *
   * @return A unmodifiable list containing strings as the ndx file name.
   *         {@link Collections#unmodifiableList}
   */
  public static List<String> getndxFileList(String schema) {
    return getFileList(schema, NDX_FILE);
  }

  /**
   * Return a tbl file reference according to schema and table name.
   *
   * @param schema
   * @param table
   * @return
   */
  public static final File tblRef(String schema, String table) {
    checkArgument(schema != null && table != null);
    return schema.equals(SystemProperties.get("infoSchema"))
        ? Paths.get(FileUtils.ARCHIVE_ROOT, schema + "." + table + ".csv").toFile()
        : Paths.get(FileUtils.ARCHIVE_ROOT, schema, table + ".tbl").toFile();
  }

  /**
   * Return a ndx file reference according to schema, table and column name.
   *
   * @param schema
   * @param table
   * @param column
   * @return
   */
  public static final File ndxRef(String schema, String table, String column) {
    checkArgument(schema != null && table != null && column != null);
    return Paths.get(FileUtils.ARCHIVE_ROOT, table + "." + column + ".ndx").toFile();
  }

  /**
   * Create a new schema i.e. a folder to hold tables.
   *
   * @param schema
   * @throws IOException
   */
  public static boolean createSchema(String schema) throws IOException {
    if (Paths.get(ARCHIVE_ROOT, schema).toFile().mkdirs()) {
      UpdateInfoSchema.atCreatingSchema(schema);
      return true;
    }
    return false;
  }

  /**
   * This function will take schema as a folder and table as the tbl file name. Unless the schema is
   * information_schema. Related ndx files are created at the same time.
   *
   * @param schema
   * @param table
   * @throws IOException
   */
  public static boolean createtblFile(String schema, String table, List<Column> columns)
      throws IOException {
    if (!tblRef(schema, table).createNewFile()) {
      return false;
    }
    for (Column col : columns) {
      if (!ndxRef(schema, table, col.getName()).createNewFile()) {
        return false;
      }
    }
    UpdateInfoSchema.atCreatingTable(schema, table, columns);
    return true;
  }

  /**
   * Delete table in the archive
   *
   * @param schemaName
   * @param tableName
   * @return true if delete successfully else false.
   */
  public static boolean deleteTable(String schema, String table) {
    if (tblRef(schema, table).delete()) {
      if (UpdateInfoSchema.atDroppingTable(schema, table)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Delete a schema in the archive.
   *
   * @param schemaName
   * @return true if delete successfully else false.
   */
  public static boolean deleteSchema(String schema) {
    checkArgument(schema != null);
    for (String tblFileName : gettblFileList(schema)) {
      if (!tblRef(schema, tblFileName).delete()) {
        return false;
      }
    }
    if (Paths.get(ARCHIVE_ROOT, schema).toFile().delete()) {
      if (UpdateInfoSchema.atDeletingSchema(schema)) {
        return true;
      }
    }
    return false;

  }

  /**
   * Get schemas in the archive. Delegate to {@link InfoSchema#getSchemas()}
   *
   * @return A unmodifiable set containing all schemas in the archive.
   *         {@link Collections#unmodifiableSet}
   */
  public static Set<String> getSchemas() {
    return InfoSchemaUtils.getSchemas();
  }



  /**
   * Get tables for specified schema.
   *
   * @param schemaName
   * @return A unmodifiable set containing all tables in the archive.
   *         {@link Collections#unmodifiableSet}
   */
  public static Set<String> getTables(String schema) {
    return InfoSchemaUtils.getTables(schema);
  }

  /**
   * Get columns for specified table.
   *
   * @param schemaName
   * @return A unmodifiable list containing all Colums in the table.
   *         {@link Collections#unmodifiableSet}
   */
  public static List<Column> getColumns(String schema, String table) {
    return InfoSchemaUtils.getColumns(schema, table);
  }

  /**
   * Validate whether the schemas in SCHEMATA table is consistent with schema folders in archive.
   *
   * @return
   */
  public static boolean validateSchemas() {
    Set<String> schemas = Sets.newHashSet();
    File[] files = new File(ARCHIVE_ROOT).listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        schemas.add(file.getName());
      }
    }
    return schemas.equals(getSchemas());
  }

  /**
   * Validate whether the tables in TABLES is consistent with tables in schema fold in archive.
   *
   * @return
   */
  public static boolean validateTables(String schema) {
    checkArgument(schema != null);
    Set<String> tables = Sets.newHashSet();
    for (String tbl : gettblFileList(schema)) {
      tables.add(inferTableFromtbl(tbl));
    }
    return tables.equals(getTables(schema));
  }

  /**
   *
   * @param pattern
   * @return
   */
  private static List<String> getFileList(String schema, Pattern pattern) {
    checkArgument(schema != null && pattern != null);
    List<String> fileList = Lists.newArrayList();
    File[] files = Paths.get(ARCHIVE_ROOT, schema).toFile().listFiles();
    for (File file : files) {
      if (file.isFile()) {
        if (pattern.matcher(file.getName()).matches()) {
          fileList.add(file.getName());
        }
      }
    }
    return Collections.unmodifiableList(fileList);
  }

  /**
   * Helper class used to read information from info schema.
   *
   * @author Jihan Chen
   *
   */
  private static class InfoSchemaUtils {
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
        DBExceptions.newError(e);
        return null;
      }
    }

    public static List<Column> getColumns(String schema, String table) {
      checkArgument(schema != null && table != null);
      ArrayList<Column> toRet = Lists.newArrayList();
      try {
        List<String> lines = Files.readLines(columns, SystemProperties.getCharset());
        Collection<String> filtered = Collections2.filter(lines,
            Predicates.containsPattern("^" + schema + delimiter + table));
        for (String column : filtered) {
          String[] values = column.split(delimiter);
          String columnName = values[2];
          String
        }

      } catch (IOException e) {
        DBExceptions.newError(e);
        return null;
      }

      return toRet;
    }
  }

  /**
   * Helper class to update info schema.
   *
   * @author Jihan Chen
   *
   */
  private static class UpdateInfoSchema {

    /**
     * Update information schema when new schema is created.
     *
     * @param schema
     */
    public static void atCreatingSchema(String schema) {
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
    public static void atCreatingTable(String schema, String table, List<Column> cols) {
      try {
        Constraint con;
        StringBuilder sb = new StringBuilder();
        Files.append(NamingUtils.join(schema, table, 0), tables, SystemProperties.getCharset());
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
    public static boolean atDroppingTable(String schema, String table) {
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
    public static boolean atDeletingSchema(String schema) {
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

}
