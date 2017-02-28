package jgamebase.plugins.extractor.sevenzip.SevenZip.Compression.RangeCoder;

import java.io.IOException;

public class BitTreeEncoder {
  short[] Models;
  int NumBitLevels;

  public BitTreeEncoder(final int numBitLevels) {
    NumBitLevels = numBitLevels;
    Models = new short[1 << numBitLevels];
  }

  public void Init() {
    Decoder.InitBitModels(Models);
  }

  public void Encode(final Encoder rangeEncoder, final int symbol) throws IOException {
    int m = 1;
    for (int bitIndex = NumBitLevels; bitIndex != 0;) {
      bitIndex--;
      final int bit = (symbol >>> bitIndex) & 1;
      rangeEncoder.Encode(Models, m, bit);
      m = (m << 1) | bit;
    }
  }

  public void ReverseEncode(final Encoder rangeEncoder, int symbol) throws IOException {
    int m = 1;
    for (int i = 0; i < NumBitLevels; i++) {
      final int bit = symbol & 1;
      rangeEncoder.Encode(Models, m, bit);
      m = (m << 1) | bit;
      symbol >>= 1;
    }
  }

  public int GetPrice(final int symbol) {
    int price = 0;
    int m = 1;
    for (int bitIndex = NumBitLevels; bitIndex != 0;) {
      bitIndex--;
      final int bit = (symbol >>> bitIndex) & 1;
      price += Encoder.GetPrice(Models[m], bit);
      m = (m << 1) + bit;
    }
    return price;
  }

  public int ReverseGetPrice(int symbol) {
    int price = 0;
    int m = 1;
    for (int i = NumBitLevels; i != 0; i--) {
      final int bit = symbol & 1;
      symbol >>>= 1;
      price += Encoder.GetPrice(Models[m], bit);
      m = (m << 1) | bit;
    }
    return price;
  }

  public static int ReverseGetPrice(final short[] Models, final int startIndex,
      final int NumBitLevels, int symbol) {
    int price = 0;
    int m = 1;
    for (int i = NumBitLevels; i != 0; i--) {
      final int bit = symbol & 1;
      symbol >>>= 1;
      price += Encoder.GetPrice(Models[startIndex + m], bit);
      m = (m << 1) | bit;
    }
    return price;
  }

  public static void ReverseEncode(final short[] Models, final int startIndex,
      final Encoder rangeEncoder, final int NumBitLevels, int symbol) throws IOException {
    int m = 1;
    for (int i = 0; i < NumBitLevels; i++) {
      final int bit = symbol & 1;
      rangeEncoder.Encode(Models, startIndex + m, bit);
      m = (m << 1) | bit;
      symbol >>= 1;
    }
  }
}
