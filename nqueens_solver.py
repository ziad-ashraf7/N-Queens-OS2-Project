"""
N-Queens Solver with Multi-threading Support

This module implements the N-Queens problem solver using multiple threads.
Each thread explores a portion of the solution space by starting with a 
fixed queen position in the first row.
"""

import threading
import multiprocessing
from queue import Queue
from typing import List, Tuple, Callable, Optional


class NQueensSolver:
    """
    Multi-threaded N-Queens solver.
    
    The solver divides the work by assigning different starting positions
    in the first row to different threads. Each thread then explores all
    possible placements for the remaining queens.
    """
    
    def __init__(self, n: int, callback: Optional[Callable] = None, use_all_threads: bool = False):
        """
        Initialize the N-Queens solver.
        
        Args:
            n: Size of the chess board (N x N)
            callback: Optional callback function called when exploring each state
                     Signature: callback(board, row)
            use_all_threads: If True, use N threads; if False, use N/num_processors threads
        """
        self.n = n
        self.callback = callback
        self.solutions = []
        self.solutions_lock = threading.Lock()
        self.stop_flag = threading.Event()
        
        # Determine number of threads to use
        num_processors = multiprocessing.cpu_count()
        if use_all_threads:
            self.num_threads = n
        else:
            # Use N/x threads where x is the number of processors
            self.num_threads = max(1, n // num_processors)
            # Ensure we don't exceed N threads
            self.num_threads = min(self.num_threads, n)
        
        self.states_explored = 0
        self.states_lock = threading.Lock()
    
    def is_safe(self, board: List[int], row: int, col: int) -> bool:
        """
        Check if placing a queen at (row, col) is safe.
        
        Args:
            board: Current board state (board[i] = j means queen at row i, col j)
            row: Row to check
            col: Column to check
            
        Returns:
            True if position is safe, False otherwise
        """
        # Check column and diagonals for conflicts with previous rows
        for i in range(row):
            # Check if same column
            if board[i] == col:
                return False
            # Check diagonals
            if abs(board[i] - col) == abs(i - row):
                return False
        return True
    
    def solve_recursive(self, board: List[int], row: int):
        """
        Recursively solve N-Queens starting from given row.
        
        Args:
            board: Current board state
            row: Current row to place queen
        """
        if self.stop_flag.is_set():
            return
        
        # Increment states explored counter
        with self.states_lock:
            self.states_explored += 1
        
        # Call callback if provided for visualization
        if self.callback:
            self.callback(board[:], row)
        
        # Base case: all queens placed
        if row == self.n:
            with self.solutions_lock:
                self.solutions.append(board[:])
            return
        
        # Try placing queen in each column of current row
        for col in range(self.n):
            if self.is_safe(board, row, col):
                board[row] = col
                self.solve_recursive(board, row + 1)
                board[row] = -1  # Backtrack
    
    def worker_thread(self, start_col: int):
        """
        Worker thread that explores solutions starting with queen at (0, start_col).
        
        Args:
            start_col: Column position for queen in first row
        """
        board = [-1] * self.n
        board[0] = start_col
        self.solve_recursive(board, 1)
    
    def solve(self) -> List[List[int]]:
        """
        Solve the N-Queens problem using multiple threads.
        
        Returns:
            List of solutions, where each solution is a list representing queen positions
        """
        self.solutions = []
        self.states_explored = 0
        self.stop_flag.clear()
        
        threads = []
        
        # Distribute work among threads
        # Each thread starts with a different column in the first row
        positions_per_thread = max(1, self.n // self.num_threads)
        
        for thread_id in range(self.num_threads):
            # Calculate which starting positions this thread handles
            start_pos = thread_id * positions_per_thread
            end_pos = start_pos + positions_per_thread
            
            # Last thread handles remaining positions
            if thread_id == self.num_threads - 1:
                end_pos = self.n
            
            # Create a thread for each starting position in this thread's range
            for col in range(start_pos, end_pos):
                t = threading.Thread(target=self.worker_thread, args=(col,))
                threads.append(t)
                t.start()
        
        # Wait for all threads to complete
        for t in threads:
            t.join()
        
        return self.solutions
    
    def stop(self):
        """Stop the solving process."""
        self.stop_flag.set()
    
    def get_stats(self) -> dict:
        """
        Get solving statistics.
        
        Returns:
            Dictionary with statistics
        """
        return {
            'solutions_found': len(self.solutions),
            'states_explored': self.states_explored,
            'threads_used': self.num_threads,
            'board_size': self.n
        }


def print_board(board: List[int]):
    """
    Print a board configuration.
    
    Args:
        board: Board state (board[i] = j means queen at row i, col j)
    """
    n = len(board)
    for i in range(n):
        row = ['Q' if board[i] == j else '.' for j in range(n)]
        print(' '.join(row))
    print()


if __name__ == '__main__':
    # Example usage
    n = 8
    print(f"Solving {n}-Queens problem...")
    
    solver = NQueensSolver(n, use_all_threads=False)
    solutions = solver.solve()
    
    stats = solver.get_stats()
    print(f"\nFound {stats['solutions_found']} solutions")
    print(f"Explored {stats['states_explored']} states")
    print(f"Used {stats['threads_used']} threads")
    
    if solutions:
        print("\nFirst solution:")
        print_board(solutions[0])
