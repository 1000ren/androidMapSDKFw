package com.ctfo.mvapi.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * 
 * @author gyx
 * 
 *         功能：为转换成标准c++字节流，特提供int , short ,byte , base64互换工具
 * 
 */
public class CodeUtil
{

  public static byte[] intToByte(int i)
  {
    byte[] abyte0 = new byte[4];
    abyte0[0] = (byte) (0xff & i);
    abyte0[1] = (byte) ((0xff00 & i) >> 8);
    abyte0[2] = (byte) ((0xff0000 & i) >> 16);
    abyte0[3] = (byte) ((0xff000000 & i) >> 24);
    return abyte0;
  }

  public static byte[] shortToByte(short i)
  {
    byte[] abyte0 = new byte[2];
    abyte0[0] = (byte) (0xff & i);
    abyte0[1] = (byte) ((0xff00 & i) >> 8);
    return abyte0;
  }

  public static int byteToInt(byte[] bytes)
  {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    buffer.order(ByteOrder.LITTLE_ENDIAN);

    return buffer.getInt();

  }

  public static byte[] readBytes(InputStream in, long length)
      throws IOException
  {
    ByteArrayOutputStream bo = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int read = 0;
    while (read < length)
    {
      int cur = in.read(buffer, 0, (int) Math.min(1024, length - read));
      if (cur < 0)
      {
        break;
      }
      read += cur;
      bo.write(buffer, 0, cur);
    }
    return bo.toByteArray();
  }

  // 十进制转十六进制数
  public static String dtoX(int d)
  {
    String x = "";
    if (d < 16)
    {
      x = change(d);
    }
    else
    {
      int c;
      int s = 0;
      int n = d;
      while (n >= 16)
      {
        s++;
        n = n / 16;
      }

      String[] m = new String[s];
      int i = 0;
      do
      {
        c = d / 16;
        // 判断是否大于10，如果大于10，则转换为A-F的格式
        m[i++] = change(d % 16);
        d = c;
      } while (c >= 16);

      x = change(d);

      for (int j = m.length - 1; j >= 0; j--)
      {
        x += m[j];
      }

    }

    return x;

  }

  // 判断是否为10-15之间的数，如果是则进行转换

  public static String change(int d)
  {
    String x = "";

    switch (d)
    {
      case 10:
        x = "A";
        break;
      case 11:
        x = "B";
        break;
      case 12:
        x = "C";
        break;
      case 13:
        x = "D";
        break;
      case 14:
        x = "E";
        break;
      case 15:
        x = "F";
        break;
      default:
        x = String.valueOf(d);
        break;
    }

    return x;

  }

  public static int bytesToInt(byte[] pack)
  {
    return bytesToInt(pack, 0);
  }

  public static int bytesToInt(byte[] pack, int index)
  {
    int a = pack[index];
    int b = pack[index + 1];
    int c = pack[index + 2];
    int d = pack[index + 3];

    if (a < 0)
    {
      a += 256;
    }

    if (b < 0)
    {
      b += 256;
    }

    if (c < 0)
    {
      c += 256;
    }

    if (d < 0)
    {
      d += 256;
    }

    int value = a | b << 8 | c << 16 | d << 24;

    return value;

  }

  public static byte[] inputStreamToByte(InputStream is)
      throws IOException
  {
    ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
    int ch;
    while ((ch = is.read()) != -1)
    {
      bytestream.write(ch);
    }
    byte data[] = bytestream.toByteArray();
    bytestream.close();
    return data;
  }
  
  public static String encode(String code) {
    //byte [] result = Base64.encodeBase64(code.getBytes());
    //return new String(result);
	  return null;
  }
  
  public static String encode(float code) {
    
    //byte [] result = Base64.encodeBase64(String.valueOf(code).getBytes());
    //return new String(result);
	  return null;
  }
  
  public static String encode(double code) {
    
    //byte [] result = Base64.encodeBase64(String.valueOf(code).getBytes());
    //return new String(result);
	  return null;
  }

  
  public static String decode(String code)
  {
    /*if(!Base64.isArrayByteBase64(code.getBytes())){
      return code;
    }
    byte [] result = Base64.decodeBase64(code.getBytes());
    return new String(result);*/
	  return null;
  }
  
  
  public static char ascii2Char(int ASCII) {   
      return (char) ASCII;   
  }   

  public static int char2ASCII(char c) {   
      return (int) c;   
  }   

  public static String ascii2String(int[] ASCIIs) {   
      StringBuffer sb = new StringBuffer();   
      for (int i = 0; i < ASCIIs.length; i++) {   
          sb.append((char) ascii2Char(ASCIIs[i]));   
      }   
      return sb.toString();   
  }   

  // ASCII码  转换为字符串
  public static String ascii2String(String ASCIIs) {   
      String[] ASCIIss = ASCIIs.split(",");   
      StringBuffer sb = new StringBuffer();   
      for (int i = 0; i < ASCIIss.length; i++) {   
          sb.append((char) ascii2Char(Integer.parseInt(ASCIIss[i])));   
      }   
      return sb.toString();   
  }   

  // 字符串转换为ASCII码   
  public static int[] string2ASCII(String s) {
      if (s == null || "".equals(s)) {   
          return null;   
      }   

      char[] chars = s.toCharArray();   
      int[] asciiArray = new int[chars.length];   

      for (int i = 0; i < chars.length; i++) {   
          asciiArray[i] = char2ASCII(chars[i]);   
      }   
      return asciiArray;   
  }   

  public static String getIntArrayString(int[] intArray) {   
      return getIntArrayString(intArray, ",");   
  }   

  public static String getIntArrayString(int[] intArray, String delimiter) {   
      StringBuffer sb = new StringBuffer();   
      for (int i = 0; i < intArray.length; i++) {   
          sb.append(intArray[i]).append(delimiter);   
      }   
      return sb.toString();   
  }   

  public static String getASCII(int begin, int end) {   
      StringBuffer sb = new StringBuffer();   
      for (int i = begin; i < end; i++) {   
          sb.append(i).append(":").append((char) i).append("\t");   
          // sb.append((char) i).append("\t");   
          if (i % 10 == 0) {   
              sb.append("\n");   
          }   
      }   
      return sb.toString();   
  }   

  public static String getCHASCII(int begin, int end) {   
      return getASCII(19968, 40869);   
  }   

  public static void showASCII(int begin, int end) {   
      for (int i = begin; i < end; i++) {   
          // System.out.print(i + ":" + (char) i + "\t");   
          System.out.print((char) i + "\t");   
          if (i % 10 == 0) {   
              System.out.println();   
          }   
      }   
  }   

  public static void showCHASCII() {   
      showASCII(19968, 40869);   
  }   

  public static void showIntArray(int[] intArray) {   
      showIntArray(intArray, ",");   
  }   

  public static void showIntArray(int[] intArray, String delimiter) {   
      for (int i = 0; i < intArray.length; i++) {   
          System.out.print(intArray[i] + delimiter);   
      }   
  }   

  public static void createFile(String filePathAndName, String fileContent)   
          throws IOException {   

      String filePath = filePathAndName;   
      filePath = filePath.toString();   
      File myFilePath = new File(filePath);   
      if (!myFilePath.exists()) {   
          myFilePath.createNewFile();   
      }   
      FileWriter resultFile = new FileWriter(myFilePath);   
      PrintWriter myFile = new PrintWriter(resultFile);   
      String strContent = fileContent;   
      myFile.println(strContent);   
      myFile.close();   
      resultFile.close();   
  }   


}
