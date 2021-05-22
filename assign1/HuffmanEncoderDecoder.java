package assign1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.io.FileOutputStream;
import java.io.FileInputStream;

/**
 * Assignment 1
 * Submitted by:
 * Ron Cohen. 	ID# 208401349
 * Noam Boni. 	ID# 315586131
 * Shai Buaron. ID# 203236138
 */

// Uncomment if you wish to use FileOutputStream and FileInputStream for file access.

import base.Compressor;

public class HuffmanEncoderDecoder implements Compressor {
    // This list will contain all symbols frequencys in the given file
    private int[] freq = new int[65537];
    private PriorityQueue<HuffmanNode> q = new PriorityQueue<>();
    private HashMap<Integer, String> dict = new HashMap<>();
    private HuffmanNode treeRoot;
    private int wordCount = 0;

    public HuffmanEncoderDecoder() {
        // TODO Auto-generated constructor stub
    }

    private int buildIntFromByteArray(byte[] b) {
        if (BitSet.valueOf(b).toLongArray().length > 0) {
            return (int) BitSet.valueOf(b).toLongArray()[0];
        } else {
            return 0;
        }
    }

    private void buildFreq(String[] input_names, byte[] fc) {
        try {
            for (int i = 0; i < fc.length - 1; i += 2) {
                byte[] word = new byte[2];
                word[0] = fc[i];
                word[1] = fc[i + 1];
                int content = buildIntFromByteArray(word);
                freq[content]++;
                wordCount += 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildDict(String[] input_names, byte[] fc) {
        buildFreq(input_names, fc);
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > 0)
                q.add(new HuffmanNode(i, freq[i], null, null));
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
            Path inputt = Paths.get(input_names[0]);
            byte[] fc = Files.readAllBytes(inputt);
            buildDict(input_names, fc);
            StringBuilder compressedFile = new StringBuilder();
            for (int i = 0; i < fc.length - 1; i += 2) {
                byte[] word = new byte[2];
                word[0] = fc[i];
                word[1] = fc[i + 1];
                int content = buildIntFromByteArray(word);
                compressedFile.append(dict.get(content));
            }
            writeBitStringToFile(compressedFile, output_names);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void buildDictionary(HuffmanNode node, String string, HashMap<Integer, String> dictionary) {
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

        ArrayList<Byte> decompressedFile = new ArrayList<Byte>();
        FileInputStream input;
        FileOutputStream output;
        int content;
        HuffmanNode node = treeRoot;
        char bit = '\0';
        String bitString = "";
        try {
            input = new FileInputStream(input_names[0]);
            Path inputt = Paths.get(input_names[0]);
            byte[] fc = Files.readAllBytes(inputt);
            for (int i = 0; i < fc.length - 1; i += 2) {
                content = input.read();
                byte[] word = new byte[2];
                word[0] = fc[i];
                word[1] = fc[i + 1];
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
                            decompressedFile.add(ByteBuffer.allocate(4).putInt(node.symbol).array()[3]);
                            decompressedFile.add(ByteBuffer.allocate(4).putInt(node.symbol).array()[2]);
                            wordCount -= 2;
                            node = treeRoot;
                        }
                    }
                    moreThanBit = !node.isLeaf();
                    if (moreThanBit && ((content = input.read()) != -1)) {
                    }
                }
                node = treeRoot;
            }
            System.out.println(output_names[0]);
            output = new FileOutputStream(output_names[0]);
            byte[] tmp = new byte[decompressedFile.size()];
            for (int i = 0; i < tmp.length; i++) {
                tmp[i] = decompressedFile.get(i);
            }
            output.write(tmp);
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
