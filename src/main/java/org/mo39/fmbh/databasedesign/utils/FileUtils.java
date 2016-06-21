package org.mo39.fmbh.databasedesign.utils;

import static org.mo39.fmbh.databasedesign.utils.NamingUtils.inferTableFromtbl;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class handles file level process within the archive. It helps to anaylise certain file name,
 * acquire file names and delete files. It does not support reading or writing the content of the
 * file.
 *
 * @author Jihan Chen
 *
 */
public abstract class FileUtils {

  private static final String ARCHIVE_ROOT = ".\\archive";
  private static final Pattern NDX_FILE = Pattern.compile(".*\\.ndx");
  private static final Pattern TBL_FILE = Pattern.compile(".*\\.tbl");

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
   * Delete table in the archive
   *
   * @param schemaName
   * @param tableName
   * @return true if delete successfully else false.
   */
  public static boolean deleteTable(String schemaName, String tableName) {
    String fileName = tableName + ".tbl";
    Path path = Paths.get(ARCHIVE_ROOT, schemaName, fileName);
    return path.toFile().delete();
  }

  /**
   * Delete a schema in the archive.
   *
   * @param schemaName
   * @return true if delete successfully else false.
   */
  public static boolean deleteSchema(String schema) {
    for (String tblFileName : gettblFileList(schema)) {
      if (!Paths.get(ARCHIVE_ROOT, schema, tblFileName).toFile().delete()) {
        return false;
      }
    }
    return Paths.get(ARCHIVE_ROOT, schema).toFile().delete();

  }

  /**
   * Get schemas in the archive
   *
   * @return A unmodifiable set containing all schemas in the archive.
   *         {@link Collections#unmodifiableSet}
   */
  public static Set<String> getSchemas() {
    Set<String> schemas = Sets.newHashSet();
    File[] files = new File(ARCHIVE_ROOT).listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        schemas.add(file.getName());
      }
    }
    return schemas;
  }

  /**
   * Get tables for specified schema.
   *
   * @param schemaName
   * @return A unmodifiable set containing all tables in the archive.
   *         {@link Collections#unmodifiableSet}
   */
  public static Set<String> getTables(String schema) {
    Set<String> set = Sets.newHashSet();
    for (String tbl : gettblFileList(schema)) {
      set.add(inferTableFromtbl(tbl));
    }
    return Collections.unmodifiableSet(set);
  }

  /**
   *
   * @param pattern
   * @return
   */
  private static List<String> getFileList(String schema, Pattern pattern) {
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
