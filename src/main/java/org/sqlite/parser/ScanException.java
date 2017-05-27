package org.sqlite.parser;

import java.util.Objects;

public class ScanException extends RuntimeException {
  private final ErrorCode code;

  ScanException(String message) {
    super(Objects.requireNonNull(message, "no msg"));
    this.code = ErrorCode.ScanError;
  }
  ScanException(String message, Throwable cause) {
    super(Objects.requireNonNull(message, "no msg"), cause);
    this.code = ErrorCode.ScanError;
  }

  ScanException(ErrorCode code) {
    super(code.name());
    this.code = code;
  }

  ErrorCode getCode() {
    return code;
  }
}
