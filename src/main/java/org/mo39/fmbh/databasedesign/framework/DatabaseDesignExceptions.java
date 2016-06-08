package org.mo39.fmbh.databasedesign.framework;

public abstract class DatabaseDesignExceptions {

  public static class MissingAnnotationException extends Error {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MissingAnnotationException(String description) {
      super(description);
    }
  }

  public static class BadUsageException extends RuntimeException {

    /**
    *
    */
    private static final long serialVersionUID = 1L;

    public BadUsageException(String description){
      super(description);
    }

    public BadUsageException() {
    }

  }

}
