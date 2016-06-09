package org.mo39.fmbh.databasedesign.utils;

import static org.mo39.fmbh.databasedesign.utils.NamingUtils.inferSchemaFromtbl;
import static org.mo39.fmbh.databasedesign.utils.NamingUtils.inferTableFromtbl;

import java.io.File;
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

  public static List<String> gettblFileList() {
    return getFileList(TBL_FILE);
  }

  public static List<String> getndxFileList() {
    return getFileList(NDX_FILE);
  }

  public static Set<String> getSchemas() {
    Set<String> set = Sets.newHashSet();
    for (String tbl : gettblFileList()) {
      set.add(inferSchemaFromtbl(tbl));
    }
    return Collections.unmodifiableSet(set);
  }

  public static Set<String> getTables(String schemaName) {
    Set<String> set = Sets.newHashSet();
    for (String tbl : gettblFileList()) {
      if (schemaName.equals(inferSchemaFromtbl(tbl))) {
        set.add(inferTableFromtbl(tbl));
      }
    }
    return Collections.unmodifiableSet(set);
  }

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
    return tableFileList;
  }

}
