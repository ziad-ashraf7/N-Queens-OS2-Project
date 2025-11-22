package com.nqueens.solver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test class for NQueensSolver.
 */
public class NQueensSolverTest {
    
    @Test
    public void testFourQueens() {
        NQueensSolver solver = new NQueensSolver(4, false);
        List<int[]> solutions = solver.solve();
        
        assertEquals(2, solutions.size(), "4-Queens should have 2 solutions");
        
        NQueensSolver.SolverStats stats = solver.getStats();
        assertEquals(2, stats.solutionsFound);
        assertEquals(4, stats.boardSize);
    }
    
    @Test
    public void testEightQueens() {
        NQueensSolver solver = new NQueensSolver(8, false);
        List<int[]> solutions = solver.solve();
        
        assertEquals(92, solutions.size(), "8-Queens should have 92 solutions");
        
        NQueensSolver.SolverStats stats = solver.getStats();
        assertEquals(92, stats.solutionsFound);
        assertEquals(8, stats.boardSize);
    }
    
    @Test
    public void testEightQueensAllThreads() {
        NQueensSolver solver = new NQueensSolver(8, true);
        List<int[]> solutions = solver.solve();
        
        assertEquals(92, solutions.size(), "8-Queens should have 92 solutions");
        
        NQueensSolver.SolverStats stats = solver.getStats();
        assertEquals(8, stats.threadsUsed, "Should use 8 threads in all-threads mode");
    }
    
    @Test
    public void testSolutionValidity() {
        NQueensSolver solver = new NQueensSolver(8, false);
        List<int[]> solutions = solver.solve();
        
        assertFalse(solutions.isEmpty(), "Should find solutions");
        
        for (int[] board : solutions) {
            assertTrue(isValidSolution(board), "All solutions should be valid");
        }
    }
    
    @Test
    public void testThreadCount() {
        int n = 8;
        int numProcessors = Runtime.getRuntime().availableProcessors();
        
        // Test auto mode
        NQueensSolver solverAuto = new NQueensSolver(n, false);
        solverAuto.solve();
        NQueensSolver.SolverStats statsAuto = solverAuto.getStats();
        
        int expectedThreads = Math.min(Math.max(1, n / numProcessors), n);
        assertEquals(expectedThreads, statsAuto.threadsUsed, 
                "Auto mode should use N/processors threads");
        
        // Test all threads mode
        NQueensSolver solverAll = new NQueensSolver(n, true);
        solverAll.solve();
        NQueensSolver.SolverStats statsAll = solverAll.getStats();
        
        assertEquals(n, statsAll.threadsUsed, 
                "All threads mode should use N threads");
    }
    
    @Test
    public void testStopFunctionality() throws InterruptedException {
        NQueensSolver solver = new NQueensSolver(12, false);
        
        // Start solving in a separate thread
        Thread solveThread = new Thread(() -> solver.solve());
        solveThread.start();
        
        // Stop after a short delay
        Thread.sleep(100);
        solver.stop();
        
        solveThread.join(1000); // Wait up to 1 second
        assertFalse(solveThread.isAlive(), "Solver should stop when requested");
    }
    
    @Test
    public void testCallback() {
        final int[] callbackCount = {0};
        
        NQueensSolver.SolverCallback callback = (board, row) -> {
            callbackCount[0]++;
        };
        
        NQueensSolver solver = new NQueensSolver(4, callback, false);
        solver.solve();
        
        assertTrue(callbackCount[0] > 0, "Callback should be called during solving");
    }
    
    /**
     * Validate that a solution is correct (no conflicts).
     */
    private boolean isValidSolution(int[] board) {
        int n = board.length;
        
        // Check all positions are valid
        for (int i = 0; i < n; i++) {
            if (board[i] < 0 || board[i] >= n) {
                return false;
            }
        }
        
        // Check unique columns
        Set<Integer> columns = new HashSet<>();
        for (int col : board) {
            columns.add(col);
        }
        if (columns.size() != n) {
            return false;
        }
        
        // Check no diagonal conflicts
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(board[i] - board[j]) == Math.abs(i - j)) {
                    return false;
                }
            }
        }
        
        return true;
    }
}
