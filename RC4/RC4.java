/*
RC4 Cipher implementation for text files.

Authors: Korey Pecha, Jin Young Park

 */

import java.io.*;
import java.util.Scanner;

public class RC4 {

	//Keystream output
	private static final byte[] S = new byte[256];

	public static void main(String[] args) throws IOException {

		// The key
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter key: ");
		String key = sc.nextLine();

		// Convert key to character array
		char[] keyChar = new char[key.length()];
		for (int i = 0; i < key.length(); i++) {
			keyChar[i] = key.charAt(i);
		}

		// Convert key character array to byte array
		byte[] keyByte = new byte[key.length()];
		for (int i = 0; i < keyChar.length; i++) {
			keyByte[i] = (byte) keyChar[i];
		}

		//Ask user if they want to encrypt or decrypt
		sc = new Scanner(System.in);
		System.out.print("Enter 1 for encryption or 2 for decryption: ");
		int Num = sc.nextInt();

		// The input file
		sc = new Scanner(System.in);
		System.out.print("Enter file name: ");
		String filename = sc.nextLine();

		// The output file
		sc = new Scanner(System.in);
		System.out.print("Enter output file name: ");
		String outfile = sc.nextLine();

		// Begin timer, begin execution
		long start = System.nanoTime();

		// Generate keystream
		KSA(keyByte);

		// Create file variable for reading
		File file = new File(filename);


		//Based off user input for encryption/decryption
		//Encryption
		if (Num == 1) {
			String plaintext = null;
			String text;
			try {
				Scanner scan = new Scanner(file);
				plaintext = scan.nextLine();
				while (scan.hasNextLine()) {
					text = scan.nextLine();
					plaintext = plaintext + "\r\n" + text;
				}
				byte Plain[]=plaintext.getBytes();
				byte ciphertext[] = PRGA(Plain);
				FileOutputStream fileOut = new FileOutputStream(outfile);
				fileOut.write(ciphertext);

			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		//Decryption
		else if (Num==2) {
			try {
				byte[] ciphertext = readContentIntoByteArray(file);
				String result = new String(PRGA(ciphertext));
				File files = new File(outfile);
				FileWriter fileWriter = new FileWriter(files, true);
				fileWriter.write(result);
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		//Print completion statement
		System.out.println("Operation complete!");

		// End timer
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 100000000;
		System.out.println("Runtime: " + elapsedTime + " seconds");
	}


	//Generates the Keystream
	public static void KSA(byte[] myKey) {

		// Create S array
		for (int i = 0; i < 255; i++) {
			S[i] = (byte) i;
		}

		// Initialize required things
		int keyLen = myKey.length;
		int j = 0;
		byte tmp;

		// Do the i and j swap
		for (int i = 0; i < 255; i++) {
			j = ((j + S[i] + myKey[i % keyLen]) % 256 & 0xFF);
			tmp = S[i];
			S[i] = S[j];
			S[j] = tmp;
		}
	}

	public static byte[] PRGA(byte[] charToByte) {

		// Copy the keystream to retain integrity
		// This will prevent us from having to call KSA again
		// (only needed if encryption and decrytion are done in a single run)
		byte[] S2 = new byte[S.length];
		for (int i = 0; i < S.length; i++) {
			S2[i] = S[i];
		}

		// Create byte array to store encrypted bytes
		byte[] theAnswer = new byte[charToByte.length];

		// Index finders
		int i = 0;
		int j = 0;

		// PRGA output to XOR with message byte
		int op;

		// PRGA operations
		for (int z = 0; z < charToByte.length; z++) {
			i = ((i + 1) % 256);
			j = ((j + (S2[i] & 0xff)) % 256);
			byte tmp = S2[i];
			S2[i] = S2[j];
			S2[j] = tmp;
			op = (S2[((S2[i] + S2[j] & 0xff)) % 256] & 0xff);

			// XOR message with PRGA
			byte result = charToByte[z] ^= op;

			// Store XOR result (Ciphertext) in byte array
			theAnswer[z] = result;
		}
		// Return byte array
		return theAnswer;
	}

	//Converts file into an array of bytes to use in PRGA
	private static byte[] readContentIntoByteArray(File file) {


		FileInputStream fileInputStream = null;
		byte[] bFile = new byte[(int) file.length()];
		try {
			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bFile;
	}
}
