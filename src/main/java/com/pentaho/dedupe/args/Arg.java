package com.pentaho.dedupe.args;

import com.pentaho.dedupe.args.flags.Flag;
import com.pentaho.dedupe.args.parsers.Parser;

public class Arg<T> {
  private final Flag flag;

  private final Parser<T> parser;

  private final T defaultValue;

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
    this.defaultValue = defaultValue;
  }

  public Arg(Flag flag, Parser<T> parser) {
    this(flag, parser, null);
  }

  @Override
  public String toString() {
    return flag + " (" + (this.defaultValue == null ? "" : "Default: " + this.defaultValue + ", ") + "type: " + parser
        + ")";
  }
}
