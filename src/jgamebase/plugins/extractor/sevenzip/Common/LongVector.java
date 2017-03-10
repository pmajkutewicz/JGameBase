package jgamebase.plugins.extractor.sevenzip.Common;

public class LongVector {
  protected long[] data = new long[10];
  int capacityIncr = 10;
  int elt = 0;

  public LongVector() {
  }

  public int size() {
    return elt;
  }

  private void ensureCapacity(final int minCapacity) {
    final int oldCapacity = data.length;
    if (minCapacity > oldCapacity) {
      final long[] oldData = data;
      int newCapacity = oldCapacity + capacityIncr;
      if (newCapacity < minCapacity) {
        newCapacity = minCapacity;
      }
      data = new long[newCapacity];
      System.arraycopy(oldData, 0, data, 0, elt);
    }
  }

  public long get(final int index) {
    if (index >= elt) {
      throw new ArrayIndexOutOfBoundsException(index);
    }

    return data[index];
  }

  public void Reserve(final int s) {
    ensureCapacity(s);
  }

  public void add(final long b) {
    ensureCapacity(elt + 1);
    data[elt++] = b;
  }

  public void clear() {
    elt = 0;
  }

  public boolean isEmpty() {
    return elt == 0;
  }

  public long Back() {
    if (elt < 1) {
      throw new ArrayIndexOutOfBoundsException(0);
    }

    return data[elt - 1];
  }

  public long Front() {
    if (elt < 1) {
      throw new ArrayIndexOutOfBoundsException(0);
    }

    return data[0];
  }

  public void DeleteBack() {
    // Delete(_size - 1);
    remove(elt - 1);
  }

  public long remove(final int index) {
    if (index >= elt) {
      throw new ArrayIndexOutOfBoundsException(index);
    }
    final long oldValue = data[index];

    final int numMoved = elt - index - 1;
    if (numMoved > 0) {
      System.arraycopy(elt, index + 1, elt, index, numMoved);
    }

    // data[--elt] = null; // Let gc do its work

    return oldValue;
  }

}
