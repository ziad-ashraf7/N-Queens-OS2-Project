#!/usr/bin/env python3
"""
Demo script showing N-Queens solver output without GUI.
This demonstrates the solver functionality and output format.
"""

import time
from nqueens_solver import NQueensSolver, print_board


def demo_solver():
    """Demonstrate the N-Queens solver with visual output."""
    
    print("=" * 70)
    print(" N-QUEENS MULTI-THREADED SOLVER DEMO")
    print("=" * 70)
    
    # Demo 1: Small board with visualization
    print("\n📌 Demo 1: 4-Queens Problem with Step-by-Step Visualization")
    print("-" * 70)
    
    step_count = [0]
    
    def visualization_callback(board, row):
        step_count[0] += 1
        if step_count[0] % 5 == 1:  # Show every 5th step
            print(f"\nStep {step_count[0]}: Exploring row {row}")
            for i in range(len(board)):
                if board[i] >= 0:
                    row_str = ['Q' if board[i] == j else '.' for j in range(len(board))]
                    print(f"  Row {i}: {' '.join(row_str)}")
    
    solver = NQueensSolver(4, callback=visualization_callback)
    print("Starting solver...")
    start_time = time.time()
    solutions = solver.solve()
    elapsed = time.time() - start_time
    
    stats = solver.get_stats()
    print(f"\n✓ Completed in {elapsed:.3f} seconds")
    print(f"  Solutions found: {stats['solutions_found']}")
    print(f"  States explored: {stats['states_explored']}")
    print(f"  Threads used: {stats['threads_used']}")
    
    print("\nAll solutions for 4-Queens:")
    for i, solution in enumerate(solutions, 1):
        print(f"\nSolution {i}:")
        print_board(solution)
    
    # Demo 2: Classic 8-Queens
    print("\n" + "=" * 70)
    print("📌 Demo 2: Classic 8-Queens Problem")
    print("-" * 70)
    
    print("Testing with AUTO thread mode (N/processors)...")
    solver_auto = NQueensSolver(8, use_all_threads=False)
    start_time = time.time()
    solutions_auto = solver_auto.solve()
    elapsed_auto = time.time() - start_time
    stats_auto = solver_auto.get_stats()
    
    print(f"✓ AUTO mode completed in {elapsed_auto:.3f} seconds")
    print(f"  Solutions: {stats_auto['solutions_found']}")
    print(f"  States explored: {stats_auto['states_explored']:,}")
    print(f"  Threads: {stats_auto['threads_used']}")
    
    print("\nTesting with ALL THREADS mode (N threads)...")
    solver_all = NQueensSolver(8, use_all_threads=True)
    start_time = time.time()
    solutions_all = solver_all.solve()
    elapsed_all = time.time() - start_time
    stats_all = solver_all.get_stats()
    
    print(f"✓ ALL THREADS mode completed in {elapsed_all:.3f} seconds")
    print(f"  Solutions: {stats_all['solutions_found']}")
    print(f"  States explored: {stats_all['states_explored']:,}")
    print(f"  Threads: {stats_all['threads_used']}")
    
    print(f"\nSpeedup with more threads: {elapsed_auto/elapsed_all:.2f}x")
    
    print("\nFirst 3 solutions for 8-Queens:")
    for i, solution in enumerate(solutions_auto[:3], 1):
        print(f"\nSolution {i}:")
        print_board(solution)
    
    print(f"(... and {len(solutions_auto) - 3} more solutions)")
    
    # Demo 3: Larger board
    print("\n" + "=" * 70)
    print("📌 Demo 3: 12-Queens Problem (Larger Board)")
    print("-" * 70)
    print("This demonstrates the solver's capability with larger boards...")
    
    solver_12 = NQueensSolver(12, use_all_threads=False)
    start_time = time.time()
    solutions_12 = solver_12.solve()
    elapsed_12 = time.time() - start_time
    stats_12 = solver_12.get_stats()
    
    print(f"\n✓ Completed in {elapsed_12:.2f} seconds")
    print(f"  Solutions found: {stats_12['solutions_found']:,}")
    print(f"  States explored: {stats_12['states_explored']:,}")
    print(f"  Threads used: {stats_12['threads_used']}")
    
    print("\nFirst solution for 12-Queens:")
    print_board(solutions_12[0])
    
    # Summary
    print("\n" + "=" * 70)
    print("✅ DEMO COMPLETE")
    print("=" * 70)
    print("\nKey Features Demonstrated:")
    print("  ✓ Multi-threaded solving (N threads or N/processors)")
    print("  ✓ Correct solutions for various board sizes")
    print("  ✓ Real-time progress callbacks")
    print("  ✓ Performance statistics tracking")
    print("  ✓ Scalability to larger boards")
    print("\nTo use the GUI: python3 nqueens_gui.py")
    print("  (Requires tkinter - may not work in headless environments)")
    print("=" * 70)


if __name__ == '__main__':
    demo_solver()
