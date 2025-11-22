# N-Queens Multi-threaded Solver - Quick Start Guide

## What is the N-Queens Problem?

Place N chess queens on an N×N board so that no two queens attack each other:
- No two queens in the same row
- No two queens in the same column
- No two queens on the same diagonal

## Quick Start

### Running the GUI Application

```bash
python3 nqueens_gui.py
```

**Note:** The GUI requires tkinter (included with most Python installations).

### Running from Command Line

```bash
# Default example (8-Queens)
python3 nqueens_solver.py

# Run the demo
python3 demo.py

# Run tests
python3 test_nqueens.py
```

## Using the Solver Programmatically

```python
from nqueens_solver import NQueensSolver

# Create solver for N=8 with automatic thread count
solver = NQueensSolver(8, use_all_threads=False)

# Solve the problem
solutions = solver.solve()

# Get statistics
stats = solver.get_stats()
print(f"Found {stats['solutions_found']} solutions")
print(f"Used {stats['threads_used']} threads")
print(f"Explored {stats['states_explored']} states")

# Print first solution
if solutions:
    board = solutions[0]
    n = len(board)
    for i in range(n):
        row = ['Q' if board[i] == j else '.' for j in range(n)]
        print(' '.join(row))
```

## Threading Modes

### Auto Mode (Recommended)
```python
solver = NQueensSolver(8, use_all_threads=False)
```
- Uses N/processors threads
- More efficient resource usage
- Example: 8-Queens on 4-core CPU → 2 threads
- Each thread handles multiple starting positions

### All Threads Mode
```python
solver = NQueensSolver(8, use_all_threads=True)
```
- Uses N threads (one per starting column)
- Maximum parallelism
- Example: 8-Queens → 8 threads
- Good for larger N values

## Real-time Visualization

You can add a callback to monitor solving progress:

```python
def progress_callback(board, row):
    """Called for each state explored"""
    print(f"Exploring row {row}")
    # board contains current queen positions

solver = NQueensSolver(8, callback=progress_callback)
solutions = solver.solve()
```

## GUI Controls

1. **Board Size (N)**: Enter the board size (minimum 4)
2. **Thread Mode**: 
   - `auto`: Efficient (N/processors threads)
   - `all`: Maximum parallelism (N threads)
3. **Visualization Speed**:
   - `instant`: No delay (fastest solving)
   - `fast`: Quick visualization
   - `medium`: Moderate speed
   - `slow`: Detailed step-by-step
4. **Start/Stop**: Control solving
5. **Previous/Next**: Browse solutions after solving

## Expected Results

| N | Solutions | Typical States Explored |
|---|-----------|------------------------|
| 4 | 2 | 16 |
| 8 | 92 | ~2,000 |
| 10 | 724 | ~35,000 |
| 12 | 14,200 | ~850,000 |

## Performance Tips

1. **For learning/visualization**: Use smaller N (4-8) with slow speed
2. **For finding solutions quickly**: Use larger N with instant visualization
3. **For demonstration**: Use N=8 with medium speed
4. **For performance testing**: Use N=12-14 with instant speed

## How the Threading Works

1. **Work Distribution**: 
   - First row is divided among threads
   - Example: 8-Queens with 2 threads
     - Thread 1: explores starting columns 0-3
     - Thread 2: explores starting columns 4-7

2. **Independent Exploration**:
   - Each thread explores all possibilities for its starting positions
   - No communication between threads (embarrassingly parallel)
   - Solutions collected in thread-safe manner

3. **Backtracking Algorithm**:
   - Place queen in current row
   - Check if safe (no conflicts)
   - Recursively solve next row
   - Backtrack if stuck

## Stopping the Solver

```python
import threading
import time

solver = NQueensSolver(12)

# Start solving in background
solve_thread = threading.Thread(target=solver.solve)
solve_thread.start()

# Stop after 1 second
time.sleep(1)
solver.stop()
solve_thread.join()

print(f"Found {len(solver.solutions)} solutions before stopping")
```

## Troubleshooting

### GUI doesn't start
- **Issue**: `ModuleNotFoundError: No module named 'tkinter'`
- **Solution**: Install tkinter:
  - Ubuntu/Debian: `sudo apt-get install python3-tk`
  - macOS: tkinter is included with Python
  - Windows: tkinter is included with Python

### Too slow for large N
- Use `instant` visualization speed
- Use `auto` thread mode instead of `all`
- Consider that N=14+ can take several minutes

### Unicode queen symbol not showing
- The GUI automatically falls back to bold 'Q'
- This is normal on some systems

## Educational Value

This project demonstrates:
- **Multi-threading**: Parallel execution of independent tasks
- **Thread synchronization**: Thread-safe data structures
- **Work distribution**: Dividing problems among workers
- **Backtracking algorithms**: Recursive problem solving
- **GUI programming**: Real-time visualization

## Files

- `nqueens_solver.py` - Core solver with threading
- `nqueens_gui.py` - GUI application
- `test_nqueens.py` - Test suite
- `demo.py` - Demonstration script
- `README.md` - Full documentation
- `QUICKSTART.md` - This file

## Further Reading

- [N-Queens Problem (Wikipedia)](https://en.wikipedia.org/wiki/Eight_queens_puzzle)
- [Backtracking (Wikipedia)](https://en.wikipedia.org/wiki/Backtracking)
- [Python Threading](https://docs.python.org/3/library/threading.html)

## License

Educational project for Operating Systems coursework.
