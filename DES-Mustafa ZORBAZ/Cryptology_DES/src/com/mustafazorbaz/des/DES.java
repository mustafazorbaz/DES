package com.mustafazorbaz.des;
import static com.mustafazorbaz.des.Main.console;
import java.lang.*;
class DES extends Permutation {
     
    private static int[][] subkey = new int[16][48];
    private static int[] Block1 = new int[28];
    private static int[] Block2 = new int[28];

      
    /**
     * DES giriş olarak aldığı 64 bitlik açık metin bloğuna, önce bir başlangıç
     * permütasyonu (initial permutation) uygular ve ardından blok 32 bitlik iki
     * alt bloğa bölünür. Bu alt bloklar üzerinde f fonksiyonu 16 defa
     * uygulanır. Bu işlemde alt bloklar anahtar ile birleştirilir. İşlem
     * sonunda alt bloklar birleştirilir ve başlangıçta uygulanan permütasyonun
     * tersi uygulanarak algoritma sona erer.
     *
     * @param inputBits
     * @param keyBits
     * @param isDecrypt
     * @return
     */
   public static int[] permutation(int[] inputBits, int[] keyBits, boolean isDecrypt) {

        int newBits[] = new int[inputBits.length];
        int L[] = new int[32];
        int R[] = new int[32];

        for (int i = 0; i < inputBits.length; i++) {
            newBits[i] = inputBits[InitialPermutation[i] - 1];
        }

        int i;

        for (i = 0; i < 28; i++) {
            Block1[i] = keyBits[KeyPermutation[i] - 1];
        }
        for (; i < 56; i++) {
            Block2[i - 28] = keyBits[KeyPermutation[i] - 1];
        }

        System.arraycopy(newBits, 0, L, 0, 32);  //newBits dizisinin 0.indisinde 32 karekterini L dizisine 0. indisiden başlayarak aktarıyor.
        System.arraycopy(newBits, 32, R, 0, 32);
        Main.console+=("\n L0 = ");
        displayBits(L); //bitleri göstermek için kulladndıgımız fonksiyon
        Main.console+=("\n R0 = ");
        displayBits(R);
        for (int n = 0; n < 16; n++) {
            
            Main.console+=("\n+++++++++++++++++++++++++++++++");
            Main.console+=("\nTur " + (n + 1) + ":");

            int newR[] = new int[0];
            if (isDecrypt) {
                newR = expansionPerProcessing(R, subkey[15 - n]);
                Main.console+=("\nTur Anahtar = ");
                displayBits(subkey[15 - n]);
            } else {
                newR = expansionPerProcessing(R, createSubKey(n, keyBits));
                 Main.console+=("\nTur Anahtar = ");
                displayBits(subkey[n]);
            }

            int newL[] = xor(L, newR);//XOR yaptık.
            L = R; //Sol diziye sagdakini
            R = newL; //Sağa da yeni dizimizi aktardık.
             Main.console+=("\nL = ");
            displayBits(L);
             Main.console+=("\nR = ");
            displayBits(R);
        }

        //Sağ ve Sol bit blokları birleşiyor
        int output[] = new int[64];
        System.arraycopy(R, 0, output, 0, 32);
        System.arraycopy(L, 0, output, 32, 32);
        int finalOutput[] = new int[64];

        //Output bit bloguna  Final Permutasyon blogundan elde eldilen yeni değerler finalOutput'a setleniyor... 
        for (i = 0; i < 64; i++) {
            finalOutput[i] = output[FP[i] - 1];
        }
        Main.console+=("\n");
        String hex = new String();
        for (i = 0; i < 16; i++) {
            String bin = new String();
            for (int j = 0; j < 4; j++) {
                bin += finalOutput[(4 * i) + j];
            }
            int decimal = Integer.parseInt(bin, 2);
            hex += Integer.toHexString(decimal);
            Main.console+=(bin);
        }
        
        String asciiValues = Converter.asciiConverterThenHex(hex);

        if (isDecrypt) {
            Main.console+=("\nSonuc : " + asciiValues);
            Main.jTextFieldResult2.setText(asciiValues);
        } else {
             Main.console+=("\nSonuc : " + asciiValues); 
              Main.jTextFieldResult1.setText(asciiValues);
        }

        return finalOutput;

    }

    /**
     * 56 bitlik anahtar önce 28 bitlik iki parçaya ayrılır. Parçalar döngüsel
     * olarak 1 ya da 2 bit kaydırılır. 16 döngü için kaydırılacak bit sayıları
     * belirlenmiştir. Kaydırma işleminin ardından 56 bitten 48’i seçilir.
     *
     * @param round
     * @param key
     * @return
     */
    private static int[] createSubKey(int round, int[] key) {

        int C1[] = new int[28];
        int D1[] = new int[28];

        int rotationTimes = (int) Rotations[round];

        C1 = leftShift(Block1, rotationTimes);
        D1 = leftShift(Block2, rotationTimes);

        //Kaydırdıktan sonra birleştiriliyor.56 bit 48 bit'e düşürülüyor.
        int CnDn[] = new int[56];
        System.arraycopy(C1, 0, CnDn, 0, 28);
        System.arraycopy(D1, 0, CnDn, 28, 28);

        int Kn[] = new int[48];
        for (int i = 0; i < Kn.length; i++) {
            //56 bitten 48’i seçilir.
            Kn[i] = CnDn[CompressionPermutation[i] - 1];//Sıkıstırma Permitasyonundaki değerlerini
        }

        subkey[round] = Kn; //DES’in her döngüsü için 48 bitlik farklı alt anahtarlar (subkey) üretilir. 
        Block1 = C1;
        Block2 = D1;
        return Kn;
    }

    /**
     * Bu işlemle, bloğun sağ yarısı olan R, 32 bitten 48 bite genişletilerek,
     * anahtarla aynı boyuta getirilir. Bu sayede,yer değiştirme işlemi
     * süresince sıkıştırılabilecek daha uzun bir sonuç oluşturulur.Bu bazen
     * E-kutusu (E-box) adını alır.
     *
     * @param R
     * @param roundKey
     * @return
     */
    private static int[] expansionPerProcessing(int[] R, int[] roundKey) {

        int expandedR[] = new int[48];
        for (int i = 0; i < 48; i++) {
            expandedR[i] = R[ExpansionPermutation[i] - 1];
        }

        int temp[] = xor(expandedR, roundKey);//iki diziyi XOR yaptık.Çıkışlar farklı ise 1 aynı ise 0

        int output[] = SBoxProcessing(temp);
        return output;
    }

    /**
     * Sıkıştırılmış anahtarın, genişletilen blokla XOR işlemine sokulmasıyla
     * elde edilen 48 bitlik sonuç, yerine koyma işlemine tabi tutulur. Yerine
     * koyma işlemi 8 adet S-kutusu ile yapılır. Her S-kutusu 6 bitlik girişe, 4
     * bitlik çıkışa sahiptir. Her blok ayrı bir S-kutusu ile işleme sokulur.
     * S-kutuları 4 satırlı ve 16 sütunlu tablolardan oluşmaktadır. Kutudaki her
     * giriş 4 bitlik bir sayıdır. 6 bitlik giriş, çıkış için hangi satır ve
     * sütuna bakılacağını belirler b1b2b3b4b5b6 S-kutusu girişi olsun. b1 ve b6
     * birleştirilerek 0-3 arasında iki bitlik bir sayı oluşturulur. Bu sayı
     * tablodaki satır numarasını belirler. Ortadaki 4 bit ise 4 bitlik bir sayı
     * oluşturarak 0-15 arası sütun numarasını verir.
     *
     * @param bits
     * @return
     */
    private static int[] SBoxProcessing(int[] bits) {

        int output[] = new int[32];//32 bitlik çıkış için yeni dizi

        // 8 tane s-Box için döngümüz...
        for (int i = 0; i < 8; i++) {

            //Satır için b1 ve b6 
            int row[] = new int[2];
            row[0] = bits[6 * i];//0->0 ,1->6,2->12... 
            row[1] = bits[(6 * i) + 5];//0->5,1->11,2->17...
            String sRow = row[0] + "" + row[1];

            //Sütun için b2b3b4b5
            int column[] = new int[4];
            column[0] = bits[(6 * i) + 1]; //0->1,1->7...
            column[1] = bits[(6 * i) + 2]; //0->2,1->8...
            column[2] = bits[(6 * i) + 3];//0->3,1->9...
            column[3] = bits[(6 * i) + 4];//0->4,1->10...
            String sColumn = column[0] + "" + column[1] + "" + column[2] + "" + column[3];

            int iRow = Integer.parseInt(sRow, 2);
            int iColumn = Integer.parseInt(sColumn, 2);
            int x = SBox[i][(iRow * 16) + iColumn];

            String s = Integer.toBinaryString(x);

            while (s.length() < 4) { //4 bit değilse 0 ekleyerek 4 bit'e tamamladık.
                s = "0" + s;
            }

            for (int j = 0; j < 4; j++) {
                output[(i * 4) + j] = Integer.parseInt(s.charAt(j) + ""); //Her kutu için 4 atlayarak cıktı sonucu için 2 lik tabandaki degerleri diziy
            }
        }

        int finalOutput[] = new int[32];
        for (int i = 0; i < 32; i++) {
            finalOutput[i] = output[PBox[i] - 1];
        }
        return finalOutput;
    }

    /**
     * İki diziyi XOR yapmaktadır.
     *
     * @param a dizi
     * @param b dizi
     * @return xor(a^b) dizi
     */
    private static int[] xor(int[] a, int[] b) {

        int answer[] = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            answer[i] = a[i] ^ b[i];
        }
        return answer;
    }

    /**
     * Bu fonksiyonda 0 indis temp te tutulur.Diğer indisler 1 er 1 er üzerine
     * gelir.
     *
     * @param bits
     * @param n
     * @return
     */
    private static int[] leftShift(int[] bits, int n) {

        int answer[] = new int[bits.length];
        System.arraycopy(bits, 0, answer, 0, bits.length);
        for (int i = 0; i < n; i++) {
            int temp = answer[0];
            for (int j = 0; j < bits.length - 1; j++) {
                answer[j] = answer[j + 1];
            }
            answer[bits.length - 1] = temp; //0. indisi en sona ekledik
        }
        return answer;
    }

    private static void displayBits(int[] bits) {

             
        //Her bir 4 bitlik  deger 1 karektere denk geldigi için 4 er 4 er yazdırma yapmaktadır.
        for (int i = 0; i < bits.length; i += 4) {
            String output = new String();
            for (int j = 0; j < 4; j++) {
                output += bits[i + j];
            }
                 Main.console+=(Integer.toBinaryString(Integer.parseInt(output, 2)));
        }
       
    }
}
