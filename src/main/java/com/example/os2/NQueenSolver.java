package com.example.os2;

import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

/**
 * Thread class that solves N-Queens problem starting from a specific column in the first row
 */
public class NQueenSolver extends Thread {
    private final int startColumn;
    private final int boardSize;
    private final NQueenController controller;
    private final int threadId;
    private final List<Board> solutions;
    private volatile boolean running = true;
    private int stepsExplored = 0;
    private boolean foundSolution = false;

    public NQueenSolver(int startColumn, int boardSize, NQueenController controller, int threadId) {
        this.startColumn = startColumn;
        this.boardSize = boardSize;
        this.controller = controller;
        this.threadId = threadId;
        this.solutions = new ArrayList<>();
        this.setName("NQueen-Thread-" + threadId);
    }
    
    @Override
    public void run() {
        Board board = new Board(boardSize);
        board.placeQueen(0, startColumn);
        
        // Update GUI with initial state
        Platform.runLater(() -> 
            controller.updateBoard(threadId, board, "Starting at column " + startColumn)
        );
        
        try {
            Thread.sleep(300); // Initial delay for visualization
        } catch (InterruptedException e) {
            return;
        }
        
        // Start solving from row 1 (row 0 already has queen at startColumn)
        solve(board, 1);
        
        // If no solution found, show final state
        if (!foundSolution && running) {
            Platform.runLater(() -> {
                // Clear board to show no solution
                Board emptyBoard = new Board(boardSize);
                emptyBoard.placeQueen(0, startColumn);
                controller.updateBoard(threadId, emptyBoard,
                    "No solution from this start (explored " + stepsExplored + " steps)");
            });
        }

        // Notify completion
        Platform.runLater(() ->
            controller.threadCompleted(threadId, solutions.size(), stepsExplored)
        );
    }
    
    /**
     * Recursive backtracking algorithm to solve N-Queens
     */
    private void solve(Board board, int row) {
        // Stop only if this thread was manually stopped or found its solution
        if (!running || foundSolution) {
            return;
        }
        
        // Base case: all queens placed successfully
        if (row == boardSize) {
            if (board.isSolution()) {
                solutions.add(new Board(board));
                foundSolution = true;

                // Update the board one final time with the solution
                Platform.runLater(() -> {
                    controller.updateBoard(threadId, new Board(board), "🏆 SOLUTION FOUND - FROZEN!");
                    controller.solutionFound(threadId, new Board(board));
                });

                // Stop only this thread's execution
                running = false;
                return;
            }
            return;
        }
        
        // Try placing queen in each column of current row
        for (int col = 0; col < boardSize; col++) {
            // Check only this thread's state
            if (!running || foundSolution) {
                return;
            }
            
            stepsExplored++;
            
            if (board.isSafe(row, col)) {
                board.placeQueen(row, col);
                
                // Update GUI with current exploration state
                final int currentRow = row;
                final int currentCol = col;
                Platform.runLater(() -> {
                    controller.updateBoard(threadId, new Board(board), 
                        "Exploring row " + currentRow + ", col " + currentCol + " (Steps: " + stepsExplored + ")");
                });
                
                try {
                    // Delay for visualization (adjust based on board size)
                    int delay = boardSize <= 6 ? 150 : (boardSize <= 8 ? 100 : 50);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    running = false;
                    return;
                }
                
                // Recursively solve for next row
                solve(board, row + 1);
                
                // If this thread found solution, stop backtracking
                if (!running || foundSolution) {
                    return;
                }

                // Backtrack if no solution found yet
                board.removeQueen(row);
                
                // Show backtracking in GUI
                Platform.runLater(() -> {
                    controller.updateBoard(threadId, new Board(board), 
                        "Backtracking from row " + currentRow + " (Steps: " + stepsExplored + ")");
                });
                
                try {
                    int delay = boardSize <= 6 ? 100 : (boardSize <= 8 ? 50 : 25);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    running = false;
                    return;
                }
            }
        }
    }
    
    /**
     * Stop the solver thread gracefully
     */
    public void stopSolver() {
        running = false;
    }
    
    /**
     * Get all solutions found by this thread
     */
    public List<Board> getSolutions() {
        return solutions;
    }
    
    /**
     * Get the number of steps explored
     */
    public int getStepsExplored() {
        return stepsExplored;
    }
}
