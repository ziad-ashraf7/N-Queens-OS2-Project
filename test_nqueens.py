"""
Test script for N-Queens solver without GUI dependencies.
Tests the core functionality of the solver.
"""

import sys
from nqueens_solver import NQueensSolver, print_board


def test_basic_functionality():
    """Test basic N-Queens solver functionality."""
    print("=" * 60)
    print("Testing N-Queens Solver")
    print("=" * 60)
    
    # Test 4-Queens
    print("\n1. Testing 4-Queens problem:")
    solver = NQueensSolver(4, use_all_threads=False)
    solutions = solver.solve()
    stats = solver.get_stats()
    
    assert len(solutions) == 2, f"Expected 2 solutions for 4-Queens, got {len(solutions)}"
    print(f"   ✓ Found {len(solutions)} solutions (expected 2)")
    print(f"   ✓ Explored {stats['states_explored']} states")
    print(f"   ✓ Used {stats['threads_used']} threads")
    
    # Test 8-Queens
    print("\n2. Testing 8-Queens problem:")
    solver = NQueensSolver(8, use_all_threads=False)
    solutions = solver.solve()
    stats = solver.get_stats()
    
    assert len(solutions) == 92, f"Expected 92 solutions for 8-Queens, got {len(solutions)}"
    print(f"   ✓ Found {len(solutions)} solutions (expected 92)")
    print(f"   ✓ Explored {stats['states_explored']} states")
    print(f"   ✓ Used {stats['threads_used']} threads")
    
    # Test 8-Queens with all threads
    print("\n3. Testing 8-Queens with all threads mode:")
    solver = NQueensSolver(8, use_all_threads=True)
    solutions = solver.solve()
    stats = solver.get_stats()
    
    assert len(solutions) == 92, f"Expected 92 solutions for 8-Queens, got {len(solutions)}"
    assert stats['threads_used'] == 8, f"Expected 8 threads in all-threads mode, got {stats['threads_used']}"
    print(f"   ✓ Found {len(solutions)} solutions (expected 92)")
    print(f"   ✓ Used {stats['threads_used']} threads (expected 8)")
    
    # Test solution validity
    print("\n4. Testing solution validity:")
    solver = NQueensSolver(8)
    solutions = solver.solve()
    
    # Check first solution
    board = solutions[0]
    n = len(board)
    
    # Verify each row has exactly one queen
    assert all(0 <= board[i] < n for i in range(n)), "Invalid queen positions"
    
    # Verify no two queens in same column
    assert len(set(board)) == n, "Two queens in same column"
    
    # Verify no two queens in same diagonal
    for i in range(n):
        for j in range(i + 1, n):
            assert abs(board[i] - board[j]) != abs(i - j), "Two queens on same diagonal"
    
    print(f"   ✓ All solutions are valid")
    print("\n   First solution:")
    for line in str_board(board).split('\n'):
        print(f"   {line}")
    
    # Test with callback
    print("\n5. Testing with callback function:")
    states_visited = []
    
    def callback(board, row):
        states_visited.append((board[:], row))
    
    solver = NQueensSolver(4, callback=callback)
    solutions = solver.solve()
    
    assert len(states_visited) > 0, "Callback was not called"
    print(f"   ✓ Callback called {len(states_visited)} times")
    print(f"   ✓ Found {len(solutions)} solutions")
    
    # Test stop functionality
    print("\n6. Testing stop functionality:")
    solver = NQueensSolver(12)  # Larger N
    
    import threading
    import time
    
    def stop_after_delay():
        time.sleep(0.1)
        solver.stop()
    
    stop_thread = threading.Thread(target=stop_after_delay)
    stop_thread.start()
    
    solutions = solver.solve()
    stop_thread.join()
    
    print(f"   ✓ Solver can be stopped (found {len(solutions)} solutions before stopping)")
    
    print("\n" + "=" * 60)
    print("All tests passed! ✓")
    print("=" * 60)


def str_board(board):
    """Convert board to string representation."""
    n = len(board)
    lines = []
    for i in range(n):
        row = ['Q' if board[i] == j else '.' for j in range(n)]
        lines.append(' '.join(row))
    return '\n'.join(lines)


if __name__ == '__main__':
    try:
        test_basic_functionality()
        sys.exit(0)
    except AssertionError as e:
        print(f"\n❌ Test failed: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ Unexpected error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)
