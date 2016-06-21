package org.mo39.fmbh.databasedesign.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mo39.fmbh.databasedesign.model.DBExceptions.BadUsageException;
import org.mo39.fmbh.databasedesign.utils.NamingUtils;

public class Column {

  private String name;
  private DataType<?> dataType;
  private Constraint constraint;
  private Object value;

  private static final String COLUMN_DEFINITION = "^(.*?)\\s+(.*?)(\\s*?$|\\s+(.*?)\\s*?$)";

  public Column(String name, DataType<?> dataType, Constraint constraint, Object value) {
    this.name = name;
    this.value = value;
    this.dataType = dataType;
    this.constraint = constraint;
  }

  public static Column newColumnDefinition(String colDefinitionStr) throws BadUsageException {
    Pattern regx = Pattern.compile(COLUMN_DEFINITION);
    Matcher matcher = regx.matcher(colDefinitionStr);
    matcher = regx.matcher(colDefinitionStr);
    // ----------------------
    if (!matcher.matches()) {
      throw new BadUsageException("Bad column definition.");
    }
    // ----------------------
    String columnName = matcher.group(1).trim();
    if (!NamingUtils.checkNamingConventions(columnName)) {
      throw new BadUsageException("Bad column name.");
    }
    // ----------------------
    String dataTypeStr = matcher.group(2).trim();
    DataType<?> dataType = DataType.supports(dataTypeStr);
    if (dataType == null) {
      throw new BadUsageException("Unsupported data type.");
    }
    // ----------------------
    String constraintStr = matcher.group(3).trim();
    Constraint constraint = Constraint.supports(constraintStr);
    if (constraint == null) {
      throw new BadUsageException("Unsupported constraint.");
    }
    return new Column(columnName, dataType, constraint, null);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DataType<?> getDataType() {
    return dataType;
  }

  public void setDataType(DataType<?> dataType) {
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
