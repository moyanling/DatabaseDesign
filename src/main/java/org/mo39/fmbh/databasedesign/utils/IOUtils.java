package org.mo39.fmbh.databasedesign.utils;

import static org.mo39.fmbh.databasedesign.utils.NamingUtils.inferSchemaFromtbl;
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

public class IOUtils {

  private static final String ARCHIVE_ROOT = ".\\archive";
  private static final Pattern NDX_FILE = Pattern.compile(".*\\.ndx");
  private static final Pattern TBL_FILE = Pattern.compile(".*\\.tbl");

  /**
   * Get table file list. i.e. the file ends with .tbl.
   *
   * @return A unmodifiable list containing strings as the tbl file name.
   *         {@link Collections#unmodifiableList}
   */
  public static List<String> gettblFileList() {
    return getFileList(TBL_FILE);
  }

  /**
   * Get index file list. i.e. the file ends with .ndx
   *
   * @return A unmodifiable list containing strings as the ndx file name.
   *         {@link Collections#unmodifiableList}
   */
  public static List<String> getndxFileList() {
    return getFileList(NDX_FILE);
  }

  /**
   * Delete table in the archive
   *
   * @param schemaName
   * @param tableName
   * @return true if delete successfully else false.
   */
  public static boolean deleteTable(String schemaName, String tableName) {
    String fileName = schemaName + "." + tableName + ".tbl";
    Path path = Paths.get(ARCHIVE_ROOT, fileName);
    return path.toFile().delete();
  }

  /**
   * Delete a schema in the archive.
   * <p>
   * This function is dangerous. Use it carefully. For example, a schema contains three tables, the
   * function may delete two but fails at the third one, in which case it merely returns false. Some
   * more actions are necessary to enforce it's function. //TODO
   *
   * @param schemaName
   * @return true if delete successfully else false.
   */
  public static boolean deleteSchema(String schemaName) {
    for (String tblFileName : gettblFileList()) {
      if (tblFileName.startsWith(schemaName)) {
        if (!Paths.get(ARCHIVE_ROOT, tblFileName).toFile().delete()) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Get schemas in the archive
   *
   * @return A unmodifiable set containing all schemas in the archive.
   *         {@link Collections#unmodifiableSet}
   */
  public static Set<String> getSchemas() {
    Set<String> set = Sets.newHashSet();
    for (String tbl : gettblFileList()) {
      set.add(inferSchemaFromtbl(tbl));
    }
    return Collections.unmodifiableSet(set);
  }

  /**
   * Get tables for specified schema.
   *
   * @param schemaName
   * @return A unmodifiable set containing all tables in the archive.
   *         {@link Collections#unmodifiableSet}
   */
  public static Set<String> getTables(String schemaName) {
    Set<String> set = Sets.newHashSet();
    for (String tbl : gettblFileList()) {
      if (schemaName.equals(inferSchemaFromtbl(tbl))) {
        set.add(inferTableFromtbl(tbl));
      }
    }
    return Collections.unmodifiableSet(set);
  }

  /**
   *
   * @param pattern
   * @return
   */
  private static List<String> getFileList(Pattern pattern) {
    List<String> tableFileList = Lists.newArrayList();
    File file = new File(ARCHIVE_ROOT);
    File fileList[] = file.listFiles();
    for (int i = 0; i < fileList.length; i++) {
      if (fileList[i].isFile()) {
        if (pattern.matcher(fileList[i].getName()).matches()) {
          tableFileList.add(fileList[i].getName());
        }
      }
    }
    return Collections.unmodifiableList(tableFileList);
  }

}
