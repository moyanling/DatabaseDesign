package org.mo39.fmbh.databasedesign.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.UnrecognizableConstraintException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.UnrecognizableDataTypeException;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

import com.google.common.collect.Lists;

public class Column {

  private String name;
  private DataType dataType;
  private Constraint constraint;

  private static final String COLUMN_REGX = "^(.*?)\\s+(.*?)(\\s*?$|\\s+(.*?)\\s*?$)";

  private Column(String name, DataType dataType, Constraint constraint) {
    this.name = name;
    this.dataType = dataType;
    this.constraint = constraint;
  }

  public static List<Column> newColumnDefinition(String content) {
    ArrayList<Column> columns = Lists.newArrayList();
    try {
      for (String columnDef : content.split(",")) {
        String colDef = columnDef.trim();
        Pattern regx = Pattern.compile(COLUMN_REGX);
        Matcher matcher = regx.matcher(colDef);
        matcher = regx.matcher(colDef);
        // ----------------------
        if (!matcher.matches()) {
          throw new BadUsageException("Bad column definition: " + colDef);
        }
        // ----------------------
        String columnName = matcher.group(1).trim();
        if (!NamingUtils.checkNamingConventions(columnName)) {
          throw new BadUsageException("Bad column name: " + columnName);
        }
        // ----------------------
        String dataTypeStr = matcher.group(2).trim();
        DataType dataType = DataType.supports(dataTypeStr);
        if (dataType == null) {
          throw new UnrecognizableDataTypeException("Unsupported data type: " + dataTypeStr);
        }
        // ----------------------
        String constraintStr = matcher.group(3).trim();
        Constraint constraint = Constraint.supports(constraintStr);
        if (constraint == null) {
          throw new UnrecognizableConstraintException("Unsupported constraint: " + constraintStr);
        }
        columns.add(new Column(columnName, dataType, constraint));
      }
    } catch (Exception e) {
      DBExceptions.newError(e);
    }
    return columns;

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

}
