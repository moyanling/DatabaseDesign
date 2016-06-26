package org.mo39.fmbh.databasedesign.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.COLUMNS;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.DELIMITER;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.INFO_SCHEMA;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.LINE_BREAK;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.SCHEMAS;
import static org.mo39.fmbh.databasedesign.framework.SystemProperties.TABLES;
import static org.mo39.fmbh.databasedesign.utils.FileUtils.tblRef;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.mo39.fmbh.databasedesign.framework.SystemProperties;
import org.mo39.fmbh.databasedesign.model.Column;
import org.mo39.fmbh.databasedesign.model.Constraint;
import org.mo39.fmbh.databasedesign.model.Constraint.NoConstraint;
import org.mo39.fmbh.databasedesign.model.Constraint.NotNull;
import org.mo39.fmbh.databasedesign.model.Constraint.PrimaryKey;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

abstract class InfoSchemaUtils {

  private static File schemata = tblRef(INFO_SCHEMA, SCHEMAS);
  private static File tables = tblRef(INFO_SCHEMA, TABLES);
  private static File columns = tblRef(INFO_SCHEMA, COLUMNS);

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

  /**
   * Update information schema when new schema is created.
   * 
   * @param schema
   */
  public static void updateAtCreatingSchema(String schema) {
    try {
      Files.append(schema + LINE_BREAK, schemata, SystemProperties.getCharset());
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
      Files.append(NamingUtils.join(schema, table, 0), tables, SystemProperties.getCharset());
      Constraint con;
      StringBuilder sb = new StringBuilder();
      for (Column col : cols) {
        sb.append(Joiner.on(DELIMITER).join(schema, table, col.getName(), cols.indexOf(col) + 1));
        con = col.getConstraint();
        sb.append(DELIMITER);
        if (con instanceof NoConstraint) {
          sb.append(Joiner.on(DELIMITER).join("YES", ""));
        } else if (con instanceof NotNull) {
          sb.append(Joiner.on(DELIMITER).join("NO", ""));
        } else if (con instanceof PrimaryKey) {
          sb.append(Joiner.on(DELIMITER).join("NO", "PRI"));
        }
        sb.append(LINE_BREAK);
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
      String pattern = "^" + schema + SystemProperties.DELIMITER + table;
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
