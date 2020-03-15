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
* Funktion: verschl�sselt einen string im aes cfb / ofb / pcbc modus pkcs5 padding
* Function: encrypts a string using aes cfb / ofb / pcbc modus with pkcs5 padding
*
* Sicherheitshinweis/Security notice
* Die Programmroutinen dienen nur der Darstellung und haben keinen Anspruch auf eine 
* korrekte Funktion, insbesondere mit Blick auf die Sicherheit ! 
* Pr�fen Sie die Sicherheit bevor das Programm in der echten Welt eingesetzt wird.
* The program routines just show the function but please be aware of the security part - 
* check yourself before using in the real world !
*/

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class B10_AesCfbOfbPcbcPkcs5PaddingRandomStringJava11 {

	public static void main(String[] args)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		System.out.println(
				"B10 AES in den Betriebsmodi CFB, OFB und PCBC PKCS5Padding mit Zufalls-Initvektor mit einem String");
		// es werden ein paar variablen ben�tigt:
		String plaintextString = "HalloWelt012345"; // hier 15 zeichen
		String decryptedtextString = ""; // enth�lt sp�ter den entschl�sselten text

		// diese konstanten und variablen ben�tigen wir zur ver- und entschl�sselung
		// der schl�ssel ist exakt 32 zeichen lang und bestimmt die st�rke der
		// verschl�sselung
		// m�gliche schl�ssell�ngen sind 16 byte (128 bit), 24 byte (192 bit) und 32
		// byte (256 bit)
		// final byte[] keyByte = "1234567890123456".getBytes("UTF-8"); // 16 byte
		final byte[] keyByte = "12345678901234567890123456789012".getBytes("UTF-8"); // 32 byte
		// der initialisierungsvektor ist exakt 16 zeichen lang
		final byte[] initvectorByte = new byte[16];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(initvectorByte);

		byte[] plaintextByte = null;
		// der verschluesselte (encrypted) text kommt in diese variable in form eines
		// byte arrays
		byte[] ciphertextByte = null; // die l�nge steht noch nicht fest, da sie von der gr��e des plaintextes abh�ngt
		// der entschl�sselte (decrypted) text kommt in dieses byte array, welches
		// sp�ter in einen string umkodiert wird
		byte[] decryptedtextByte = null; // die l�nge steht noch nicht fest, da sie von der gr��e des plaintextes
											// abh�ngt

		// modus AES CFB PKCS Padding

		// ab hier arbeiten wir nun im verschl�sselungsmodus
		// umwandlung des klartextes in ein byte array
		plaintextByte = plaintextString.getBytes("UTF-8");
		ciphertextByte = AesCfbPaddingEncrypt(plaintextByte, keyByte, initvectorByte);

		// ab hier arbeiten wir nun im entschl�sselungsmodus
		// nun wird der ciphertext wieder entschl�sselt
		decryptedtextByte = AesCfbPaddingDecrypt(ciphertextByte, keyByte, initvectorByte);

		// zur�ck-kodierung des byte array in text
		decryptedtextString = new String(decryptedtextByte, "UTF-8");

		// ausgabe der variablen
		System.out.println("");
		System.out.println("Betriebsmodus: AES CFB PKCS Padding");
		System.out.println("keyByte (hex)          :" + printHexBinary(keyByte));
		System.out.println("initvectorByte (hex)   :" + printHexBinary(initvectorByte));
		System.out.println("plaintextString        :" + plaintextString);
		System.out.println("plaintextByte (hex)    :" + printHexBinary(plaintextByte));
		System.out.println("= = = Verschl�sselung = = =");
		System.out.println("ciphertextByte (hex)   :" + printHexBinary(ciphertextByte));
		System.out.println("= = = Entschl�sselung = = =");
		System.out.println("decryptedtextByte (hex):" + printHexBinary(decryptedtextByte));
		System.out.println("decryptedtextString    :" + decryptedtextString);

		// modus AES OFB PKCS Padding

		// ab hier arbeiten wir nun im verschl�sselungsmodus
		// umwandlung des klartextes in ein byte array
		plaintextByte = plaintextString.getBytes("UTF-8");
		ciphertextByte = AesOfbPaddingEncrypt(plaintextByte, keyByte, initvectorByte);

		// ab hier arbeiten wir nun im entschl�sselungsmodus
		// nun wird der ciphertext wieder entschl�sselt
		decryptedtextByte = AesOfbPaddingDecrypt(ciphertextByte, keyByte, initvectorByte);

		// zur�ck-kodierung des byte array in text
		decryptedtextString = new String(decryptedtextByte, "UTF-8");

		// ausgabe der variablen
		System.out.println("");
		System.out.println("Betriebsmodus: AES OFB PKCS Padding");
		System.out.println("keyByte (hex)          :" + printHexBinary(keyByte));
		System.out.println("initvectorByte (hex)   :" + printHexBinary(initvectorByte));
		System.out.println("plaintextString        :" + plaintextString);
		System.out.println("plaintextByte (hex)    :" + printHexBinary(plaintextByte));
		System.out.println("= = = Verschl�sselung = = =");
		System.out.println("ciphertextByte (hex)   :" + printHexBinary(ciphertextByte));
		System.out.println("= = = Entschl�sselung = = =");
		System.out.println("decryptedtextByte (hex):" + printHexBinary(decryptedtextByte));
		System.out.println("decryptedtextString    :" + decryptedtextString);

		// modus AES PCBC PKCS Padding

		// ab hier arbeiten wir nun im verschl�sselungsmodus
		// umwandlung des klartextes in ein byte array
		plaintextByte = plaintextString.getBytes("UTF-8");
		ciphertextByte = AesPcbcPaddingEncrypt(plaintextByte, keyByte, initvectorByte);

		// ab hier arbeiten wir nun im entschl�sselungsmodus
		// nun wird der ciphertext wieder entschl�sselt
		decryptedtextByte = AesPcbcPaddingDecrypt(ciphertextByte, keyByte, initvectorByte);

		// zur�ck-kodierung des byte array in text
		decryptedtextString = new String(decryptedtextByte, "UTF-8");

		// ausgabe der variablen
		System.out.println("");
		System.out.println("Betriebsmodus: AES PCBC PKCS Padding");
		System.out.println("keyByte (hex)          :" + printHexBinary(keyByte));
		System.out.println("initvectorByte (hex)   :" + printHexBinary(initvectorByte));
		System.out.println("plaintextString        :" + plaintextString);
		System.out.println("plaintextByte (hex)    :" + printHexBinary(plaintextByte));
		System.out.println("= = = Verschl�sselung = = =");
		System.out.println("ciphertextByte (hex)   :" + printHexBinary(ciphertextByte));
		System.out.println("= = = Entschl�sselung = = =");
		System.out.println("decryptedtextByte (hex):" + printHexBinary(decryptedtextByte));
		System.out.println("decryptedtextString    :" + decryptedtextString);
	}

	public static byte[] AesCfbPaddingEncrypt(byte[] plaintextByte, byte[] keyByte, byte[] initvectorByte)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] ciphertextByte = null;
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// der initvector wird in die richtige form gebracht
		IvParameterSpec ivKeySpec = new IvParameterSpec(initvectorByte);
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherEnc = Cipher.getInstance("AES/CFB/PKCS5PADDING");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherEnc.init(Cipher.ENCRYPT_MODE, keySpec, ivKeySpec);
		// hier erfolgt nun die verschl�sselung des plaintextes
		ciphertextByte = aesCipherEnc.doFinal(plaintextByte);
		return ciphertextByte;
	}

	public static byte[] AesCfbPaddingDecrypt(byte[] ciphertextByte, byte[] keyByte, byte[] initvectorByte)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] decryptedtextByte = null;
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// der initvector wird in die richtige form gebracht
		IvParameterSpec ivKeySpec = new IvParameterSpec(initvectorByte);
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherDec = Cipher.getInstance("AES/CFB/PKCS5PADDING");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherDec.init(Cipher.DECRYPT_MODE, keySpec, ivKeySpec);
		// hier erfolgt nun die verschl�sselung des plaintextes
		decryptedtextByte = aesCipherDec.doFinal(ciphertextByte);
		return decryptedtextByte;
	}

	public static byte[] AesOfbPaddingEncrypt(byte[] plaintextByte, byte[] keyByte, byte[] initvectorByte)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] ciphertextByte = null;
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// der initvector wird in die richtige form gebracht
		IvParameterSpec ivKeySpec = new IvParameterSpec(initvectorByte);
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherEnc = Cipher.getInstance("AES/OFB/PKCS5PADDING");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherEnc.init(Cipher.ENCRYPT_MODE, keySpec, ivKeySpec);
		// hier erfolgt nun die verschl�sselung des plaintextes
		ciphertextByte = aesCipherEnc.doFinal(plaintextByte);
		return ciphertextByte;
	}

	public static byte[] AesOfbPaddingDecrypt(byte[] ciphertextByte, byte[] keyByte, byte[] initvectorByte)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] decryptedtextByte = null;
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// der initvector wird in die richtige form gebracht
		IvParameterSpec ivKeySpec = new IvParameterSpec(initvectorByte);
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherDec = Cipher.getInstance("AES/OFB/PKCS5PADDING");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherDec.init(Cipher.DECRYPT_MODE, keySpec, ivKeySpec);
		// hier erfolgt nun die verschl�sselung des plaintextes
		decryptedtextByte = aesCipherDec.doFinal(ciphertextByte);
		return decryptedtextByte;
	}

	public static byte[] AesPcbcPaddingEncrypt(byte[] plaintextByte, byte[] keyByte, byte[] initvectorByte)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		byte[] ciphertextByte = null;
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// der initvector wird in die richtige form gebracht
		IvParameterSpec ivKeySpec = new IvParameterSpec(initvectorByte);
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherEnc = Cipher.getInstance("AES/PCBC/PKCS5PADDING");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherEnc.init(Cipher.ENCRYPT_MODE, keySpec, ivKeySpec);
		// hier erfolgt nun die verschl�sselung des plaintextes
		ciphertextByte = aesCipherEnc.doFinal(plaintextByte);
		return ciphertextByte;
	}

	public static byte[] AesPcbcPaddingDecrypt(byte[] ciphertextByte, byte[] keyByte, byte[] initvectorByte)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] decryptedtextByte = null;
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// der initvector wird in die richtige form gebracht
		IvParameterSpec ivKeySpec = new IvParameterSpec(initvectorByte);
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherDec = Cipher.getInstance("AES/PCBC/PKCS5PADDING");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherDec.init(Cipher.DECRYPT_MODE, keySpec, ivKeySpec);
		// hier erfolgt nun die verschl�sselung des plaintextes
		decryptedtextByte = aesCipherDec.doFinal(ciphertextByte);
		return decryptedtextByte;
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