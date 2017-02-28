package jgamebase.plugins.extractor.sevenzip.Common;

public class BoolVector {

  protected boolean[] data = new boolean[10];
  int capacityIncr = 10;
  int elt = 0;

  public BoolVector() {
  }

  public int size() {
    return elt;
  }

  private void ensureCapacity(final int minCapacity) {
    final int oldCapacity = data.length;
    if (minCapacity > oldCapacity) {
      final boolean[] oldData = data;
      int newCapacity = oldCapacity + capacityIncr;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }
      data = new boolean[newCapacity];
      System.arraycopy(oldData, 0, data, 0, elt);
    }
  }

  public boolean get(final int index) {
    if (index >= elt) {
      throw new ArrayIndexOutOfBoundsException(index);
    }

    return data[index];
  }

  public void Reserve(final int s) {
    ensureCapacity(s);
  }

  public void add(final boolean b) {
    ensureCapacity(elt + 1);
    data[elt++] = b;
  }

  public void clear() {
    elt = 0;
  }

  public boolean isEmpty() {
    return elt == 0;
  }
}
