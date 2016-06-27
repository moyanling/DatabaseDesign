package org.mo39.fmbh.databasedesign.model;

/**
 * Database Design Exceptions.
 *
 * @author Jihan Chen
 *
 */
@SuppressWarnings("serial")
public class DBExceptions extends Exception {

  public DBExceptions() {}

  public DBExceptions(String description) {
    super(description);
  }

  /**
   * Indicates a bad usage for input command.
   *
   * @author Jihan Chen
   *
   */
  public static class BadUsageException extends DBExceptions {

    private static final long serialVersionUID = 1L;

    public BadUsageException() {}

    public BadUsageException(String description) {
      super(description);
    }
  }
  /**
   * Indicates the insert record does not observe the constraint.
   *
   * @author Jihan Chen
   *
   */
  public static class ConstraintViolationException extends DBExceptions {
    private static final long serialVersionUID = 1L;

    public ConstraintViolationException() {}

    public ConstraintViolationException(String description) {
      super(description);
    }
  }

  /**
   * Indicates that the data type cannot be recognized or is not supported in current design.
   *
   * @author Jihan Chen
   *
   */
  public static class UnrecognizableDataTypeException extends DBExceptions {

    private static final long serialVersionUID = 1L;

    public UnrecognizableDataTypeException() {}

    public UnrecognizableDataTypeException(String description) {
      super(description);
    }
  }

  /**
   * Indicates that the constraint cannot be recognized or is not supported in current design.
   *
   * @author Jihan Chen
   *
   */
  public static class UnrecognizableConstraintException extends DBExceptions {

    private static final long serialVersionUID = 1L;

    public UnrecognizableConstraintException() {}

    public UnrecognizableConstraintException(String description) {
      super(description);
    }

  }

  /**
   * Indicates that the folders and tables in archive is not consistent with the information
   * provided by Information_Schema.
   *
   * @author Jihan Chen
   *
   */
  public static class InvalidInformationSchemaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidInformationSchemaException() {}

    public InvalidInformationSchemaException(String description) {
      super(description);
    }
  }

}
