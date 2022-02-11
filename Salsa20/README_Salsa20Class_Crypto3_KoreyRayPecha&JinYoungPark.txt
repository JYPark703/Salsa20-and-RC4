-------------README-------------
===Salsa20===
Crypto 3
Name: Korey Ray Pecha,Jin Young Park 

We implemented salsa20 code in Java.
This program saves the result of encryption or decryption to the desired file path by entering the key, nonce and message file path.

===Usage===
1. Compile the Java file to create a class file.

2. Enter 32 numbers to be used as keys.(the numbers are between 0 to 127.)
example) 
1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32

3. Enter 8 numbers to be used as nonce. ( the numbers are between 0 to 127.)
example)
3 1 4 1 5 9 2 6

4. Enter 1 if you want to encrypt, or 2 if you want to decrypt.

5. Enter the path of the file you want to encrypt or decrypt.
example)
C:\\Book.txt

6. Enter the path of the file you want to store the implemented results
example)
C:\\Result\\result.txt


7. If the implemention is completed successfully, print "Encryption success" or "Decryption success" and the result is saved in the desired file path.

===Description===
-KeyGen
Salsa 20 generates a key stream of 516 bits using key 256 bits and nonce 64 bits.
There is also a counter 64 bits. In the case of counter, the message to be encrypted or decrypted is divided by 516 bits, and increased by 1 per block.

First, set up the block.
block[0], block [5], block [10], block [15] are constant words, block [1] ~ block[4] and block [11] ~ block [14] are key, block [ 6], block [7] is nonce,block [8], block [9] is block-counter. And the salsa20 block is little endian format.

Second, mix this block using addition, leftshift, xor.
1) add the diagonal and above-diagonal words, rotate left by 7 bits, and xor into the below-diagonal words.
2) add the diagonal and below-diagonal words, rotate left by 9 bits, and xor into the belowbelow-diagonal words.
3) Salsa20 continues down each column, rotating left by 13 bits.
4) Salsa20 then modifies the diagonal words, this time rotating left by 18 bits.
5) Salsa20 finally transposes the array and repeat as round.
6) Add the initial block and the modified block.

In the code, the Salsa20 method sets up a block, and the Salsa20_KeyGen method creates a keystream by executing the salsa20 operation.

-Encryption & Decryption
The message is xored using the keystream generated through Salsa 20.
In the code, the encryption method and the decryption method are separated. The reason is that plain text and decrypted text are txt files, so it is a string format, but ciphertext is a byte array format.

