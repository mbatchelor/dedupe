package com.pentaho.dedupe.args;

import com.pentaho.dedupe.args.parsers.Parser;

public class Arg<T> {
  private final Flag flag;
  private final Parser<T> parser;
  private T value;

  public T get() {
    return value;
  }

  public void process(String value) {
    this.value = parser.parse(value);
  }

  public boolean matches(String flag) {
    return this.flag.matches(flag);
  }

  public boolean needsArg() {
    return parser.needsArg();
  }

  public Arg(Flag flag, Parser<T> parser, T defaultValue) {
    this.flag = flag;
    this.parser = parser;
    this.value = defaultValue;
  }

  public Arg(Flag flag, Parser<T> parser) {
    this(flag, parser, null);
  }

  @Override
  public String toString() {
    return flag + "(type: " + parser + ")";
  }
}
