package net.bplaced.javacrypto.symmetricencryption;

/*
* Herkunft/Origin: http://javacrypto.bplaced.net/
* Programmierer/Programmer: Michael Fehr
* Copyright/Copyright: frei verwendbares Programm (Public Domain)
* Copyright: This is free and unencumbered software released into the public domain.
* Lizenttext/Licence: <http://unlicense.org>
* getestet mit/tested with: Java Runtime Environment 8 Update 191 x64
* Datum/Date (dd.mm.jjjj): 13.01.2019 
* Funktion: verschl�sselt einen Text im AES GCM Modus kein Padding
*           die Ausgabe erfolgt in eine Datei
*           zus�tzlich werden erg�nzende Daten (aad) genutzt
* Function: encrypts a text string using AES GCM modus with no padding
*           the output is written in a file
*           additionally it uses Additional Associated Data (aad)
*
* Sicherheitshinweis/Security notice
* Die Programmroutinen dienen nur der Darstellung und haben keinen Anspruch auf eine 
* korrekte Funktion, insbesondere mit Blick auf die Sicherheit ! 
* Pr�fen Sie die Sicherheit bevor das Programm in der echten Welt eingesetzt wird.
* The program routines just show the function but please be aware of the security part - 
* check yourself before using in the real world !
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class B16_AesGcmNoPaddingRandomAadPbkdf2File {

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException,
			FileNotFoundException, IOException {
		System.out.println(
				"B16 AES im Betriebsmodus GCM Kein Padding mit Zufalls-GCM Nonce, AAD, PBKDF2 mit einer Datei");

		// dateinamen f�r verschl�sselungsmodus
		String dateinameReadString = "b16_test.txt"; // aus der datei wird das plaintextByte eingelesen
		String dateinameWriteString = "b16_test.enc"; // in diese datei werden alle (verschl�sselten) daten geschrieben
		// dateinamen f�r entschl�sselungsmodus
		String dateinameReadStringReceived = "b16_test.enc"; // aus dieser datei werden alle daten gelesen
		String dateinameWriteStringReceived = "b16_test.dec"; // in diese datei werden alle (entschl�sselten) daten geschrieben

		// der gcm modus bietet an, erg�nzende daten ohne verschl�sselung mit zu
		// speichern
		// diese daten werden ebenfalls mit dem hashwert gesichert
		String aadtextString = "Hier stehen die AAD-Daten, welche im Klartext an den Empfaenger gesendet werden.";
		byte[] aadtextByte = aadtextString.getBytes("utf-8");

		// das passwort wird z.b. von einem jPassword-Feld �bergeben
		char[] passwordChar = "12345678901234567890123456789012".toCharArray();

		// es werden ein paar variablen ben�tigt:
		byte[] plaintextByte = null; // in diese variable werden sp�ter die zu verschl�sselnden daten eingelesen

		// zuerst testen wir ob die einzulesende datei existiert
		if (FileExistsCheck(dateinameReadString) == false) {
			System.out.println("Die Datei " + dateinameReadString + " existiert nicht. Das Programm wird beendet.");
			System.exit(0);
		};
		// einlesen der zu verschl�sselnden daten aus einer datei
		plaintextByte = readBytesFromFileNio(dateinameReadString);

		// erzeugung des password-keys mittels PBKDF2
		// variablen f�r pbkdf2
		final int PBKDF2_ITERATIONS = 10000; // anzahl der iterationen, h�her = besser = langsamer
		final int SALT_SIZE_BYTE = 256; // gr�sse des salts, sollte so gro� wie der hash sein
		final int HASH_SIZE_BYTE = 256; // gr��e das hashes bzw. gehashten passwortes, 256 byte
		byte[] passwordHashByte = new byte[HASH_SIZE_BYTE]; // das array nimmt das gehashte passwort auf
		// erzeugung einen zufalls salt mit securerandom, nicht mit random
		SecureRandom secureRandom = new SecureRandom();
		byte passwordSaltByte[] = new byte[SALT_SIZE_BYTE];
		secureRandom.nextBytes(passwordSaltByte);

		// erstellung des gehashten passwortes
		PBEKeySpec spec = new PBEKeySpec(passwordChar, passwordSaltByte, PBKDF2_ITERATIONS, HASH_SIZE_BYTE);
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		passwordHashByte = skf.generateSecret(spec).getEncoded();
		// ab hier "normale" gcm-routinen
		final int GCMNONCELENGTH = 12; // = 96 bit
		// diese konstanten und variablen ben�tigen wir zur ver- und entschl�sselung
		// GENERATE random nonce (number used once)
		final byte[] gcmNonceByte = new byte[GCMNONCELENGTH];
		SecureRandom secureRandomGcm = new SecureRandom();
		secureRandomGcm.nextBytes(gcmNonceByte);
		
		// der verschluesselte (encrypted) text kommt in diese variable in form eines
		// byte arrays
		byte[] ciphertextByte = null; // die l�nge steht noch nicht fest, da sie von der gr��e des plaintextes abh�ngt

		// verschl�sselungsmodus
		ciphertextByte = AesGcmNoPaddingAadEncrypt(plaintextByte, aadtextByte, passwordHashByte, gcmNonceByte);
		// aus sicherheitsgr�nden l�schen wir die wichtigen byte arrays
		Arrays.fill(plaintextByte, (byte) 0); // �berschreibt das byte array mit nullen
		Arrays.fill(passwordHashByte, (byte) 0); // �berschreibt das byte array mit nullen
		Arrays.fill(passwordChar, (char) 0); // �berschreibt das char array mit nullen

		// speicherung der daten in eine datei in folgendem format:
		// integer gr�sse der aad-daten
		// byte[] aad-daten (unverschl�sselt)
		// integer PBKDF2_ITERATIONS
		// integer HASH_SIZE_BYTE
		// integer gr��e des passwordSaltByte
		// byte[] passwordSaltByte
		// integer gr��e des gcmNonceByte
		// byte[] gcmNonceByte
		// integer gr��e des ciphertextByte
		// byte[] ciphertextByte
		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(dateinameWriteString))) {
			out.writeInt(aadtextByte.length);
			out.write(aadtextByte);
			out.writeInt(PBKDF2_ITERATIONS);
			out.writeInt(HASH_SIZE_BYTE);
			out.writeInt(passwordSaltByte.length);
			out.write(passwordSaltByte);
			out.writeInt(gcmNonceByte.length);
			out.write(gcmNonceByte);
			out.writeInt(ciphertextByte.length);
			out.write(ciphertextByte);
		}

		// ausgabe der daten
		System.out.println();
		System.out.println("Klartextdaten aus Datei einlesen, verschl�sseln und als Datei speichern");
		System.out.println("plaintext Datei eingelesen   :" + dateinameReadString);
		System.out.println("ciphertext Datei erzeugt     :" + dateinameWriteString);
		System.out.println("aadtextString                :" + aadtextString);
		System.out.println("= = = Verschl�sselung = = =");
		System.out.println("ciphertextByte (hex)         :" + DatatypeConverter.printHexBinary(ciphertextByte));

		// entschl�sselungsmodus
		System.out.println();
		System.out.println("= = = Nun startet die Entschl�sselung = = =");
		// hier simulieren wir die eingabe des keybytes
		// final byte[] keyByteDecrypt =
		// "12345678901234567890123456789012".getBytes("UTF-8"); // 32 byte
		// das passwort wird z.b. von einem jPassword-Feld �bergeben
		char[] passwordCharDecrypt = "12345678901234567890123456789012".toCharArray();
		byte[] passwordHashByteDecrypt = null;
		
		// der entschl�sselte (decrypted) text kommt in dieses byte array, welches
		// sp�ter in einen string umkodiert wird
		byte[] decryptedtextByte = null; // die l�nge steht noch nicht fest, da sie von der gr��e des plaintextes
											// abh�ngt
		// zuerst testen wir ob die einzulesende datei existiert
		if (FileExistsCheck(dateinameReadStringReceived) == false) {
			System.out.println("Die Datei " + dateinameReadString + " existiert nicht. Das Programm wird beendet.");
			System.exit(0);
		};

		// variablen
		byte[] aadtextByteReceived = null;
		String aadtextStringReceived = "";
		int PBKDF2_ITERATIONS_RECEIVED = 0;
		int HASH_SIZE_BYTE_RECEIVED = 0;
		byte[] passwordSaltByteReceived = null;
		byte[] gcmNonceByteReceived = null;
		byte[] ciphertextByteReceived = null;
		// byte array einlesen
		// speicherung der daten in eine datei in folgendem format:
		// integer gr�sse der aad-daten
		// byte[] aad-daten (unverschl�sselt)
		// integer PBKDF2_ITERATIONS
		// integer HASH_SIZE_BYTE
		// integer gr��e des passwordSaltByte
		// byte[] passwordSaltByte
		// integer gr��e des gcmNonceByte
		// byte[] gcmNonceByte
		// integer gr��e des ciphertextByte
		// byte[] ciphertextByte
		try (DataInputStream dataIn = new DataInputStream(new FileInputStream(dateinameReadStringReceived))) {
			int aadtextByteReceivedLength = dataIn.readInt();
			aadtextByteReceived = new byte[aadtextByteReceivedLength];
			dataIn.read(aadtextByteReceived, 0, aadtextByteReceived.length);
			aadtextStringReceived = new String(aadtextByteReceived, "UTF-8");
			PBKDF2_ITERATIONS_RECEIVED = dataIn.readInt();
			HASH_SIZE_BYTE_RECEIVED = dataIn.readInt();
			int passwordSaltByteReceivedLength = dataIn.readInt();
			passwordSaltByteReceived = new byte[passwordSaltByteReceivedLength];
			dataIn.read(passwordSaltByteReceived, 0, passwordSaltByteReceivedLength);
			int gcmNonceByteReceivedLength = dataIn.readInt();
			gcmNonceByteReceived = new byte[gcmNonceByteReceivedLength];
			dataIn.read(gcmNonceByteReceived, 0, gcmNonceByteReceivedLength);
			int ciphertextByteReceivedLength = dataIn.readInt();
			ciphertextByteReceived = new byte[ciphertextByteReceivedLength];
			dataIn.read(ciphertextByteReceived, 0, ciphertextByteReceivedLength);
		}

		// ganz wichtig: wir ben�tigen zur entschl�sselung auch die aad-daten
		// zur simulation von falsch erhaltenen aad-daten - einfach die zeile ohne
		// kommentarvermerk ausf�hren
		// aadtextByteReceived = "Hier stehen die AAD-Daten1".getBytes("utf-8");

		// aus dem eingegebenen passwort und den �bergebenen iterationen und dem salt
		// wird der passwordHashByteDecrypt errechnet
		PBEKeySpec specDec = new PBEKeySpec(passwordCharDecrypt, passwordSaltByteReceived, PBKDF2_ITERATIONS_RECEIVED,
				HASH_SIZE_BYTE_RECEIVED);
		SecretKeyFactory skfDecrypt = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		passwordHashByteDecrypt = skfDecrypt.generateSecret(specDec).getEncoded();

		// nun wird der ciphertext wieder entschl�sselt
		decryptedtextByte = AesGcmNoPaddingAadDecrypt(ciphertextByteReceived, aadtextByteReceived, passwordHashByteDecrypt, gcmNonceByteReceived);
		Arrays.fill(passwordHashByteDecrypt, (byte) 0); // �berschreibt das byte array mit nullen
		Arrays.fill(passwordChar, (char) 0); // �berschreibt das char array mit nullen
		
		// speicherung in datei
		writeBytesToFileNio(decryptedtextByte, dateinameWriteStringReceived);
		Arrays.fill(decryptedtextByte, (byte) 0); // �berschreibt das byte array mit nullen
		
		// ausgabe der daten
		System.out.println("Verschl�sselte Daten aus Datei einlesen, entschl�sseln und als Datei speichern");
		System.out.println("ciphertext Datei eingelesen  :" + dateinameReadStringReceived);
		System.out.println("decryptedtext Datei erzeugt  :" + dateinameWriteStringReceived);
		System.out.println("= = = Erhaltene Daten = = = ");
		System.out.println("aadtextStringReceived        :" + aadtextStringReceived);
		System.out.println("ciphertextByteReceived (hex) :" + DatatypeConverter.printHexBinary(ciphertextByteReceived));
		System.out.println("= = = Entschl�sselung durchgef�hrt = = =");
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

	private static boolean FileExistsCheck(String dateinameString) {
		return Files.exists(Paths.get(dateinameString), new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
	}

	// Since JDK 7, NIO
	private static byte[] readBytesFromFileNio(String filenameString) {
		byte[] byteFromFileByte = null;
		try {
			// bFile = Files.readAllBytes(new File(filenameString).toPath());
			byteFromFileByte = Files.readAllBytes(Paths.get(filenameString));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return byteFromFileByte;
	}

	// Since JDK 7, NIO
	private static void writeBytesToFileNio(byte[] byteToFileByte, String filenameString) {
		try {
			Path path = Paths.get(filenameString);
			Files.write(path, byteToFileByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
