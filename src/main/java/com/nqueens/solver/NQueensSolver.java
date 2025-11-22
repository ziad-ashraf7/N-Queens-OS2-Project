package com.nqueens.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multi-threaded N-Queens solver.
 * 
 * The solver divides the work by assigning different starting positions
 * in the first row to different threads. Each thread then explores all
 * possible placements for the remaining queens using backtracking.
 */
public class NQueensSolver {
    
    private final int n;
    private final SolverCallback callback;
    private final boolean useAllThreads;
    private final int numThreads;
    private final List<int[]> solutions;
    private final AtomicInteger statesExplored;
    private final AtomicBoolean stopFlag;
    
    /**
     * Callback interface for visualization during solving.
     */
    public interface SolverCallback {
        void onStateExplored(int[] board, int row);
    }
    
    /**
     * Constructor for N-Queens solver.
     * 
     * @param n Size of the chess board (N x N)
     * @param callback Optional callback for visualization (can be null)
     * @param useAllThreads If true, use N threads; if false, use N/numProcessors threads
     */
    public NQueensSolver(int n, SolverCallback callback, boolean useAllThreads) {
        this.n = n;
        this.callback = callback;
        this.useAllThreads = useAllThreads;
        
        int numProcessors = Runtime.getRuntime().availableProcessors();
        if (useAllThreads) {
            this.numThreads = n;
        } else {
            this.numThreads = Math.min(Math.max(1, n / numProcessors), n);
        }
        
        this.solutions = new CopyOnWriteArrayList<>();
        this.statesExplored = new AtomicInteger(0);
        this.stopFlag = new AtomicBoolean(false);
    }
    
    /**
     * Constructor without callback.
     */
    public NQueensSolver(int n, boolean useAllThreads) {
        this(n, null, useAllThreads);
    }
    
    /**
     * Check if placing a queen at (row, col) is safe.
     * 
     * @param board Current board state
     * @param row Row to check
     * @param col Column to check
     * @return true if position is safe, false otherwise
     */
    private boolean isSafe(int[] board, int row, int col) {
        for (int i = 0; i < row; i++) {
            // Check if same column
            if (board[i] == col) {
                return false;
            }
            // Check diagonals
            if (Math.abs(board[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Recursively solve N-Queens starting from given row.
     * 
     * @param board Current board state
     * @param row Current row to place queen
     */
    private void solveRecursive(int[] board, int row) {
        if (stopFlag.get()) {
            return;
        }
        
        statesExplored.incrementAndGet();
        
        // Call callback if provided
        if (callback != null) {
            callback.onStateExplored(board.clone(), row);
        }
        
        // Base case: all queens placed
        if (row == n) {
            solutions.add(board.clone());
            return;
        }
        
        // Try placing queen in each column of current row
        for (int col = 0; col < n; col++) {
            if (isSafe(board, row, col)) {
                board[row] = col;
                solveRecursive(board, row + 1);
                board[row] = -1; // Backtrack
            }
        }
    }
    
    /**
     * Worker thread that explores solutions starting with queens at given columns.
     */
    private class WorkerThread extends Thread {
        private final List<Integer> startColumns;
        
        public WorkerThread(List<Integer> startColumns) {
            this.startColumns = startColumns;
        }
        
        @Override
        public void run() {
            for (int startCol : startColumns) {
                if (stopFlag.get()) {
                    return;
                }
                int[] board = new int[n];
                for (int i = 0; i < n; i++) {
                    board[i] = -1;
                }
                board[0] = startCol;
                solveRecursive(board, 1);
            }
        }
    }
    
    /**
     * Solve the N-Queens problem using multiple threads.
     * 
     * @return List of solutions, where each solution is an array representing queen positions
     */
    public List<int[]> solve() {
        solutions.clear();
        statesExplored.set(0);
        stopFlag.set(false);
        
        List<Thread> threads = new ArrayList<>();
        
        // Distribute starting columns among threads
        int columnsPerThread = (n + numThreads - 1) / numThreads;
        
        for (int threadId = 0; threadId < numThreads; threadId++) {
            int startIdx = threadId * columnsPerThread;
            int endIdx = Math.min(startIdx + columnsPerThread, n);
            
            if (startIdx >= n) {
                break;
            }
            
            List<Integer> threadColumns = new ArrayList<>();
            for (int col = startIdx; col < endIdx; col++) {
                threadColumns.add(col);
            }
            
            Thread thread = new WorkerThread(threadColumns);
            threads.add(thread);
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return new ArrayList<>(solutions);
    }
    
    /**
     * Stop the solving process.
     */
    public void stop() {
        stopFlag.set(true);
    }
    
    /**
     * Get solving statistics.
     * 
     * @return Statistics object
     */
    public SolverStats getStats() {
        return new SolverStats(solutions.size(), statesExplored.get(), numThreads, n);
    }
    
    /**
     * Statistics class for solver results.
     */
    public static class SolverStats {
        public final int solutionsFound;
        public final int statesExplored;
        public final int threadsUsed;
        public final int boardSize;
        
        public SolverStats(int solutionsFound, int statesExplored, int threadsUsed, int boardSize) {
            this.solutionsFound = solutionsFound;
            this.statesExplored = statesExplored;
            this.threadsUsed = threadsUsed;
            this.boardSize = boardSize;
        }
        
        @Override
        public String toString() {
            return String.format("Solutions: %d | States: %d | Threads: %d | Board: %dx%d",
                    solutionsFound, statesExplored, threadsUsed, boardSize, boardSize);
        }
    }
    
    /**
     * Print a board configuration to console.
     * 
     * @param board Board state
     */
    public static void printBoard(int[] board) {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(board[i] == j ? "Q " : ". ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * Main method for testing the solver.
     */
    public static void main(String[] args) {
        int n = 8;
        System.out.println("Solving " + n + "-Queens problem...");
        
        NQueensSolver solver = new NQueensSolver(n, false);
        long startTime = System.currentTimeMillis();
        List<int[]> solutions = solver.solve();
        long endTime = System.currentTimeMillis();
        
        SolverStats stats = solver.getStats();
        System.out.println("\nFound " + stats.solutionsFound + " solutions");
        System.out.println("Explored " + stats.statesExplored + " states");
        System.out.println("Used " + stats.threadsUsed + " threads");
        System.out.println("Time: " + (endTime - startTime) + "ms");
        
        if (!solutions.isEmpty()) {
            System.out.println("\nFirst solution:");
            printBoard(solutions.get(0));
        }
    }
}
