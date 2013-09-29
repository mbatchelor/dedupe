package com.pentaho.dedupe.util;

public class ComparisonUtil {
  public static Boolean equalsNullCheck(Object first, Object second) {
    if (first == null) {
      if(second == null) {
        return Boolean.TRUE;
      } else {
        return Boolean.FALSE;
      }
    } else {
      if (second == null) {
        return Boolean.FALSE;
      } else {
        return null;
      }
    }
  }
}
