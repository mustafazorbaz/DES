package com.mustafazorbaz.des;

public class Converter {
	/**
	 *  Asci kodu hex koduna çevirir.
	 * @param asciiValue
	 * @return
	 */
	 public static String hexConverterThenAscii(String asciiValue)
   {
      char[] chars = asciiValue.toCharArray();
      StringBuffer hex = new StringBuffer();
      for (int i = 0; i < chars.length; i++)
      {
         hex.append(Integer.toHexString((int) chars[i]));
      }
      return hex.toString();
   }
	 /**
	  * Hex den Ascii koda çevirir.
	  * @param hexValue
	  * @return
	  */
   public static String asciiConverterThenHex(String hexValue)
   {
      StringBuilder output = new StringBuilder("");
      for (int i = 0; i < hexValue.length(); i += 2)
      {
         String str = hexValue.substring(i, i + 2);
         output.append((char) Integer.parseInt(str, 16));
      }
      return output.toString();
   }
   public static int[] binaryConverterThenHex(int [] inputBits,String hexEquivalent)
 	{
 		//Girdiyi 2 lik sisteme dönüştürmek için kullanır.Fakat 4 bitlik degerler halinde alır.
 				for(int i=0 ; i < 16 ; i++) {
 					
 					String s = Integer.toBinaryString(Integer.parseInt(hexEquivalent.charAt(i) + "", 16));
 					 
 					while(s.length() < 4) {//4 bitlik değer olarak tamamlar
 						s = "0" + s;
 					}
 					
 					for(int j=0 ; j < 4 ; j++) 
 						inputBits[(4*i)+j] = Integer.parseInt(s.charAt(j) + "");	
 					
 				    Main.console+=s;
 				}
 				return inputBits;
 	}
}
