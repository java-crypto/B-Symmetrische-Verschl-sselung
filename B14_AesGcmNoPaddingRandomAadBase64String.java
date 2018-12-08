package net.bplaced.javacrypto.symmetricencryption;

/*
* Herkunft/Origin: http://javacrypto.bplaced.net/
* Programmierer/Programmer: Michael Fehr
* Copyright/Copyright: frei verwendbares Programm (Public Domain)
* Copyright: This is free and unencumbered software released into the public domain.
* Lizenttext/Licence: <http://unlicense.org>
* getestet mit/tested with: Java Runtime Environment 8 Update 191 x64
* Datum/Date (dd.mm.jjjj): 19.11.2018 
* Funktion: verschl�sselt einen Text im AESs GCM Modus kein Padding
*           die Ausgabe erfolgt als Base64-kodierter String
*           zus�tzlich werden erg�nzende Daten (aad) genutzt
* Function: encrypts a text string using AES GCM modus with no padding
*           the output is decode as a Base64-string
*           additionally it uses Additional Associated Data (aad)
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
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class B14_AesGcmNoPaddingRandomAadBase64String {

	public static void main(String[] args)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		System.out.println(
				"B14 AES im Betriebsmodus GCM Kein Padding mit Zufalls-GCM Nonce, AAD und Base64-Kodierung mit einem String");
		// es werden ein paar variablen ben�tigt:
		String plaintextString = "Dieses ist der super geheime Text";
		byte[] plaintextByte = plaintextString.getBytes("UTF-8");

		// der gcm modus bietet an, erg�nzende daten ohne verschl�sselung mit zu
		// speichern
		// diese daten werden ebenfalls mit dem hashwert gesichert
		String aadtextString = "Hier stehen die AAD-Daten";
		byte[] aadtextByte = aadtextString.getBytes("utf-8");

		final int GCMNONCELENGTH = 12; // = 96 bit

		String decryptedtextString = ""; // enth�lt sp�ter den entschl�sselten text

		// diese konstanten und variablen ben�tigen wir zur ver- und entschl�sselung
		// der schl�ssel ist exakt 32 zeichen lang und bestimmt die st�rke der
		// verschl�sselung. m�gliche schl�ssell�ngen sind 16 byte (128 bit),
		// 24 byte (192 bit) und 32 byte (256 bit)
		final byte[] keyByte = "12345678901234567890123456789012".getBytes("UTF-8"); // 32 byte

		// GENERATE random nonce (number used once)
		final byte[] gcmNonceByte = new byte[GCMNONCELENGTH];
		SecureRandom secureRandomGcm = new SecureRandom();
		secureRandomGcm.nextBytes(gcmNonceByte);

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
		// hier erfolgt nun die verschl�sselung des plaintextes
		ciphertextByte = AesGcmNoPaddingAadEncrypt(plaintextByte, aadtextByte, keyByte, gcmNonceByte);
		// byte array aus gcmNonceByte und ciphertextByte erzeugen
		byte[] gcmNonceCiphertextByte = new byte[(GCMNONCELENGTH + ciphertextByte.length)];
		System.arraycopy(gcmNonceByte, 0, gcmNonceCiphertextByte, 0, GCMNONCELENGTH);
		System.arraycopy(ciphertextByte, 0, gcmNonceCiphertextByte, GCMNONCELENGTH, ciphertextByte.length);
		// byte array in einen base64-string umwandeln
		String gcmNonceCiphertextString = Base64.getEncoder().encodeToString(gcmNonceCiphertextByte);

		// ausgabe der daten
		System.out.println("");
		System.out.println("Klartextdaten verschl�sseln und als Base64-String anzeigen");
		System.out.println("plaintextString              :" + plaintextString);
		System.out.println("plaintextByte (hex)          :" + DatatypeConverter.printHexBinary(plaintextByte));
		System.out.println("gcmNonceByte (hex)           :" + DatatypeConverter.printHexBinary(gcmNonceByte));
		System.out.println("keyByte (hex)                :" + DatatypeConverter.printHexBinary(keyByte));
		System.out.println("aadtextString                :" + aadtextString);
		System.out.println("aadtextByte (hex)            :" + DatatypeConverter.printHexBinary(aadtextByte));
		System.out.println("= = = Verschl�sselung = = =");
		System.out.println("ciphertextByte (hex)         :" + DatatypeConverter.printHexBinary(ciphertextByte));
		System.out.println("= = = gcmNonceByte + ciphertextByte = = =");
		System.out.println("gcmNonceCiphertextByte (hex) :" + DatatypeConverter.printHexBinary(gcmNonceCiphertextByte));
		System.out.println("gcmNonceCiphertextString(B64):" + gcmNonceCiphertextString);

		// ab hier arbeiten wir nun im entschl�sselungsmodus

		// hier simulieren wir die eingabe des keybytes
		final byte[] keyByteDecrypt = "12345678901234567890123456789012".getBytes("UTF-8"); // 32 byte

		// hier simulieren wir den empfang der nachricht
		String receivedMessageString = gcmNonceCiphertextString;
		// umwandlung des base64-strings in ein byte array
		byte[] gcmNonceCiphertextByteReceived = Base64.getDecoder().decode(receivedMessageString);

		// ganz wichtig: wir ben�tigen zur entschl�sselung auch die aad-daten

		// simulation von falsch erhaltenen aad-daten - einfach die beiden zeilen ohne
		// kommentarvermerk mit ausf�hren
		// aadtextString = "Hier stehen die AAD-Daten1";
		// aadtextByte = aadtextString.getBytes("utf-8");

		// aufteilung gcmNonce + ciphertext
		byte[] gcmNonceByteReceived = Arrays.copyOfRange(gcmNonceCiphertextByteReceived, 0, GCMNONCELENGTH);
		byte[] ciphertextByteReceived = Arrays.copyOfRange(gcmNonceCiphertextByteReceived, GCMNONCELENGTH,
				gcmNonceCiphertextByteReceived.length);

		// nun wird der ciphertext wieder entschl�sselt
		decryptedtextByte = AesGcmNoPaddingAadDecrypt(ciphertextByteReceived, aadtextByte, keyByteDecrypt,
				gcmNonceByteReceived);

		// zur�ck-kodierung des byte array in text
		decryptedtextString = new String(decryptedtextByte, "UTF-8");

		// ausgabe der daten
		System.out.println("");
		System.out.println("= = = Erhaltene Daten = = = ");
		System.out.println("aadtextString                :" + aadtextString);
		System.out.println("aadtextByte (hex)            :" + DatatypeConverter.printHexBinary(aadtextByte));
		System.out.println("receivedMessageString Base64 :" + receivedMessageString);
		System.out.println(
				"gcmNonceCiphertextByteR (hex):" + DatatypeConverter.printHexBinary(gcmNonceCiphertextByteReceived));
		System.out.println("gcmNonceByteReceived (hex)   :" + DatatypeConverter.printHexBinary(gcmNonceByteReceived));
		System.out.println("ciphertextByteReceived (hex) :" + DatatypeConverter.printHexBinary(ciphertextByteReceived));
		System.out.println("= = = geheimer Schl�ssel = = =");
		System.out.println("keyByteDecrypt (hex)         :" + DatatypeConverter.printHexBinary(keyByteDecrypt));
		System.out.println("= = = Entschl�sselung = = =");
		System.out.println("decryptedtextByte (hex)      :" + DatatypeConverter.printHexBinary(decryptedtextByte));
		System.out.println("decryptedtextString          :" + decryptedtextString);
	}

	public static byte[] AesGcmNoPaddingAadEncrypt(byte[] plaintextByte, byte[] aadtextByte, byte[] keyByte,
			byte[] gcmNonceByte) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		final int GCM_TAG_LENGTH = 128;
		byte[] ciphertextByte = null;
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// statt eines initvectors wird ein gcm parameter benoetigt
		GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, gcmNonceByte);
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherEnc = Cipher.getInstance("AES/GCM/NoPadding");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherEnc.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
		// einbindung der aad-daten
		aesCipherEnc.updateAAD(aadtextByte);
		// hier erfolgt nun die verschl�sselung des plaintextes
		ciphertextByte = aesCipherEnc.doFinal(plaintextByte);
		return ciphertextByte;
	}

	public static byte[] AesGcmNoPaddingAadDecrypt(byte[] ciphertextByte, byte[] aadtextByte, byte[] keyByte,
			byte[] gcmNonceByte) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		final int GCM_TAG_LENGTH = 128;
		byte[] decryptedtextByte = null;
		// der schl�ssel wird in die richtige form gebracht
		SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
		// statt eines initvectors wird ein gcm parameter benoetigt
		GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, gcmNonceByte);
		// die verschl�sselungsroutine wird mit dem gew�nschten parameter erstellt
		Cipher aesCipherDec = Cipher.getInstance("AES/GCM/NoPadding");
		// nun wird die routine mit dem schl�ssel initialisiert
		aesCipherDec.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
		// einbindung der aad-daten
		aesCipherDec.updateAAD(aadtextByte);
		// hier erfolgt nun die verschl�sselung des plaintextes
		decryptedtextByte = aesCipherDec.doFinal(ciphertextByte);
		return decryptedtextByte;
	}
}