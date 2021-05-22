package assign1;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Assignment 1
 * Submitted by:
 * Ron Cohen. 	ID# 208401349
 * Noam Boni. 	ID# 315586131
 * Shai Buaron. ID# 203236138
 */

// Uncomment if you wish to use FileOutputStream and FileInputStream for file access.
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import base.Compressor;

public class HuffmanEncoderDecoder implements Compressor {
    // This list will contain all symbols frequencys in the given file
    private int[] freq = new int[65537];
    private PriorityQueue<HuffmanNode> q = new PriorityQueue<>();
    private HashMap<Character, String> dict = new HashMap<>();
    private HuffmanNode treeRoot;
    private int wordCount = 0;

    public HuffmanEncoderDecoder() {
        // TODO Auto-generated constructor stub
    }
    private int buildIntFromByteArray(byte[] b){
        if (BitSet.valueOf(b).toLongArray().length > 0) {
            return (int)BitSet.valueOf(b).toLongArray()[0];
        } else {
            return 0;
        }
    }
    private void buildFreq(String[] input_names, byte[] fc) {
        try {
            FileInputStream input = new FileInputStream(input_names[0]);
            for(int i=0;i<fc.length-1;i+=2){
                byte[] word = new byte[2];
                word[0] = fc[i];
                word[1] = fc[i+1];
                int content = buildIntFromByteArray(word);
                freq[content]++;
                wordCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildDict(String[] input_names, byte[] fc) {
        buildFreq(input_names, fc);
        for (int i = 0; i < freq.length; i++) {
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
        treeRoot = root;
        buildDictionary(root, "", dict);
    }

    @Override
    public void Compress(String[] input_names, String[] output_names) {

        try {
            FileInputStream input = new FileInputStream(input_names[0]);
            byte[] fc = input.readAllBytes();
            buildDict(input_names, fc);
            StringBuilder compressedFile = new StringBuilder();
            for(int i=0;i<fc.length-1;i+=2){
                byte[] word = new byte[2];
                word[0] = fc[i];
                word[1] = fc[i+1];
                int content = buildIntFromByteArray(word);
                compressedFile.append(dict.get((char) content));
            }
            writeBitStringToFile(compressedFile, output_names);
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
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    @Override
    public void Decompress(String[] input_names, String[] output_names) {

        StringBuilder decompressedFile = new StringBuilder();
        FileInputStream input;
        FileWriter output;
        int content;
        HuffmanNode node = treeRoot;
        char bit = '\0';
        String bitString = "";
        try {
            input = new FileInputStream(input_names[0]);
            byte[] fc = input.readAllBytes();
            for(int i=0;i<fc.length-1;i+=2){
                byte[] word = new byte[2];
                word[0] = fc[i];
                word[1] = fc[i+1];
                content = buildIntFromByteArray(word);
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
            System.out.println(output_names[0]);
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
