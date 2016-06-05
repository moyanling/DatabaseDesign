package org.mo39.fmbh.databasedesign.model;

public abstract class DatabaseDesignExceptions {

  public static class MissingAnnotationException extends RuntimeException {

    public MissingAnnotationException(String description) {
      super(description);
    }
  }

}
