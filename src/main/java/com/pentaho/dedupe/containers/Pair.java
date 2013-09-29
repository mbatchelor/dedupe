package com.pentaho.dedupe.containers;

import com.pentaho.dedupe.util.ComparisonUtil;

public class Pair<F, S> {
  private final F first;
  private final S second;
  private final HashImpl<F, S> hashImpl;
  @SuppressWarnings("rawtypes")
  private static final HashImpl DEFAULT = new HashImpl<Object, Object>() {

    @Override
    public int hashCode(Pair<Object, Object> pair) {
      int number = 1000;
      if (pair.first != null) {
        number += pair.first.hashCode();
      }
      if (pair.second != null) {
        number *= pair.second.hashCode();
      }
      return number;
    }

    @Override
    public boolean equals(Pair<Object, Object> first, Object secondObj) {
      Boolean result = ComparisonUtil.equalsNullCheck(first, secondObj);
      if (result != null) {
        return result;
      }
      if (!(secondObj instanceof Pair)) {
        return false;
      }
      @SuppressWarnings("unchecked")
      Pair<Object, Object> second = (Pair<Object, Object>) secondObj;
      result = ComparisonUtil.equalsNullCheck(first.first, second.first);
      if (result != null) {
        return result;
      }
      result = ComparisonUtil.equalsNullCheck(first.second, second.second);
      if (result != null) {
        return result;
      }
      return first.first.equals(second.first)
          && first.second.equals(second.second);
    }
  };

  public Pair(F first, S second, HashImpl<F, S> hashImpl) {
    this.first = first;
    this.second = second;
    this.hashImpl = hashImpl;
  }

  @SuppressWarnings("unchecked")
  public Pair(F first, S second) {
    this(first, second, DEFAULT);
  }

  public static <F, S> Pair<F, S> of(F first, S second) {
    return new Pair<F, S>(first, second);
  }

  public F getFirst() {
    return first;
  }

  public S getSecond() {
    return second;
  }

  @Override
  public boolean equals(Object obj) {
    return hashImpl.equals(this, obj);
  }

  @Override
  public int hashCode() {
    return hashImpl.hashCode(this);
  }

  @Override
  public String toString() {
    return "Pair[" + String.valueOf(first) + "," + String.valueOf(second) + "]";
  }

  public interface HashImpl<F, S> {
    public boolean equals(Pair<F, S> first, Object second);

    public int hashCode(Pair<F, S> pair);
  }
}
