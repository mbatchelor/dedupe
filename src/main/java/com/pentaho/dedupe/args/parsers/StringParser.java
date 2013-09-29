package com.pentaho.dedupe.args.parsers;

public class StringParser implements Parser<String> {

  @Override
  public boolean needsArg() {
    return true;
  }

  @Override
  public String parse(String value) {
    return value;
  }

  @Override
  public String toString() {
    return "string";
  }
}
