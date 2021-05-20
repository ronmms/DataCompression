
/**
 * Assignment 1
 * Submitted by: 
 * Ron Cohen. 	ID# 208401349
 * Noam Boni. 	ID# 315586131
 */
import java.io.*;

import assign1.HuffmanEncoderDecoder;

public class IORunner {

	static String IN_FILE_PATH = "C:\\Users\\Ron\\Desktop\\Java_assign1_files\\Examples\\Smiley.bmp";

	static String OUT_FILE_PATH = "C:\\Users\\Ron\\Desktop\\Java_assign1_files\\Examples\\output.bmp";

	public static void main(String[] args) {
		FileInputStream input;
		FileOutputStream output;
		try {
			input = new FileInputStream(IN_FILE_PATH);
			output = new FileOutputStream(OUT_FILE_PATH);
			HuffmanEncoderDecoder compress = new HuffmanEncoderDecoder();
			String[] input_names = { "C:\\Users\\Ron\\Desktop\\Java_assign1_files\\Examples\\Smiley.bmp" };
			String[] output_names = { "C:\\Users\\Ron\\Desktop\\Java_assign1_files\\Examples\\output.bmp" };
			String[] deCompressed = { "C:\\Users\\Ron\\Desktop\\Java_assign1_files\\Examples\\decompressed.bmp" };

			compress.Compress(input_names, output_names);
			compress.Decompress(output_names, deCompressed);

			// while (true) // Keep going until forced out.
			// // for (int i = 0; i < 100; i++) // Check only 100 first bytes.
			// {
			// int x = input.read();
			// if (x != -1) // -1 is EOF
			// {
			// // System.out.print(x);
			// // System.out.print((char)x);
			// output.write(x);
			// }
			// else
			// {
			// System.out.println(x);
			// break;
			// }
			// }

			input.close();
			output.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
