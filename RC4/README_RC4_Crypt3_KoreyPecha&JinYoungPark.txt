-------------README-------------
===RC4===
Crypto 3
Name: Korey Ray Pecha,Jin Young Park 

We implemented RC4 code in Java.
This program saves the result of encryption or decryption to the desired file path by entering the key, and message file path.

===Usage===
1. Compile the Java file to create a class file.

2. Enter string as key (between 1 and 256 bits). 

3. Enter 1 if you want to encrypt, or 2 if you want to decrypt.

5. Enter the path of the file you want to encrypt or decrypt.
(example)
pg10.txt

6. Enter the path of the file you want to store the implemented results
(example)
pg11.txt

7. After completing encryption or decryption print "Operation complete!" and the runtime.

===Description===
-KSA
1. Take the key array (an array of all characters in the key converted to bytes).

2. Create an array of 256 elements (numbers 0 to 255). This array is S[]

3. Then calculate "j":
   j= [(j+S(i)+key[i % key.length] % 256

4. Swap S[i] with S[j] for the entire array.

5. Return S to use in PRGA

-PRGA
1. I first make a clone of S[] so I don't have to re-use the KSA function. 

2. Then I initialize 2 iterators (i,j) to 0 and begin calculations for PRGA swapping.

3. i is incremented by 1 on every iteration:
   i = (i+1) % 256

4. j is incremented based on the entry at S[i]:
   j= (j+S[i]) % 256

5. S[i] and S[j] are swapped

6. I then add the 2 numbers that were swapped and mod by 256:
   result = (S[i] + S[j]) % 256

7. I then XOR the result with the i-th element of the message array to get the    encrypted bytes.

8 I place this result in a final byte array for the entire message and then return   that.

The final step is to convert the byte array into a String and write it into the output file.
 

-Encryption & Decryption

The message is xored using the keystream generated by RC4 KSA.
In the code, the encryption method and the decryption method are separated. The reason is that plain text and decrypted text are txt files, so it is a string format, but ciphertext is a byte array format.