/**

 * Assignment 1
 * Submitted by: 
 * Ron Cohen. 	ID# 208401349
 * Noam Boni. 	ID# 315586131
 */
package assign1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import base.Compressor;

public class HuffmanEncoderDecoder implements Compressor {
	private int[] freq;
	private PriorityQueue<HuffmanNode> q;
	private HashMap<Character, String> dict;
	private HuffmanNode treeRoot;
	private int wordCount;

	public HuffmanEncoderDecoder() {
		this.freq = new int[10000];
		this.q = new PriorityQueue<>();
		this.dict = new HashMap<>();
		this.wordCount = 0;
	}

	@Override
	public void Compress(String[] input_names, String[] output_names) {

		FileInputStream input;
		int content;

		try {
			input = new FileInputStream(input_names[0]);
			while ((content = input.read()) != -1) {
				freq[content]++;
				wordCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 10000; i++) {
			if (freq[i] > 0)
				q.add(new HuffmanNode((char) i, freq[i], null, null));
		}

		while (q.size() > 1) {
			HuffmanNode left = q.poll();
			HuffmanNode right = q.poll();
			HuffmanNode parent = new HuffmanNode('\0', left.freq + right.freq, left, right);
			q.add(parent);
		}

		HuffmanNode root = q.poll();
		// Keeping a pointer to the tree root to use when decompressing
		treeRoot = root;
		buildDictionary(root, "", dict);
		StringBuilder compressedFile = new StringBuilder();
		try {
			input = new FileInputStream(input_names[0]);
			InputStreamReader file = new InputStreamReader(input, StandardCharsets.UTF_8);
			BufferedReader reader = new BufferedReader(file);
			while ((content = reader.read()) != -1) {
				compressedFile.append(dict.get((char) content));
			}
			writeBitStringToFile(compressedFile, output_names);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void buildDictionary(HuffmanNode node, String string, HashMap<Character, String> dictionary) {
		if (!node.isLeaf()) {
			buildDictionary(node.leftChild, string + '0', dictionary);
			buildDictionary(node.rightChild, string + '1', dictionary);
		} else {
			dictionary.put(node.symbol, string);
		}
	}

	private void writeBitStringToFile(StringBuilder compressedFile, String[] output_names) {
		FileOutputStream fos;
		// Copied from SO
		String s = compressedFile + "00000000".substring(compressedFile.length() % 8);
		try {
			fos = new FileOutputStream(output_names[0]);
			for (int i = 0, len = s.length(); i < len; i += 8)
				fos.write((byte) Integer.parseInt(s.substring(i, i + 8), 2));
			fos.close();
		} catch (Exception e) {
		}

	}

	private String intToByteString(int b) {
		// Copied from SO
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}

	@Override
	public void Decompress(String[] input_names, String[] output_names) {

		StringBuilder decompressedFile = new StringBuilder();
		FileInputStream input;
		FileWriter output;
		int content;
		HuffmanNode node = treeRoot;
		char bit;
		String bitString;

		try {
			input = new FileInputStream(input_names[0]);
			while ((content = input.read()) != -1 && wordCount > 0) {
				boolean moreThanBit = true;
				while (moreThanBit && content != -1) {
					bitString = intToByteString(content);
					for (int j = 0; j < bitString.length() && wordCount > 0; j++) {
						bit = bitString.charAt(j);
						if (bit == '1' && !node.isLeaf()) {
							node = node.rightChild;
						} else if (bit == '0' && !node.isLeaf()) {
							node = node.leftChild;
						}
						if (node.isLeaf()) {
							decompressedFile.append(node.symbol);
							wordCount--;
							node = treeRoot;
						}
					}
					moreThanBit = !node.isLeaf();
					if (moreThanBit && (content = input.read()) != -1) {
					}
				}
				node = treeRoot;
			}
			output = new FileWriter(output_names[0]);
			output.write(decompressedFile.toString());
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public byte[] CompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] DecompressWithArray(String[] input_names, String[] output_names) {
		// TODO Auto-generated method stub
		return null;
	}

}
