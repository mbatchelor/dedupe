package com.pentaho.dedupe.args.parsers;

import java.io.File;


public class FolderParser implements Parser<File> {

  @Override
  public boolean needsArg() {
    return true;
  }

  @Override
  public File parse(String value) {
    File result = new File(value).getAbsoluteFile();
    if (result.exists() && !result.isDirectory()) {
      throw new RuntimeException(value + " must refer to a valid folder");
    }
    return result;
  }

  @Override
  public String toString() {
    return "folder";
  }
}
