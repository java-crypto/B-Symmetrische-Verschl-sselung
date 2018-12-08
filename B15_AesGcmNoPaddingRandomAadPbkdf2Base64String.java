package net.bplaced.javacrypto.symmetricencryption;

/*
* Herkunft/Origin: http://javacrypto.bplaced.net/
* Programmierer/Programmer: Michael Fehr
* Copyright/Copyright: frei verwendbares Programm (Public Domain)
* Copyright: This is free and unencumbered software released into the public domain.
* Lizenttext/Licence: <http://unlicense.org>
* getestet mit/tested with: Java Runtime Environment 8 Update 191 x64
* Datum/Date (dd.mm.jjjj): 08.12.2018 
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
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class B15_AesGcmNoPaddingRandomAadPbkdf2Base64String {

	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException, InvalidKeySpecException {
		System.out.println(
				"B15 AES im Betriebsmodus GCM Kein Padding mit Zufalls-GCM Nonce, AAD, PBKDF2 und Base64-Kodierung mit einem String");
		// es werden ein paar variablen ben�tigt:
		String plaintextString = "Dieses ist der super geheime Text";
		byte[] plaintextByte = plaintextString.getBytes("UTF-8");

		// der gcm modus bietet an, erg�nzende daten ohne verschl�sselung mit zu
		// speichern
		// diese daten werden ebenfalls mit dem hashwert gesichert
		String aadtextString = "Hier stehen die AAD-Daten";
		byte[] aadtextByte = aadtextString.getBytes("utf-8");

		// das passwort wird z.b. von einem jPassword-Feld �bergeben
		char[] passwordChar = "12345678901234567890123456789012".toCharArray();

		// erzeugung des password-keys mittels PBKDF2
		// variablen f�r pbkdf2
		final int PBKDF2_ITERATIONS = 10000; // anzahl der iterationen, h�her = besser = langsamer
		final int SALT_SIZE_BYTE = 256; // gr�sse des salts, sollte so gro� wie der hash sein
		final int HASH_SIZE_BYTE = 256; // gr��e das hashes bzw. gehashten passwortes, 256 byte
		byte[] passwordHashByte = new byte[HASH_SIZE_BYTE]; // das array nimmt das gehashte passwort auf
		// wir erzeugen einen zufalls salt mit securerandom, nicht mit random
		SecureRandom secureRandom = new SecureRandom();
		byte passwordSaltByte[] = new byte[SALT_SIZE_BYTE];
		secureRandom.nextBytes(passwordSaltByte);

		// erstellung des gehashten passwortes
		PBEKeySpec spec = new PBEKeySpec(passwordChar, passwordSaltByte, PBKDF2_ITERATIONS, HASH_SIZE_BYTE);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		passwordHashByte = skf.generateSecret(spec).getEncoded();

		// ab hier "normale" gcm-routinen
		final int GCMNONCELENGTH = 12; // = 96 bit

		String decryptedtextString = ""; // enth�lt sp�ter den entschl�sselten text

		// diese konstanten und variablen ben�tigen wir zur ver- und entschl�sselung

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
		ciphertextByte = AesGcmNoPaddingAadEncrypt(plaintextByte, aadtextByte, passwordHashByte, gcmNonceByte);
		// aus sicherheitsgr�nden l�schen wir die wichtigen byte arrays
		Arrays.fill(plaintextByte, (byte) 0); // �berschreibt das byte array mit nullen
		Arrays.fill(passwordHashByte, (byte) 0); // �berschreibt das byte array mit nullen
		Arrays.fill(passwordChar, (char) 0); // �berschreibt das char array mit nullen
		// byte array aus PBKDF2_ITERATIONS, passwordSaltByte, gcmNonceByte und
		// ciphertextByte erzeugen
		// integer wert pbkdf2_iterations in byte array umwandeln
		// pbkdf2_iterationsByte ist 4 byte lang
		byte[] pbkdf2_iterationsByte = ByteBuffer.allocate(4).putInt(PBKDF2_ITERATIONS).array();
		// gcmCompleteCiphertextByte enth�lt sp�ter den kompletten datensatz
		byte[] gcmCompleteCiphertextByte = new byte[(pbkdf2_iterationsByte.length + SALT_SIZE_BYTE + GCMNONCELENGTH
				+ ciphertextByte.length)];
		System.arraycopy(pbkdf2_iterationsByte, 0, gcmCompleteCiphertextByte, 0, 4);
		System.arraycopy(passwordSaltByte, 0, gcmCompleteCiphertextByte, 4, SALT_SIZE_BYTE);
		System.arraycopy(gcmNonceByte, 0, gcmCompleteCiphertextByte, (4 + SALT_SIZE_BYTE), GCMNONCELENGTH);
		System.arraycopy(ciphertextByte, 0, gcmCompleteCiphertextByte, (4 + SALT_SIZE_BYTE + GCMNONCELENGTH),
				ciphertextByte.length);

		// byte array in einen base64-string umwandeln
		String gcmCompleteCiphertextString = Base64.getEncoder().encodeToString(gcmCompleteCiphertextByte);
		// aad-daten ebenfalls in einen base64-string umwandeln
		String aadtextB64String = Base64.getEncoder().encodeToString(aadtextByte);

		// ausgabe der daten
		System.out.println("Klartextdaten verschl�sseln und als Base64-String anzeigen");
		System.out.println("plaintextString                 :" + plaintextString);
		System.out.println("plaintextByte (hex)             :" + " * * * Das Byte Array wurde gel�scht * * *");
		System.out.println("passwordChar (hex)              :" + " * * * Das Char Array wurde gel�scht * * *");
		System.out.println("gcmNonceByte (hex)              :" + DatatypeConverter.printHexBinary(gcmNonceByte));
		System.out.println("passwordSaltByte (hex)          :" + DatatypeConverter.printHexBinary(passwordSaltByte));
		System.out.println("passwordHashByte (hex)          :" + " * * * Das Byte Array wurde gel�scht * * *");
		System.out.println("aadtextString                   :" + aadtextString);
		System.out.println("aadtextByte (hex)               :" + DatatypeConverter.printHexBinary(aadtextByte));
		System.out.println("aadtextString (B64)             :" + aadtextB64String);
		System.out.println("= = = Verschl�sselung = = =");
		System.out.println("ciphertextByte (hex)            :" + DatatypeConverter.printHexBinary(ciphertextByte));
		System.out.println("= = = gcmNonceByte + ciphertextByte = = =");
		System.out.println(
				"gcmCompleteCiphertextByte (hex) :" + DatatypeConverter.printHexBinary(gcmCompleteCiphertextByte));
		System.out.println("gcmCompleteCiphertextString(B64):" + gcmCompleteCiphertextString);

		// ab hier arbeiten wir nun im entschl�sselungsmodus

		// simulation der eingabe des passwortes
		// das passwort wird z.b. von einem jPassword-Feld �bergeben
		char[] passwordCharDecrypt = "12345678901234567890123456789012".toCharArray();

		// simulation des empfangs der nachricht
		// zuerst die aad-daten dekodieren
		String receivedAadtextB64String = aadtextB64String;
		byte[] aadtextByteReceived = Base64.getDecoder().decode(receivedAadtextB64String);
		String aadtextStringReceived = new String(aadtextByteReceived, "UTF-8");

		// ganz wichtig: wir ben�tigen zur entschl�sselung auch die aad-daten
		// simulation von falsch erhaltenen aad-daten - einfach die zeile ohne
		// kommentarvermerk ausf�hren
		// aadtextByteReceived = "Hier stehen die AAD-Daten1".getBytes("utf-8");

		byte[] passwordHashByteDecrypt = null;
		// jetzt die verschl�sselten daten erhalten
		String receivedMessageString = gcmCompleteCiphertextString;
		// umwandlung des base64-strings in ein byte array
		byte[] gcmCompleteCiphertextByteReceived = Base64.getDecoder().decode(receivedMessageString);

		// aufteilung PBKDF2_ITERATIONS, passwordSaltByte, gcmNonceByte und
		// ciphertextByte
		int PBKDF2_ITERATIONS_RECEIVED = 0;
		byte[] pbkdf2_iterationsByteReceived = Arrays.copyOfRange(gcmCompleteCiphertextByteReceived, 0, 4);
		PBKDF2_ITERATIONS_RECEIVED = ByteBuffer.wrap(pbkdf2_iterationsByteReceived).getInt();
		byte[] passwordSaltByteReceived = Arrays.copyOfRange(gcmCompleteCiphertextByteReceived, 4, 4 + SALT_SIZE_BYTE);
		byte[] gcmNonceByteReceived = Arrays.copyOfRange(gcmCompleteCiphertextByteReceived, (4 + SALT_SIZE_BYTE),
				(4 + SALT_SIZE_BYTE) + GCMNONCELENGTH);
		byte[] ciphertextByteReceived = Arrays.copyOfRange(gcmCompleteCiphertextByteReceived,
				(4 + SALT_SIZE_BYTE + GCMNONCELENGTH), gcmCompleteCiphertextByteReceived.length);

		// aus dem eingegebenen passwort und den �bergebenen iterationen und de salt
		// wird der passwordHashByteDecrypt errechnet
		PBEKeySpec specDec = new PBEKeySpec(passwordCharDecrypt, passwordSaltByteReceived, PBKDF2_ITERATIONS_RECEIVED,
				HASH_SIZE_BYTE);
		SecretKeyFactory skfDecrypt = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		passwordHashByteDecrypt = skfDecrypt.generateSecret(specDec).getEncoded();

		// nun wird der ciphertext wieder entschl�sselt
		decryptedtextByte = AesGcmNoPaddingAadDecrypt(ciphertextByteReceived, aadtextByteReceived,
				passwordHashByteDecrypt, gcmNonceByteReceived);
		// aus sicherheitsgr�nden l�schen wir die wichtigen byte arrays
		Arrays.fill(passwordHashByteDecrypt, (byte) 0); // �berschreibt das byte array mit nullen
		Arrays.fill(passwordChar, (char) 0); // �berschreibt das char array mit nullen
		// zur�ck-kodierung des byte array in text
		decryptedtextString = new String(decryptedtextByte, "UTF-8");

		// ausgabe der daten
		System.out.println("");
		System.out.println("= = = Erhaltene Daten = = = ");
		System.out.println("gcmCompleteCiphertextByteReceived:"
				+ DatatypeConverter.printHexBinary(gcmCompleteCiphertextByteReceived));
		System.out
				.println("aadtextByte (hex)                :" + DatatypeConverter.printHexBinary(aadtextByteReceived));
		System.out.println("aadtextStringReceived            :" + aadtextStringReceived);
		System.out.println("receivedMessageString Base64     :" + receivedMessageString);
		System.out.println("gcmCompleteCiphertextByteR (hex) :"
				+ DatatypeConverter.printHexBinary(gcmCompleteCiphertextByteReceived));
		System.out.println(
				"passwordSaltByte Received        :" + DatatypeConverter.printHexBinary(passwordSaltByteReceived));
		System.out.println("passwordHashByteDecrypt          :" + " * * * Das Byte Array wurde gel�scht * * *");
		System.out
				.println("gcmNonceByteReceived (hex)       :" + DatatypeConverter.printHexBinary(gcmNonceByteReceived));
		System.out.println(
				"ciphertextByteReceived (hex)     :" + DatatypeConverter.printHexBinary(ciphertextByteReceived));
		System.out.println("= = = Entschl�sselung = = =");
		System.out.println("decryptedtextByte (hex)          :" + DatatypeConverter.printHexBinary(decryptedtextByte));
		System.out.println("decryptedtextString              :" + decryptedtextString);
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
