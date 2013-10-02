package com.pentaho.dedupe.args.parsers;

public class BooleanParser implements Parser<Boolean> {

  @Override
  public boolean needsArg() {
    return false;
  }

  @Override
  public Boolean parse(String value) {
    return true;
  }

  @Override
  public String toString() {
    return "boolean";
  }
}
