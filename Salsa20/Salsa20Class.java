/*
 * This class encrypts and decrypts using salsa20. 
 * The Encrypt method encrypts plain text after generating a keystream using salsa20. 
 * The Decrypt method decrypts the encrypted ciphertext. 
 * The salsa20 method is setup to perform the salsa20 operation and calls the method that performs the salsa20 operation, Salsa20_KeyGen. 
 * And the Salsa20_KeyGen method performs salsa20 operation to generate the key stream.
 */
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;

public class Salsa20Class {

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

	// Method to convert byte array to hexadecimal string.
	public static String byteArrayToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b & 0xff));
		}
		return sb.toString();
	}

	// Method to convert byte array to int (little endian)
	public static int ByteArrayToInt(byte[] h, int point) {

		return ((int) (h[point]) /* & 0xff */) | ((((int) (h[point + 1])/* &0xff */)) << 8)
				| ((((int) (h[point + 2])/* &0xff */)) << 16) | ((((int) (h[point + 3])/* &0xff */)) << 24);
	}

	// Method to convert long array to byte array
	public static void LongArrayToByteArray(byte[] h, long[] Output_block) {
		for (int i = 0; i < 16; i++) {
			h[(i * 4)] = (byte) Output_block[i];
			Output_block[i] >>>= 8;
			h[(i * 4) + 1] = (byte) Output_block[i];
			Output_block[i] >>>= 8;
			h[(i * 4) + 2] = (byte) Output_block[i];
			Output_block[i] >>>= 8;
			h[(i * 4) + 3] = (byte) Output_block[i];
		}
	}

	// Method to convert int to byte array (little endian).
	public static byte[] InttoByte(int value) {
		byte[] byteArray = new byte[16];
		for (int i = 15; i >= 0; i--) {
			byteArray[i] = (byte) (value >> (i * 8));
		}
		return byteArray;
	}

	// Method that leftshifts 'a' by 'num'.
	public static int Left_shift(int a, int num) {
		return ((int) a << num) | ((int) a >>> (32 - num));
	}

	// Swap Method
	public static void Swap(long[] arr, int a, int b) {
		long temp = arr[a];
		arr[a] = arr[b];
		arr[b] = temp;
	}

	// Method to convert int to long, because there is no unsigned int in Java.
	public static long unsigned32(int n) {
		return n & 0xFFFFFFFFL;
	}

	/*
	 * This is Salsa 20's operation. The tool is addition, left shift, and xor.
	 */
	public static int Salsa20_KeyGen(long[] Block, long[] Output_block, int round) {

		for (int i = 0; i < round; i++) {

			// First step: add the diagonal and above-diagonal words, rotate left by 7 bits,
			// and xor into the below-diagonal words.
			Block[4] ^= unsigned32(Left_shift((int) (Block[0] + Block[12]), 7));
			Block[9] ^= unsigned32(Left_shift((int) (Block[5] + Block[1]), 7));
			Block[14] ^= unsigned32(Left_shift((int) (Block[10] + Block[6]), 7));
			Block[3] ^= unsigned32(Left_shift((int) (Block[15] + Block[11]), 7));

			// Second step:add the diagonal and below-diagonal words, rotate left by 9 bits,
			// and xor into the belowbelow-diagonal words.
			Block[8] ^= unsigned32(Left_shift((int) (Block[4] + Block[0]), 9));
			Block[13] ^= unsigned32(Left_shift((int) (Block[9] + Block[5]), 9));
			Block[2] ^= unsigned32(Left_shift((int) (Block[14] + Block[10]), 9));
			Block[7] ^= unsigned32(Left_shift((int) (Block[3] + Block[15]), 9));

			// Third step:Salsa20 continues down each column, rotating left by 13 bits
			Block[12] ^= unsigned32(Left_shift((int) (Block[8] + Block[4]), 13));
			Block[1] ^= unsigned32(Left_shift((int) (Block[13] + Block[9]), 13));
			Block[6] ^= unsigned32(Left_shift((int) (Block[2] + Block[14]), 13));
			Block[11] ^= unsigned32(Left_shift((int) (Block[7] + Block[3]), 13));

			// Forth step:Salsa20 then modifies the diagonal words, this time rotating left
			// by 18 bits
			Block[0] ^= unsigned32(Left_shift((int) (Block[12] + Block[8]), 18));
			Block[5] ^= unsigned32(Left_shift((int) (Block[1] + Block[13]), 18));
			Block[10] ^= unsigned32(Left_shift((int) (Block[6] + Block[2]), 18));
			Block[15] ^= unsigned32(Left_shift((int) (Block[11] + Block[7]), 18));

			// Fifth step:Salsa20 finally transposes the array and repeat as round
			Swap(Block, 1, 4);
			Swap(Block, 2, 8);
			Swap(Block, 3, 12);
			Swap(Block, 6, 9);
			Swap(Block, 7, 13);
			Swap(Block, 11, 14);

		}

		// Final step: Add the initial block and the modified block.
		for (int i = 0; i < 16; i++) {
			Output_block[i] += Block[i];
		}

		return 0;
	}

	/*
	 * Set up the block of salsa20 before executing the operation of salsa20. block
	 * [0], block [5], block [10], block [15] are constant words, block [1] ~ block
	 * [4] and block [11] ~ block [14] are key, block [ 6], block [7] is nonce,
	 * block [8], block [9] is block-counter. And the salsa20 block is little endian
	 * format.
	 */
	public static byte[] Salsa20(byte[] h_Key, byte[] h_Nonce, int counter) {

		byte h_block[] = new byte[16];
		h_block = InttoByte(counter);
		byte Output[] = new byte[64];

		long Block[] = new long[16];
		long Output_block[] = new long[16];
		Output_block[0] = Block[0] = 0x61707865;
		Output_block[5] = Block[5] = 0x3320646e;
		Output_block[10] = Block[10] = 0x79622d32;
		Output_block[15] = Block[15] = 0x6b206574;
		for (int i = 1, j = 0; i < 5 && j < 16; i++, j += 4) {
			Output_block[i] = Block[i] = unsigned32(ByteArrayToInt(h_Key, j));
		}
		for (int i = 6, j = 0; i < 8 && j < 8; i++, j += 4) {
			Output_block[i] = Block[i] = unsigned32(ByteArrayToInt(h_Nonce, j));
		}
		for (int i = 8, j = 0; i < 10 && j < 8; i++, j++) {
			Output_block[i] = Block[i] = unsigned32(h_block[j]);
		}
		for (int i = 11, j = 16; i < 15 && j < 32; i++, j += 4) {
			Output_block[i] = Block[i] = unsigned32(ByteArrayToInt(h_Key, j));
		}

		Salsa20_KeyGen(Block, Output_block, 20);

		LongArrayToByteArray(Output, Output_block);
		for (int i = 28; i < 32; i++) {

		}
		return Output;
	}

	/*
	 * Encrypt the plaintext using salsa20 and one-time pad So, Using key, nonce(IV)
	 * and counter, create key stream with salsa20 and xor the generated key stream
	 * with plaintext. Since the block output of salsa20 is 512 bits, the plaintext
	 * is divided by 512 bits and the count per block is increased by one.
	 */
	public static byte[] Encrypt(String plain_text, byte[] key, byte[] nonce) {

		byte Plain[] = plain_text.getBytes();

		byte cipher[] = new byte[Plain.length];

		byte key_stream[] = new byte[64];

		for (int counter = 0; counter <= (Plain.length / 64); counter++) {
			key_stream = Salsa20(key, nonce, counter);

			for (int i = 0; i < 64 && i + (counter * 64) < Plain.length; i++) {
				cipher[i + (counter * 64)] = (byte) (Plain[i + (counter * 64)] ^ key_stream[i]);

			}
		}

		return cipher;
	}

	/* Decrypt the ciphertext */
	public static String Decrypt(byte[] cipher, byte[] key, byte[] nonce) {

		byte plain[] = new byte[cipher.length];

		byte key_stream[] = new byte[64];

		for (int counter = 0; counter <= (cipher.length / 64); counter++) {
			key_stream = Salsa20(key, nonce, counter);
			for (int i = 0; i < 64 && i + (counter * 64) < cipher.length; i++) {
				plain[i + (counter * 64)] = (byte) (cipher[i + (counter * 64)] ^ key_stream[i]);
			}
		}
		String Plain_text = new String(plain);
		return Plain_text;
	}

	public static void main(String Args[]) {

		Scanner scanner = new Scanner(System.in);
	
		System.out.print("Enter key: ");
		byte[] key_byte = new byte[32];
		for (int i = 0; i < 32; i++) {
			byte key;
			key = scanner.nextByte();
			key_byte[i] = key;
		}
		
		scanner = new Scanner(System.in);
		System.out.print("Enter nonce: ");
		byte[] nonce_byte = new byte[8];

		for (int i = 0; i < 8; i++) {
			byte nonce;
			nonce = scanner.nextByte();
			nonce_byte[i] = nonce;
		}

		scanner = new Scanner(System.in);
		System.out.print("Enter 1 for encryption or 2 for decryption: ");
		int Num = scanner.nextInt();
		if (Num == 1) {
			scanner = new Scanner(System.in);
			System.out.println("File location to encrypt: ");
			String filepath = scanner.nextLine();
			scanner = new Scanner(System.in);
			System.out.print("File location to store result: ");
			String storecryptofilepath = scanner.nextLine();

			String plaintext = null;
			String text;
			try {
				File file = new File(filepath);

				Scanner scan = new Scanner(file);
				plaintext = scan.nextLine();
				while (scan.hasNextLine()) {
					text = scan.nextLine();
					plaintext = plaintext + "\r\n" + text;

				}
				byte ciphertext[] = Encrypt(plaintext, key_byte, nonce_byte);
				System.out.println("ciphertext: " + byteArrayToHexString(ciphertext));

				FileOutputStream fileOut = new FileOutputStream(storecryptofilepath);
				fileOut.write(ciphertext);
				System.out.println("Encryption success");

			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}

		} else if (Num == 2) {
			scanner = new Scanner(System.in);
			System.out.println("File location to decrypt: ");
			String filepath = scanner.nextLine();
			scanner = new Scanner(System.in);
			System.out.print("File location to store result: ");
			String storefilepath = scanner.nextLine();

			try {

				File file = new File(filepath);

				byte[] ciphertext = readContentIntoByteArray(file);

				String result = Decrypt(ciphertext, key_byte, nonce_byte);

				File files = new File(storefilepath);
				FileWriter fileWriter = new FileWriter(files, true);
				fileWriter.write(result);
				fileWriter.flush();
				fileWriter.close();
				System.out.println("Decryption success");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}

		}
	}

}
