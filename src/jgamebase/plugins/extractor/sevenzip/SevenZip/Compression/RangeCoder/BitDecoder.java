package jgamebase.plugins.extractor.sevenzip.SevenZip.Compression.RangeCoder;


public class BitDecoder extends BitModel
{
    public BitDecoder(final int num) {
        super(num);
    }
  public int Decode(final Decoder decoder)  throws java.io.IOException
  {
    final int newBound = (decoder.Range >>> kNumBitModelTotalBits) * Prob;
    if ((decoder.Code ^ 0x80000000) < (newBound ^ 0x80000000))
    {
      decoder.Range = newBound;
      Prob += (kBitModelTotal - Prob) >>> numMoveBits;
      if ((decoder.Range & kTopMask) == 0)
      {
        decoder.Code = (decoder.Code << 8) | decoder.bufferedStream.read();
        decoder.Range <<= 8;
      }
      return 0;
    }
    else
    {
      decoder.Range -= newBound;
      decoder.Code -= newBound;
      Prob -= (Prob) >>> numMoveBits;
      if ((decoder.Range & kTopMask) == 0)
      {
        decoder.Code = (decoder.Code << 8) | decoder.bufferedStream.read();
        decoder.Range <<= 8;
      }
      return 1;
    }
  }
}
