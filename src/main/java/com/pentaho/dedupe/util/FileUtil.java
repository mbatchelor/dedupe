package com.pentaho.dedupe.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class FileUtil {
  public static String bytesToHex(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte bte : bytes) {
      result.append(Integer.toString((bte & 0xff) + 0x100, 16).substring(1));
    }
    return result.toString();
  }

  public static String getHexDigest(MessageDigest md, File file)
      throws IOException {
    md.reset();
    RandomAccessFile randomAccessFile = null;
    FileChannel inChannel = null;
    try {
      randomAccessFile = new RandomAccessFile(file, "r");
      inChannel = randomAccessFile.getChannel();
      ByteBuffer byteBuffer = ByteBuffer.allocate((int)inChannel.size());
      inChannel.read(byteBuffer);
      byteBuffer.rewind();
      byte[] hashBytes = new byte[byteBuffer.remaining()];
      byteBuffer.get(hashBytes);
      md.update(hashBytes);
    } finally {
      if (inChannel != null) {
        inChannel.close();
      }
      if (randomAccessFile != null) {
        randomAccessFile.close();
      }
    }
    return bytesToHex(md.digest());
  }

  public static void copy(File source, File dest) throws IOException {
    if (dest.exists()) {
      throw new IOException("Refusing to copy over destination file: " + dest);
    } else {
      dest.createNewFile();
    }
    FileChannel sourceChannel = null;
    FileChannel destChannel = null;
    try {
      sourceChannel = new FileInputStream(source).getChannel();
      destChannel = new FileOutputStream(dest).getChannel();
      destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
    } finally {
      if (sourceChannel != null) {
        sourceChannel.close();
      }
      if (destChannel != null) {
        destChannel.close();
      }
    }
  }
}
