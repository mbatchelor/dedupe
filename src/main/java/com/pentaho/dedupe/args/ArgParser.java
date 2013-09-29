package com.pentaho.dedupe.args;

import java.util.ArrayList;
import java.util.List;

import com.pentaho.dedupe.args.flags.StandardFlag;
import com.pentaho.dedupe.args.parsers.Parser;

public class ArgParser {
  private final List<Arg<?>> args = new ArrayList<Arg<?>>();
  
  public ArgParser() {
    register (new Arg<Object>(new StandardFlag("h", "help", "Show help message and exit"), new Parser<Object>() {

      @Override
      public boolean needsArg() {
        return false;
      }

      @Override
      public Object parse(String value) {
        for(Arg<?> arg:args) {
          System.out.println(arg);
        }
        System.exit(0);
        return null;
      }
      
      @Override
      public String toString() {
        return "N/A";
      }
    }));
  }
  
  public void register(Arg<?> arg) {
    args.add(arg);
  }
  
  private Arg<?> getArg(String value) {
    for (Arg<?> arg : args) {
      if (arg.matches(value)) {
        return arg;
      }
    }
    return null;
  }
  
  public void parse(String[] args) {
    for (int i = 0; i < args.length; i++) {
      Arg<?> arg = getArg(args[i]);
      if (arg != null) {
        if (arg.needsArg()) {
          if (i < args.length - 1) {
            arg.process(args[i+1]);
            i++;
          } else {
            throw new RuntimeException("Argument " + arg + " requires an option but the end of the argument string was reached.");
          }
        } else {
          arg.process(null);
        }
      }
    }
  }
}
