# N-Queens Solver - Multi-threaded Implementation

A multi-threaded solution to the classic N-Queens problem with real-time GUI visualization.

## Overview

This project solves the N-Queens problem by placing N queens on an N×N chessboard so that no two queens attack each other (no shared row, column, or diagonal). The solution uses multi-threading to explore the solution space efficiently.

## Features

- **Multi-threaded Solver**: Uses N threads or N/x threads (where x = number of processors) to explore different branches of the solution space in parallel
- **Real-time Visualization**: GUI displays the board exploration in real-time as threads search for solutions
- **Flexible Threading**: Choose between automatic thread count (N/processors) or using N threads
- **Solution Navigation**: Browse through all found solutions
- **Adjustable Visualization Speed**: Control the speed of real-time board exploration
- **Performance Statistics**: View number of solutions found, states explored, threads used, and solving time

## Requirements

- Python 3.6 or higher
- tkinter (usually included with Python)

## Installation

No additional packages need to be installed if you have Python 3 with tkinter support.

To verify tkinter is available:
```bash
python3 -c "import tkinter"
```

## Usage

### GUI Application

Run the graphical interface:
```bash
python3 nqueens_gui.py
```

**Controls:**
- **Board Size (N)**: Enter the size of the chessboard (minimum 4)
- **Thread Mode**: 
  - `auto`: Uses N/processors threads (default, more efficient)
  - `all`: Uses N threads (one per starting column)
- **Visualization Speed**: Control how fast the board updates during exploration
  - `instant`: No visualization delay (fastest)
  - `fast`: Quick updates
  - `medium`: Moderate speed
  - `slow`: Detailed visualization
- **Start**: Begin solving
- **Stop**: Halt the solving process
- **Navigation**: Use Previous/Next buttons to browse through solutions

### Command Line

Run the solver directly:
```bash
python3 nqueens_solver.py
```

Or use it as a module:
```python
from nqueens_solver import NQueensSolver

# Create solver for 8x8 board
solver = NQueensSolver(8, use_all_threads=False)

# Solve
solutions = solver.solve()

# Get statistics
stats = solver.get_stats()
print(f"Found {stats['solutions_found']} solutions")
print(f"Used {stats['threads_used']} threads")
```

## Algorithm

### N-Queens Problem

The N-Queens problem asks to place N queens on an N×N chessboard such that:
- No two queens share the same row
- No two queens share the same column  
- No two queens share the same diagonal

### Multi-threading Strategy

1. **Work Distribution**: The first row of the board is divided among threads
   - Each thread starts with a queen in a specific column of the first row
   - The thread then recursively explores all possible placements for remaining queens
   
2. **Thread Count**: 
   - **Auto mode**: Uses N/processors threads to balance parallelism with system resources
   - **All threads mode**: Uses N threads (one per starting column in first row)
   - For large N, threads may handle multiple starting positions

3. **Thread Safety**: Solutions are collected in a thread-safe manner using locks

4. **Backtracking**: Each thread uses backtracking to explore the solution space
   - Place a queen in the current row
   - Check if placement is safe (no conflicts)
   - Recursively solve for next row
   - Backtrack if no valid placement found

### Complexity

- **Time Complexity**: O(N!) in the worst case, but pruning significantly reduces actual states explored
- **Space Complexity**: O(N) for each thread's board state
- **Parallelization**: Near-linear speedup with multiple threads for large N

## Example Results

### 8-Queens Problem
- Solutions: 92
- Typical states explored: ~2,000
- Threads used: 2-8 (depending on mode and CPU count)

### 12-Queens Problem
- Solutions: 14,200
- States explored: ~150,000+
- Threads used: 3-12 (depending on mode and CPU count)

## Project Structure

```
N-Queens-OS2-Project/
├── README.md              # This file
├── nqueens_solver.py      # Core solver with multi-threading
└── nqueens_gui.py         # GUI application
```

## Testing

Test with different board sizes:
```bash
# Test 4-Queens (simple case)
python3 -c "from nqueens_solver import NQueensSolver; s = NQueensSolver(4); print(f'4-Queens: {len(s.solve())} solutions')"

# Test 8-Queens (classic case)  
python3 -c "from nqueens_solver import NQueensSolver; s = NQueensSolver(8); print(f'8-Queens: {len(s.solve())} solutions')"

# Test 12-Queens (larger case)
python3 -c "from nqueens_solver import NQueensSolver; s = NQueensSolver(12); print(f'12-Queens: {len(s.solve())} solutions')"
```

Expected results:
- 4-Queens: 2 solutions
- 8-Queens: 92 solutions
- 12-Queens: 14,200 solutions

## OS Concepts Demonstrated

This project demonstrates several important Operating Systems concepts:

1. **Multi-threading**: Multiple threads working on independent sub-problems
2. **Thread Synchronization**: Using locks to protect shared data (solutions list)
3. **Work Distribution**: Dividing work among threads based on system resources
4. **Thread Pooling**: Managing a pool of worker threads
5. **Parallel Computation**: Achieving speedup through concurrent execution

## License

This is an educational project for Operating Systems coursework.

## Author

Created for OS2 (Operating Systems 2) coursework.