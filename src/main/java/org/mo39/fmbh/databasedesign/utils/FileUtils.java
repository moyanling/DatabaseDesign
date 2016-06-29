package org.mo39.fmbh.databasedesign.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static org.mo39.fmbh.databasedesign.utils.NamingUtils.inferTableFromtbl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.framework.InfoSchema;
import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.model.Column;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class handles file level process within the archive. It helps to analyze certain file name,
 * acquire file names and delete files. It does not support reading or writing the content of the
 * file.
 *
 * @author Jihan Chen
 *
 */
public abstract class FileUtils {

  static final String ARCHIVE_ROOT = SystemProperties.get("archiveRoot");

  static final Pattern NDX_FILE = Pattern.compile(".*\\.ndx");
  static final Pattern TBL_FILE = Pattern.compile(".*\\.tbl");

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
      InfoSchema.updateAtCreatingSchema(schema);
      return true;
    }
    return false;
  }

  /**
   * This function will take schema as a folder and table as the tbl file name. Unless the schema is
   * information_schema.
   * 
   * @param schema
   * @param table
   * @throws IOException
   */
  public static boolean createtblFile(String schema, String table, List<Column> columns)
      throws IOException {
    if (tblRef(schema, table).createNewFile()) {
      InfoSchema.updateAtCreatingTable(schema, table, columns);
      return true;
    }
    return false;
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
      if (InfoSchema.updateAtDroppingTable(schema, table)) {
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
      if (InfoSchema.updateAtDeletingSchema(schema)) {
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
    return InfoSchema.getSchemas();
  }



  /**
   * Get tables for specified schema.
   *
   * @param schemaName
   * @return A unmodifiable set containing all tables in the archive.
   *         {@link Collections#unmodifiableSet}
   */
  public static Set<String> getTables(String schema) {
    return InfoSchema.getTables(schema);
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

}
