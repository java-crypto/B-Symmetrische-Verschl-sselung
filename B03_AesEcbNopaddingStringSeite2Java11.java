package net.bplaced.javacrypto.symmetricencryption;

/*
* Herkunft/Origin: http://javacrypto.bplaced.net/
* Programmierer/Programmer: Michael Fehr
* Copyright/Copyright: frei verwendbares Programm (Public Domain)
* Copyright: This is free and unencumbered software released into the public domain.
* Lizenttext/Licence: <http://unlicense.org>
* getestet mit/tested with: Java Runtime Environment 8 Update 191 x64
* getestet mit/tested with: Java Runtime Environment 11.0.1 x64
* Datum/Date (dd.mm.jjjj): 13.01.2019
* Funktion: verschl�sselt einen string im aes ecb modus ohne padding
* Function: encrypts a string using aes ecb modus without a padding
*
* Sicherheitshinweis/Security notice
* Die Programmroutinen dienen nur der Darstellung und haben keinen Anspruch auf eine 
* korrekte Funktion, insbesondere mit Blick auf die Sicherheit ! 
* Pr�fen Sie die Sicherheit bevor das Programm in der echten Welt eingesetzt wird.
* The program routines just show the function but please be aware of the security part - 
* check yourself before using in the real world !
*/

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class B03_AesEcbNopaddingStringSeite2Java11 {

	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("B03 AES im Betriebsmodus ECB NoPadding mit einem String Seite 2");
		// es werden ein paar variablen ben�tigt:
		String plaintextString = "K 100 Akt Nr123 V 200 Akt Nr456 "; // exakt 16 zeichen

		String decryptedtextString = ""; // enth�lt sp�ter den entschl�sselten text

		// diese konstanten und variablen ben�tigen wir zur ver- und entschl�sselung
		// der schl�ssel ist exakt 32 zeichen lang und bestimmt die st�rke der
		// verschl�sselung
		// hier ist der schl�ssel 32 byte = 256 bit lang
		// m�gliche schl�ssell�ngen sind 16 byte (128 bit), 24 byte (192 bit) und 32
		// byte (256 bit)
		// final byte[] keyByte = "1234567890123456".getBytes("UTF-8"); // 16 byte
		final byte[] keyByte = "12345678901234567890123456789012".getBytes("UTF-8"); // 32 byte

		byte[] plaintextByte = null;
		// der verschluesselte (encrypted) text kommt in diese variable in form eines
		// byte arrays
		byte[] ciphertextByte = null; // die l�nge steht noch nicht fest, da sie von der gr��e des plaintextes abh�ngt
		// der entschl�sselte (decrypted) text kommt in dieses byte array, welches
		// sp�ter in einen string umkodiert wird
		byte[] decryptedtextByte = null; // die l�nge steht noch nicht fest, da sie von der gr��e des plaintextes
											// abh�ngt

		// ab hier arbeiten wir nun im verschl�sselungsmodus
		// umwandlung des klartextes in ein byte array
		plaintextByte = plaintextString.getBytes("UTF-8");
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherEnc = Cipher.getInstance("AES/ECB/NOPADDING");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherEnc.init(Cipher.ENCRYPT_MODE, keySpec);
		// hier erfolgt nun die verschl�sselung des plaintextes
		ciphertextByte = aesCipherEnc.doFinal(plaintextByte);

		// ab hier arbeiten wir nun im entschl�sselungsmodus
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherDec = Cipher.getInstance("AES/ECB/NOPADDING");
		// zum einsatz kommt derselbe schl�ssel, daher symmetrische verschl�sselung
		// achtung: hier wird der DECRYPT_MODE = entschl�sselung genutzt
		aesCipherDec.init(Cipher.DECRYPT_MODE, keySpec);
		// nun wird der ciphertext wieder entschl�sselt
		decryptedtextByte = aesCipherDec.doFinal(ciphertextByte);

		// zur�ck-kodierung des byte array in text
		decryptedtextString = new String(decryptedtextByte, "UTF-8");

		// ausgabe der variablen
		System.out.println("");
		System.out.println("keyByte (hex)          :" + printHexBinary(keyByte));
		System.out.println("plaintextString        :" + plaintextString);
		System.out.println("plaintextByte (hex)    :" + printHexBinary(plaintextByte));
		System.out.println("= = = Verschl�sselung = = =");
		System.out.println("ciphertextByte (hex)   :" + printHexBinary(ciphertextByte));
		System.out.println("= = = Entschl�sselung = = =");
		System.out.println("decryptedtextByte (hex):" + printHexBinary(decryptedtextByte));
		System.out.println("decryptedtextString    :" + decryptedtextString);

		// nun �ndert der angreifer den zweiten block ab, in dem er den ersten block an
		// stelle des zweiten blocks sendet
		ciphertextByte = hexStringToByteArray("E31EEE268C5102319D3EF4256E0A5EE0E31EEE268C5102319D3EF4256E0A5EE0");

		// hier wird entschl�sselt
		aesCipherDec = Cipher.getInstance("AES/ECB/NOPADDING");
		// zum einsatz kommt derselbe schl�ssel, daher symmetrische verschl�sselung
		// achtung: hier wird der DECRYPT_MODE = entschl�sselung genutzt
		aesCipherDec.init(Cipher.DECRYPT_MODE, keySpec);
		// nun wird der ciphertext wieder entschl�sselt
		decryptedtextByte = aesCipherDec.doFinal(ciphertextByte);

		// zur�ck-kodierung des byte array in text
		decryptedtextString = new String(decryptedtextByte, "UTF-8");

		// ausgabe der variablen
		System.out.println("");
		System.out.println("= = = Der ciphertext wurde vom Angreifer ver�ndert = = =");
		System.out.println("keyByte (hex)          :" + printHexBinary(keyByte));
		System.out.println("plaintextString        :" + plaintextString);
		System.out.println("plaintextByte (hex)    :" + printHexBinary(plaintextByte));
		System.out.println("= = = Verschl�sselung = = =");
		System.out.println("ciphertextByte (hex)   :" + printHexBinary(ciphertextByte));
		System.out.println("= = = Entschl�sselung = = =");
		System.out.println("decryptedtextByte (hex):" + printHexBinary(decryptedtextByte));
		System.out.println("decryptedtextString    :" + decryptedtextString);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
	public static String printHexBinary(byte[] bytes) {
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
