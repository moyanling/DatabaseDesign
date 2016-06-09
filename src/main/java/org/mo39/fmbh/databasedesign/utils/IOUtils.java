package org.mo39.fmbh.databasedesign.utils;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class IOUtils {

  private static final String ARCHIVE_ROOT = ".\\archive";

  public static List<String> getTableFileList() {
    List<String> tableFileList = Lists.newArrayList();
    File file = new File(ARCHIVE_ROOT);
    File fileList[] = file.listFiles();
    for (int i = 0; i < fileList.length; i++) {
      if (fileList[i].isFile()) {
        tableFileList.add(fileList[i].getName());
      }
    }
    return tableFileList;
  }

  public static Set<String> getSchemas() {
    Set<String> set = Sets.newHashSet();
    for (String tbl : getTableFileList()) {
      set.add(NamingUtils.inferSchemaFromtbl(tbl));
    }
    return Collections.unmodifiableSet(set);
  }

}
