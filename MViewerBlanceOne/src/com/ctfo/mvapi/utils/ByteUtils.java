package com.ctfo.mvapi.utils;

public class ByteUtils 
{
	public static String toStringHex1(String s) 
	{  
		   byte[] baKeyword = new byte[s.length() / 2];  
		   for (int i = 0; i < baKeyword.length; i++) {  
		    try {  
		     baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(  
		       i * 2, i * 2 + 2), 16));  
		    } catch (Exception e) {  
		     e.printStackTrace();  
		    }  
		   }  
		   try {  
		    s = new String(baKeyword, "utf-8");// UTF-16le:Not  
		   } catch (Exception e1) {  
		    e1.printStackTrace();  
		   }  
		   return s;  
		}  
	
	
    public static String  printHexString( byte[] b) {  
       String str = null;	
       for (int i = 0; i < b.length; i++) { 
         String hex = Integer.toHexString(b[i] & 0xFF); 
         if (hex.length() == 1) { 
           hex = '0' + hex; 
         } 
         System.out.print(""+hex.toUpperCase() ); 
         str=hex.toUpperCase();
       } 
       System.out.println("");
       System.out.println("");
       return str;
    }
	
    public static void putShort(byte b[], short s, int index) {  
        b[index + 1] = (byte) (s >> 8);  
        b[index + 0] = (byte) (s >> 0);  
    }  
 
    public static short getShort(byte[] b, int index) {  
        return (short) (((b[index + 1] << 8) | b[index + 0] & 0xff));  
    }  
 
    public static void putInt(byte[] bb, int x, int index) {  
        bb[index + 3] = (byte) (x >> 24);  
        bb[index + 2] = (byte) (x >> 16);  
        bb[index + 1] = (byte) (x >> 8);  
        bb[index + 0] = (byte) (x >> 0);  
    }  
 
    public static int getInt(byte[] bb, int index) {  
        return (int) ((((bb[index + 3] & 0xff) << 24)  
                | ((bb[index + 2] & 0xff) << 16)  
                | ((bb[index + 1] & 0xff) << 8) | ((bb[index + 0] & 0xff) << 0)));  
    }  
 
    public static void putLong(byte[] bb, long x, int index) {  
        bb[index + 7] = (byte) (x >> 56);  
        bb[index + 6] = (byte) (x >> 48);  
        bb[index + 5] = (byte) (x >> 40);  
        bb[index + 4] = (byte) (x >> 32);  
        bb[index + 3] = (byte) (x >> 24);  
        bb[index + 2] = (byte) (x >> 16);  
        bb[index + 1] = (byte) (x >> 8);  
        bb[index + 0] = (byte) (x >> 0);  
    }  
 
    public static long getLong(byte[] bb, int index) {  
        return ((((long) bb[index + 7] & 0xff) << 56)  
                | (((long) bb[index + 6] & 0xff) << 48)  
                | (((long) bb[index + 5] & 0xff) << 40)  
                | (((long) bb[index + 4] & 0xff) << 32)  
                | (((long) bb[index + 3] & 0xff) << 24)  
                | (((long) bb[index + 2] & 0xff) << 16)  
                | (((long) bb[index + 1] & 0xff) << 8) | (((long) bb[index + 0] & 0xff) << 0));  
    }  
 
    public static void putChar(byte[] bb, char ch, int index) {  
        int temp = (int) ch;  
        // byte[] b = new byte[2];  
        for (int i = 0; i < 2; i ++ ) {  
            bb[index + i] = new Integer(temp & 0xff).byteValue(); // �����λ���������λ  
            temp = temp >> 8; // ������8λ  
        }  
    }  
 
    public static char getChar(byte[] b, int index) {  
        int s = 0;  
        if (b[index + 1] > 0)  
            s += b[index + 1];  
        else 
            s += 256 + b[index + 0];  
        s *= 256;  
        if (b[index + 0] > 0)  
            s += b[index + 1];  
        else 
            s += 256 + b[index + 0];  
        char ch = (char) s;  
        return ch;  
    }  
 
    public static void putFloat(byte[] bb, float x, int index) {  
        // byte[] b = new byte[4];  
        int l = Float.floatToIntBits(x);  
        for (int i = 0; i < 4; i++) {  
            bb[index + i] = new Integer(l).byteValue();  
            l = l >> 8;  
        }  
    }  
 
    public static float getFloat(byte[] b, int index) {  
        int l;  
        l = b[index + 0];  
        l &= 0xff;  
        l |= ((long) b[index + 1] << 8);  
        l &= 0xffff;  
        l |= ((long) b[index + 2] << 16);  
        l &= 0xffffff;  
        l |= ((long) b[index + 3] << 24);  
        return Float.intBitsToFloat(l);  
    }  
 
    public static void putDouble(byte[] bb, double x, int index) {  
        // byte[] b = new byte[8];  
        long l = Double.doubleToLongBits(x);  
        for (int i = 0; i < 4; i++) {  
            bb[index + i] = new Long(l).byteValue();  
            l = l >> 8;  
        }  
    }  
 
    public static double getDouble(byte[] b, int index) {  
        long l;  
        l = b[0];  
        l &= 0xff;  
        l |= ((long) b[1] << 8);  
        l &= 0xffff;  
        l |= ((long) b[2] << 16);  
        l &= 0xffffff;  
        l |= ((long) b[3] << 24);  
        l &= 0xffffffffl;  
        l |= ((long) b[4] << 32);  
        l &= 0xffffffffffl;  
        l |= ((long) b[5] << 40);  
        l &= 0xffffffffffffl;  
        l |= ((long) b[6] << 48);  
        l &= 0xffffffffffffffl;  
        l |= ((long) b[7] << 56);  
        return Double.longBitsToDouble(l);  
    }  
} 
