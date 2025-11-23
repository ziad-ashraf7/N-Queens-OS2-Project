package com.example.os2;

/**
 * Board class representing an N×N chessboard for the N-Queens problem
 */
public class Board {
    private final int size;
    private final int[] queens; // queens[row] = column position (-1 means no queen)

    public Board(int size) {
        this.size = size;
        this.queens = new int[size];
        for (int i = 0; i < size; i++) {
            queens[i] = -1;
        }
    }

    /**
     * Copy constructor for creating a deep copy of the board
     */
    public Board(Board other) {
        this.size = other.size;
        this.queens = other.queens.clone();
    }

    /**
     * Check if placing a queen at (row, col) is safe
     */
    public boolean isSafe(int row, int col) {
        // Check all previous rows
        for (int i = 0; i < row; i++) {
            // Check column conflict
            if (queens[i] == col) {
                return false;
            }
            // Check diagonal conflict
            if (Math.abs(queens[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Place a queen at the specified position
     */
    public void placeQueen(int row, int col) {
        queens[row] = col;
    }

    /**
     * Remove a queen from the specified row
     */
    public void removeQueen(int row) {
        queens[row] = -1;
    }

    /**
     * Get the column position of the queen in the specified row
     */
    public int getQueenPosition(int row) {
        return queens[row];
    }

    /**
     * Get the board size
     */
    public int getSize() {
        return size;
    }

    /**
     * Check if this board represents a complete solution
     */
    public boolean isSolution() {
        for (int i = 0; i < size; i++) {
            if (queens[i] == -1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (queens[row] == col) {
                    sb.append("Q ");
                } else {
                    sb.append(". ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

