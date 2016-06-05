package org.mo39.fmbh.databasedesign.model;

public abstract class DatabaseDesignExceptions {

  public static class MissingAnnotationException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public MissingAnnotationException(String description) {
      super(description);
    }
  }

}
