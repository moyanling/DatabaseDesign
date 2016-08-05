package org.mo39.fmbh.databasedesign.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.model.Constraint.PrimaryKey;
import org.mo39.fmbh.databasedesign.model.DBExceptions.DuplicatePrimaryKeyException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.UnrecognizableConstraintException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.UnrecognizableDataTypeException;
import org.mo39.fmbh.databasedesign.utils.DbChecker;

import com.google.common.collect.Lists;

public class Column implements Comparable<Column> {

  private String name;
  private DataType dataType;
  private Constraint constraint;
  private int ordinalPosi;

  private static final String COLUMN_REGX = "^(.*?)\\s+(.*?)(\\s*?$|\\s+(.*?)\\s*?$)";

  public Column(String name, DataType dataType, Constraint constraint, int ordinalPosi) {
    this.name = name;
    this.dataType = dataType;
    this.constraint = constraint;
    this.ordinalPosi = ordinalPosi;
  }

  /**
   * Parse a string content which defines the columns for a table, i.e. the content in the
   * parenthesis of a create table command.
   *
   * @param content
   * @return List<Column> containing several Column objects.
   */
  public static List<Column> newColumnDefinition(String content) {
    // only one primary key. count the number of primary key
    int count = 0;
    // Count ordinal position for each Column.
    int ordinalPosi = 0;
    ArrayList<Column> columns = Lists.newArrayList();
    try {
      for (String columnDef : content.split(",")) {
        ordinalPosi++;
        String colDef = columnDef.trim();
        Pattern regx = Pattern.compile(COLUMN_REGX);
        Matcher m = regx.matcher(colDef);
        // ----------------------
        DbChecker.checkSyntax(m);
        String columnName = DbChecker.checkName(m, 1);
        // ----------------------
        String dataTypeStr = m.group(2).trim();
        DataType dataType = DataType.valueOf(dataTypeStr);
        if (dataType == null) {
          throw new UnrecognizableDataTypeException("Unsupported data type: " + dataTypeStr);
        }
        // ----------------------
        String constraintStr = m.group(3).trim();
        Constraint constraint = Constraint.valueOf(constraintStr);
        if (constraint == null) {
          throw new UnrecognizableConstraintException("Unsupported constraint: " + constraintStr);
        }
        if (constraint instanceof PrimaryKey) {
          count++;
          if (count > 1) {
            throw new DuplicatePrimaryKeyException("More than one primary key is assigned.");
          }
        }
        columns.add(new Column(columnName, dataType, constraint, ordinalPosi));
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
    return columns;
  }

  @Override
  public int compareTo(Column o) {
    if (o instanceof Column) {
      Column col = Column.class.cast(o);
      return ordinalPosi - col.getOrdinalPosi();
    }
    return 0;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DataType getDataType() {
    return dataType;
  }

  public void setDataType(DataType dataType) {
    this.dataType = dataType;
  }

  public Constraint getConstraint() {
    return constraint;
  }

  public void setConstraint(Constraint constraint) {
    this.constraint = constraint;
  }

  public int getOrdinalPosi() {
    return ordinalPosi;
  }

  public void setOrdinalPosi(int ordinalPosi) {
    this.ordinalPosi = ordinalPosi;
  }


}
