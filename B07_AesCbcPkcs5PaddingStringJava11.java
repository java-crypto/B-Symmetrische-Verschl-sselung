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
* Funktion: verschlüsselt einen string im aes cbc modus pkcs5 padding
* Function: encrypts a string using aes cbc modus with pkcs5 padding
*
* Sicherheitshinweis/Security notice
* Die Programmroutinen dienen nur der Darstellung und haben keinen Anspruch auf eine 
* korrekte Funktion, insbesondere mit Blick auf die Sicherheit ! 
* Prüfen Sie die Sicherheit bevor das Programm in der echten Welt eingesetzt wird.
* The program routines just show the function but please be aware of the security part - 
* check yourself before using in the real world !
*/

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class B07_AesCbcPkcs5PaddingStringJava11 {

	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		System.out.println("B07 AES im Betriebsmodus CBC PKCS5Padding mit einem String");
		// es werden ein paar variablen benötigt:
		String plaintextString = "HalloWelt012345"; // hier 15 zeichen
		String decryptedtextString = ""; // enthält später den entschlüsselten text
		
		// diese konstanten und variablen benötigen wir zur ver- und entschlüsselung
		// der schlüssel ist exakt 32 zeichen lang und bestimmt die stärke der verschlüsselung
		// hier ist der schlüssel 32 byte = 256 bit lang
		// mögliche schlüssellängen sind 16 byte (128 bit), 24 byte (192 bit) und 32 byte (256 bit)
		// final byte[] keyByte = "1234567890123456".getBytes("UTF-8"); // 16 byte
		final byte[] keyByte = "12345678901234567890123456789012".getBytes("UTF-8"); // 32 byte
		// der initialisierungsvektor ist exakt 16 zeichen lang
		final byte[] initvectorByte = "abcdefghijklmnop".getBytes("UTF-8");
		
		byte[] plaintextByte = null; 
		// der verschluesselte (encrypted) text kommt in diese variable in form eines byte arrays
		byte[] ciphertextByte = null; // die länge steht noch nicht fest, da sie von der größe des plaintextes abhängt
		// der entschlüsselte (decrypted) text kommt in dieses byte array, welches später in einen string umkodiert wird
		byte[] decryptedtextByte = null; // die länge steht noch nicht fest, da sie von der größe des plaintextes abhängt
		
		// ab hier arbeiten wir nun im verschlüsselungsmodus
		// umwandlung des klartextes in ein byte array
		plaintextByte = plaintextString.getBytes("UTF-8");
		// der schlüssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// der initvector wird in die richtige form gebracht
		IvParameterSpec ivKeySpec = new IvParameterSpec(initvectorByte);
		// die verschlüsselungsroutine wird mit dem gewünschten parameter erstellt
		Cipher aesCipherEnc = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		// nun wird die routine mit dem schlüssel initialisiert
		aesCipherEnc.init(Cipher.ENCRYPT_MODE, keySpec, ivKeySpec);
		// hier erfolgt nun die verschlüsselung des plaintextes
		ciphertextByte = aesCipherEnc.doFinal(plaintextByte);
		
		// ab hier arbeiten wir nun im entschlüsselungsmodus
		// die verschlüsselungsroutine wird mit dem gewünschten parameter erstellt
		Cipher aesCipherDec = Cipher.getInstance("AES/CBC/PKCS5PADDING"); 
		// zum einsatz kommt derselbe schlüssel, daher symmetrische verschlüsselung
		// achtung: hier wird der DECRYPT_MODE = entschlüsselung genutzt
		aesCipherDec.init(Cipher.DECRYPT_MODE, keySpec, ivKeySpec);
		// nun wird der ciphertext wieder entschlüsselt
		decryptedtextByte = aesCipherDec.doFinal(ciphertextByte);
		
		// zurück-kodierung des byte array in text
		decryptedtextString = new String(decryptedtextByte,"UTF-8");
		
		// ausgabe der variablen
		System.out.println("");
		System.out.println("keyByte (hex)          :" + printHexBinary(keyByte));
		System.out.println("initvectorByte (hex)   :" + printHexBinary(initvectorByte));
		System.out.println("plaintextString        :" + plaintextString);
		System.out.println("plaintextByte (hex)    :" + printHexBinary(plaintextByte));
		System.out.println("= = = Verschlüsselung = = =");
		System.out.println("ciphertextByte (hex)   :" + printHexBinary(ciphertextByte));
		System.out.println("= = = Entschlüsselung = = =");
		System.out.println("decryptedtextByte (hex):" + printHexBinary(decryptedtextByte));
		System.out.println("decryptedtextString    :" + decryptedtextString);
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
