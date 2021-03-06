/*
 * Copyright (C) 2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iq80.snappy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HadoopSnappyCodec
        implements CompressionCodec {
  /**
   * Here is an excerpt of code from SequenceFile$Reader.init():
   * <p>
   * ...
   * valBuffer = new DataInputBuffer();
   * if (decompress) {
   * valDecompressor = CodecPool.getDecompressor(codec);
   * valInFilter = codec.createInputStream(valBuffer, valDecompressor);
   * valIn = new DataInputStream(valInFilter);
   * } else {
   * valIn = valBuffer;
   * ...
   * </p>
   * It creates a stream on an empty buffer. During processing it continuously re-fills this buffer.
   * This implies two things:<br>
   *   <ol>
   *     <li>Reading a stream header will not work.</li>
   *     <li>Although it retrieves the decompressor, it actually never uses it. Hence, delaying the exception to when
   *         the decompressor is actually used will make the codec implementation work for SequenceFile.</li>
   *   </ol>
   */
  private static VoidCompressor _compressor = new VoidCompressor();
  private static VoidDecompressor _decompressor = new VoidDecompressor();

  @Override
  public CompressionOutputStream createOutputStream(OutputStream outputStream)
          throws IOException {
    return new SnappyCompressionOutputStream(outputStream);
  }

  @Override
  public CompressionOutputStream createOutputStream(OutputStream outputStream, Compressor compressor)
          throws IOException {
    assert compressor == _compressor;
    System.out.println("HERE: Creating Snappy output stream ... ");
    return createOutputStream(outputStream);
  }

  @Override
  public Class<? extends Compressor> getCompressorType() {
    return VoidCompressor.class;
  }

  @Override
  public Compressor createCompressor() {
    return _compressor;
  }

  @Override
  public CompressionInputStream createInputStream(InputStream inputStream)
          throws IOException {
    return new SnappyCompressionInputStream(inputStream);
  }

  @Override
  public CompressionInputStream createInputStream(InputStream inputStream, Decompressor decompressor)
          throws IOException {
    assert decompressor == _decompressor;
    return createInputStream(inputStream);
  }

  @Override
  public Class<? extends Decompressor> getDecompressorType() {
    return VoidDecompressor.class;
  }

  @Override
  public Decompressor createDecompressor() {
    return _decompressor;
  }

  @Override
  public String getDefaultExtension() {
    return ".snappy";
  }

  private static class SnappyCompressionOutputStream
          extends CompressionOutputStream {
    public SnappyCompressionOutputStream(OutputStream outputStream)
            throws IOException {
      super(new SnappyOutputStream(outputStream));
    }

    @Override
    public void write(byte[] b, int off, int len)
            throws IOException {
      out.write(b, off, len);
    }

    @Override
    public void finish()
            throws IOException {
      out.flush();
    }

    @Override
    public void resetState()
            throws IOException {
      out.flush();
    }

    @Override
    public void write(int b)
            throws IOException {
      out.write(b);
    }
  }

  private static class SnappyCompressionInputStream
          extends CompressionInputStream {
    public SnappyCompressionInputStream(InputStream inputStream)
            throws IOException {
      super(new SnappyInputStream(inputStream));
    }

    @Override
    public int read(byte[] b, int off, int len)
            throws IOException {
      return in.read(b, off, len);
    }

    @Override
    public void resetState()
            throws IOException {
      // nothing here
//            throw new UnsupportedOperationException("resetState not supported for Snappy");
    }

    @Override
    public int read()
            throws IOException {
      return in.read();
    }
  }

  private static class VoidCompressor implements Compressor {

    @Override
    public void setInput(byte[] b, int off, int len) {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public boolean needsInput() {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public void setDictionary(byte[] b, int off, int len) {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public long getBytesRead() {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public long getBytesWritten() {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public void finish() {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public boolean finished() {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public int compress(byte[] b, int off, int len) throws IOException {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public void reset() {
      // nothing to be reset
    }

    @Override
    public void end() {
      throw new UnsupportedOperationException("Snappy Compressor is not supported");
    }

    @Override
    public void reinit(Configuration configuration) {
      // nothing
    }
  }

  private static class VoidDecompressor implements Decompressor {

    @Override
    public void setInput(byte[] b, int off, int len) {
      throw new UnsupportedOperationException("Snappy Decompressor is not supported");
    }

    @Override
    public boolean needsInput() {
      throw new UnsupportedOperationException("Snappy Decompressor is not supported");
    }

    @Override
    public void setDictionary(byte[] b, int off, int len) {
      throw new UnsupportedOperationException("Snappy Decompressor is not supported");
    }

    @Override
    public boolean needsDictionary() {
      throw new UnsupportedOperationException("Snappy Decompressor is not supported");
    }

    @Override
    public boolean finished() {
      throw new UnsupportedOperationException("Snappy Decompressor is not supported");
    }

    @Override
    public int decompress(byte[] b, int off, int len) throws IOException {
      throw new UnsupportedOperationException("Snappy Decompressor is not supported");
    }

    @Override
    public int getRemaining() {
      throw new UnsupportedOperationException("Snappy Decompressor is not supported");
    }

    @Override
    public void reset() {
      // nothing to be reset
    }

    @Override
    public void end() {
      throw new UnsupportedOperationException("Snappy Decompressor is not supported");
    }
  }
}
