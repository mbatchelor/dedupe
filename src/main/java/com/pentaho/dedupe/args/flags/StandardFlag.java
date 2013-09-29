package com.pentaho.dedupe.args.flags;

import com.pentaho.dedupe.args.Flag;

public class StandardFlag implements Flag {
  private final String abbreviatedArg;
  private final String extendedArg;
  private final String description;

  public StandardFlag(String abbreviatedArg, String extendedArg,
      String description) {
    this.abbreviatedArg = abbreviatedArg;
    this.extendedArg = extendedArg;
    this.description = description;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    if (abbreviatedArg != null) {
      result.append(", ");
      result.append("-");
      result.append(abbreviatedArg);
    }
    if (extendedArg != null) {
      result.append(", ");
      result.append("--");
      result.append(extendedArg);
    }
    if (description != null) {
      result.append(", ");
      result.append(description);
    }
    String output = result.toString();
    if (output.length() > 1) {
      output = output.substring(2);
    }
    return output;
  }

  @Override
  public boolean matches(String flag) {
    return (abbreviatedArg != null && ("-" + abbreviatedArg).equals(flag))
        || (extendedArg != null && ("--" + extendedArg).equals(flag));
  }
}
