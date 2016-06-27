
package org.mo39.fmbh.databasedesign.utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.mo39.fmbh.databasedesign.model.Column;

public abstract class IOUtils {

  private static final String ARCHIVE_ROOT = ".\\archive";

  public static void createNewTable(String schema, String table, List<Column> columns)
      throws IOException {
    String tbl = table + ".tbl";
    if(Paths.get(ARCHIVE_ROOT, schema, tbl).toFile().createNewFile()){
    //TODO Update system table
    }
  }

}
