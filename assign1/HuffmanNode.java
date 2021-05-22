/**
 * Assignment 1
 * Submitted by:
 * Ron Cohen. 	ID# 208401349
 * Noam Boni. 	ID# 315586131
 * Shai Buaron. ID# 203236138
 */
package assign1;

public class HuffmanNode implements Comparable<HuffmanNode> {
    public char symbol;
    public int freq;
    public HuffmanNode leftChild;
    public HuffmanNode rightChild;

    public HuffmanNode(char symbol, int freq, HuffmanNode leftChild, HuffmanNode rightChild) {
        this.symbol = symbol;
        this.freq = freq;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    boolean isLeaf() {
        return this.leftChild == null && this.rightChild == null;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        int freqcompare = Integer.compare(this.freq, other.freq);
        if (freqcompare != 0)
            return freqcompare;

        return Integer.compare(this.symbol, other.symbol);
    }


}
