package com.pentaho.dedupe.args.parsers;

public interface Parser<T> {
  public boolean needsArg();
  public T parse(String value);
}
