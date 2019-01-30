package org.ljsn.clavardage.database;

public class DatabaseException extends RuntimeException {
  public DatabaseException() {
    super();
  }

  public DatabaseException(String errorMsg) {
    super(errorMsg);
  }
}
