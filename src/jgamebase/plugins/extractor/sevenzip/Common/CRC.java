package jgamebase.plugins.extractor.sevenzip.Common;

public class CRC {
  static public int[] Table = new int[256];

  static {
    for (int i = 0; i < 256; i++) {
      int r = i;
      for (int j = 0; j < 8; j++) {
        if ((r & 1) != 0) {
          r = (r >>> 1) ^ 0xEDB88320;
        } else {
          r >>>= 1;
        }
      }
      Table[i] = r;
    }
  }

  int _value = -1;

  public void Init() {
    _value = -1;
  }

  public void UpdateByte(final int b) {
    _value = Table[(_value ^ b) & 0xFF] ^ (_value >>> 8);
  }

  public void UpdateUInt32(final int v) {
    for (int i = 0; i < 4; i++) {
      UpdateByte((v >> (8 * i)) & 0xFF);
    }
  }

  public void UpdateUInt64(final long v) {
    for (int i = 0; i < 8; i++) {
      UpdateByte((int) ((v >> (8 * i))) & 0xFF);
    }
  }

  public int GetDigest() {
    return _value ^ (-1);
  }

  public void Update(final byte[] data, final int size) {
    for (int i = 0; i < size; i++) {
      _value = Table[(_value ^ data[i]) & 0xFF] ^ (_value >>> 8);
    }
  }

  public void Update(final byte[] data) {
    for (final byte element : data) {
      _value = Table[(_value ^ element) & 0xFF] ^ (_value >>> 8);
    }
  }

  public void Update(final byte[] data, final int offset, final int size) {
    for (int i = 0; i < size; i++) {
      _value = Table[(_value ^ data[offset + i]) & 0xFF] ^ (_value >>> 8);
    }
  }

  public static int CalculateDigest(final byte[] data, final int size) {
    final CRC crc = new CRC();
    crc.Update(data, size);
    return crc.GetDigest();
  }

  static public boolean VerifyDigest(final int digest, final byte[] data, final int size) {
    return (CalculateDigest(data, size) == digest);
  }
}
