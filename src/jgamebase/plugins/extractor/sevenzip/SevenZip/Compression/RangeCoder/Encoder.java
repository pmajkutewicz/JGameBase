package jgamebase.plugins.extractor.sevenzip.SevenZip.Compression.RangeCoder;

import java.io.IOException;

public class Encoder {
  static final int kTopMask = ~((1 << 24) - 1);

  static final int kNumBitModelTotalBits = 11;
  static final int kBitModelTotal = (1 << kNumBitModelTotalBits);
  static final int kNumMoveBits = 5;

  java.io.OutputStream Stream;

  long Low;
  int Range;
  int _cacheSize;
  int _cache;

  long _position;

  public void SetStream(final java.io.OutputStream stream) {
    Stream = stream;
  }

  public void ReleaseStream() {
    Stream = null;
  }

  public void Init() {
    _position = 0;
    Low = 0;
    Range = -1;
    _cacheSize = 1;
    _cache = 0;
  }

  public void FlushData() throws IOException {
    for (int i = 0; i < 5; i++) {
      ShiftLow();
    }
  }

  public void FlushStream() throws IOException {
    Stream.flush();
  }

  public void ShiftLow() throws IOException {
    final int LowHi = (int) (Low >>> 32);
    if ((LowHi != 0) || (Low < 0xFF000000L)) {
      _position += _cacheSize;
      int temp = _cache;
      do {
        Stream.write(temp + LowHi);
        temp = 0xFF;
      } while (--_cacheSize != 0);
      _cache = (((int) Low) >>> 24);
    }
    _cacheSize++;
    Low = (Low & 0xFFFFFF) << 8;
  }

  public void EncodeDirectBits(final int v, final int numTotalBits) throws IOException {
    for (int i = numTotalBits - 1; i >= 0; i--) {
      Range >>>= 1;
      if (((v >>> i) & 1) == 1) {
        Low += Range;
      }
      if ((Range & Encoder.kTopMask) == 0) {
        Range <<= 8;
        ShiftLow();
      }
    }
  }

  public long GetProcessedSizeAdd() {
    return _cacheSize + _position + 4;
  }

  static final int kNumMoveReducingBits = 2;
  public static final int kNumBitPriceShiftBits = 6;

  public static void InitBitModels(final short[] probs) {
    for (int i = 0; i < probs.length; i++) {
      probs[i] = (kBitModelTotal >>> 1);
    }
  }

  public void Encode(final short[] probs, final int index, final int symbol) throws IOException {
    final int prob = probs[index];
    final int newBound = (Range >>> kNumBitModelTotalBits) * prob;
    if (symbol == 0) {
      Range = newBound;
      probs[index] = (short) (prob + ((kBitModelTotal - prob) >>> kNumMoveBits));
    } else {
      Low += (newBound & 0xFFFFFFFFL);
      Range -= newBound;
      probs[index] = (short) (prob - ((prob) >>> kNumMoveBits));
    }
    if ((Range & kTopMask) == 0) {
      Range <<= 8;
      ShiftLow();
    }
  }

  private static int[] ProbPrices = new int[kBitModelTotal >>> kNumMoveReducingBits];

  static {
    final int kNumBits = (kNumBitModelTotalBits - kNumMoveReducingBits);
    for (int i = kNumBits - 1; i >= 0; i--) {
      final int start = 1 << (kNumBits - i - 1);
      final int end = 1 << (kNumBits - i);
      for (int j = start; j < end; j++) {
        ProbPrices[j] = (i << kNumBitPriceShiftBits)
            + (((end - j) << kNumBitPriceShiftBits) >>> (kNumBits - i - 1));
      }
    }
  }

  static public int GetPrice(final int Prob, final int symbol) {
    return ProbPrices[(((Prob - symbol) ^ ((-symbol))) & (kBitModelTotal - 1)) >>> kNumMoveReducingBits];
  }

  static public int GetPrice0(final int Prob) {
    return ProbPrices[Prob >>> kNumMoveReducingBits];
  }

  static public int GetPrice1(final int Prob) {
    return ProbPrices[(kBitModelTotal - Prob) >>> kNumMoveReducingBits];
  }
}
