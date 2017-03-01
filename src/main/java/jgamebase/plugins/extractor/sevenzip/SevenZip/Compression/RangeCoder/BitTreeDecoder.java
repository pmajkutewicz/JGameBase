package jgamebase.plugins.extractor.sevenzip.SevenZip.Compression.RangeCoder;

public class BitTreeDecoder {
  short[] Models;
  int NumBitLevels;

  public BitTreeDecoder(final int numBitLevels) {
    NumBitLevels = numBitLevels;
    Models = new short[1 << numBitLevels];
  }

  public void Init() {
    Decoder.InitBitModels(Models);
  }

  public int Decode(final Decoder rangeDecoder) throws java.io.IOException {
    int m = 1;
    for (int bitIndex = NumBitLevels; bitIndex != 0; bitIndex--) {
      m = (m << 1) + rangeDecoder.DecodeBit(Models, m);
    }
    return m - (1 << NumBitLevels);
  }

  public int ReverseDecode(final Decoder rangeDecoder) throws java.io.IOException {
    int m = 1;
    int symbol = 0;
    for (int bitIndex = 0; bitIndex < NumBitLevels; bitIndex++) {
      final int bit = rangeDecoder.DecodeBit(Models, m);
      m <<= 1;
      m += bit;
      symbol |= (bit << bitIndex);
    }
    return symbol;
  }

  public static int ReverseDecode(final short[] Models, final int startIndex,
      final Decoder rangeDecoder, final int NumBitLevels) throws java.io.IOException {
    int m = 1;
    int symbol = 0;
    for (int bitIndex = 0; bitIndex < NumBitLevels; bitIndex++) {
      final int bit = rangeDecoder.DecodeBit(Models, startIndex + m);
      m <<= 1;
      m += bit;
      symbol |= (bit << bitIndex);
    }
    return symbol;
  }
}
