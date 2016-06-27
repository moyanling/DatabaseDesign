package org.mo39.fmbh.databasedesign.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.UnrecognizableConstraintException;
import org.mo39.fmbh.databasedesign.model.DBExceptions.UnrecognizableDataTypeException;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

public class Column {

  private String name;
  private DataType dataType;
  private Constraint constraint;
  private Object value;

  private static final String COLUMN_DEFINITION = "^(.*?)\\s+(.*?)(\\s*?$|\\s+(.*?)\\s*?$)";

  public Column(String name, DataType dataType, Constraint constraint, Object value) {
    this.name = name;
    this.value = value;
    this.dataType = dataType;
    this.constraint = constraint;
  }

  public static Column newColumnDefinition(String columnDef) throws DBExceptions {
    String colDef = columnDef.trim();
    Pattern regx = Pattern.compile(COLUMN_DEFINITION);
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
    return new Column(columnName, dataType, constraint, null);
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

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

}
