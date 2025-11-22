# N-Queens GUI Application - User Guide

## GUI Layout

The GUI application provides an intuitive interface for solving the N-Queens problem with real-time visualization.

```
┌─────────────────────────────────────────────────────────────────────┐
│ N-Queens Solver - Multi-threaded                                   │
├─────────────────────────────────────────────────────────────────────┤
│ ┌─ Controls ────────────────────────────────────────────────────┐  │
│ │ Board Size (N): [  8  ]  Thread Mode: [auto ▼]               │  │
│ │ [ Start ]  [ Stop ]                                           │  │
│ │                                                               │  │
│ │ Visualization Speed: [fast ▼]                                │  │
│ └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│ ┌─ Statistics ──────────────────────────────────────────────────┐  │
│ │ Solutions: 92 | States Explored: 2,056 | Threads: 2 | 0.02s  │  │
│ └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│ ┌─ Board Visualization ─────────────────────────────────────────┐  │
│ │                                                               │  │
│ │        ┌───┬───┬───┬───┬───┬───┬───┬───┐                     │  │
│ │        │ ♕ │   │   │   │   │   │   │   │                     │  │
│ │        ├───┼───┼───┼───┼───┼───┼───┼───┤                     │  │
│ │        │   │   │   │   │ ♕ │   │   │   │                     │  │
│ │        ├───┼───┼───┼───┼───┼───┼───┼───┤                     │  │
│ │        │   │   │   │   │   │   │   │ ♕ │                     │  │
│ │        ├───┼───┼───┼───┼───┼───┼───┼───┤                     │  │
│ │        │   │   │   │   │   │ ♕ │   │   │                     │  │
│ │        ├───┼───┼───┼───┼───┼───┼───┼───┤                     │  │
│ │        │   │   │ ♕ │   │   │   │   │   │                     │  │
│ │        ├───┼───┼───┼───┼───┼───┼───┼───┤                     │  │
│ │        │   │   │   │   │   │   │ ♕ │   │                     │  │
│ │        ├───┼───┼───┼───┼───┼───┼───┼───┤                     │  │
│ │        │   │ ♕ │   │   │   │   │   │   │                     │  │
│ │        ├───┼───┼───┼───┼───┼───┼───┼───┤                     │  │
│ │        │   │   │   │ ♕ │   │   │   │   │                     │  │
│ │        └───┴───┴───┴───┴───┴───┴───┴───┘                     │  │
│ │                                                               │  │
│ └───────────────────────────────────────────────────────────────┘  │
│                                                                     │
│ [ << Previous ]  Solution 1 of 92  [ Next >> ]                     │
└─────────────────────────────────────────────────────────────────────┘
```

## Features Demonstration

### 1. Input Controls

**Board Size (N):**
- Enter any value ≥ 4
- Recommended: 4-12 for visualization
- Warning shown for N > 20

**Thread Mode:**
- **auto**: Uses N/processors threads (efficient, default)
- **all**: Uses N threads (maximum parallelism)

**Visualization Speed:**
- **instant**: No delay, fastest solving
- **fast**: Quick updates every 0.01s
- **medium**: Moderate speed every 0.05s  
- **slow**: Detailed view every 0.1s

### 2. Real-time Visualization

During solving, the board shows:
- Current queen positions
- Highlighted row being explored
- Animated placement and backtracking
- Live state counter

Example states during solving:

```
State 1: Trying first column    State 2: Exploring row 2
┌───┬───┬───┬───┐              ┌───┬───┬───┬───┐
│ ♕ │   │   │   │              │ ♕ │   │   │   │
├───┼───┼───┼───┤              ├───┼───┼───┼───┤
│   │   │   │   │              │   │   │ ♕ │   │
├───┼───┼───┼───┤              ├───┼───┼───┼───┤
│   │   │   │   │  -->         │   │   │   │   │
├───┼───┼───┼───┤              ├───┼───┼───┼───┤
│   │   │   │   │              │   │   │   │   │
└───┴───┴───┴───┘              └───┴───┴───┴───┘
```

### 3. Solution Navigation

After solving completes:
- Browse all solutions using Previous/Next buttons
- View solution counter (e.g., "Solution 5 of 92")
- Each solution is validated and correct

### 4. Statistics Display

Real-time statistics shown:
```
Solutions: 92 | States Explored: 2,056 | Threads: 2 | Time: 0.02s
```

Shows:
- Total solutions found
- Number of states explored (performance metric)
- Threads used for solving
- Total solving time

## Usage Examples

### Example 1: Learning (4-Queens, Slow Speed)

```
Settings:
- Board Size: 4
- Thread Mode: auto
- Speed: slow

Result:
- Shows detailed step-by-step exploration
- Easy to understand backtracking
- 2 solutions found quickly
```

### Example 2: Classic Problem (8-Queens, Medium Speed)

```
Settings:
- Board Size: 8
- Thread Mode: auto
- Speed: medium

Result:
- Balanced visualization
- 92 solutions in ~1 second
- Good for demonstrations
```

### Example 3: Performance Test (12-Queens, Instant)

```
Settings:
- Board Size: 12
- Thread Mode: all
- Speed: instant

Result:
- Maximum solving speed
- 14,200 solutions in ~5 seconds
- No visualization delay
```

## Visual Board Features

### Color Scheme
- **Light squares**: Beige (#F0D9B5)
- **Dark squares**: Brown (#B58863)
- **Current row**: Highlighted in peach (#FFE5B4)
- **Queens**: Dark red with ♕ symbol (or bold 'Q' as fallback)

### Board Scaling
- Automatically adjusts to window size
- Maintains aspect ratio
- Larger boards use smaller cells
- Queens scale with cell size

### Interactive Elements
- Click controls to start/stop
- Navigation buttons for solutions
- Dropdown menus for settings
- Responsive to window resizing

## What You'll See

### During Solving:
1. **Rapid board updates** showing exploration
2. **Queens appearing/disappearing** as algorithm backtracks
3. **Row highlighting** showing current search position
4. **Live statistics** updating in real-time

### After Solving:
1. **Final solution displayed** on board
2. **Complete statistics** shown
3. **Navigation enabled** to browse solutions
4. **All solutions validated** and correct

## Performance Indicators

### Small Boards (N=4-6)
- **Instant solving** (< 0.01s)
- **Easy to follow** visualization
- **Perfect for learning** the algorithm

### Medium Boards (N=8-10)
- **Quick solving** (< 1s)
- **Good visualization** at fast/medium speed
- **Demonstrative** of multi-threading

### Large Boards (N=12-14)
- **Takes seconds** to solve
- **Use instant speed** for best experience
- **Shows scalability** of approach

## Tips for Best Experience

1. **Start small**: Begin with N=4 or N=8 to understand the problem
2. **Adjust speed**: Use slow speed for learning, instant for performance
3. **Compare modes**: Try both auto and all-threads to see difference
4. **Browse solutions**: Use navigation to see different valid arrangements
5. **Watch statistics**: Observe how states and time grow with N

## Technical Notes

The GUI runs in the main thread and spawns worker threads for solving:
- **Main thread**: Handles GUI updates and user interaction
- **Worker threads**: Explore solution space independently
- **Callback mechanism**: Workers report progress to GUI
- **Thread-safe updates**: Solutions collected safely from all threads

## Troubleshooting

**If GUI is slow:**
- Reduce N (try N=8 or less)
- Use "instant" speed setting
- Close other applications

**If visualization is jumpy:**
- Try "medium" or "fast" speed
- Ensure system has available CPU

**If no solutions appear:**
- Check that N ≥ 4
- Wait for solving to complete
- Check statistics for errors

## Running the GUI

```bash
python3 nqueens_gui.py
```

Requires:
- Python 3.6+
- tkinter (usually pre-installed)

For systems without GUI support, use command-line solver:
```bash
python3 nqueens_solver.py
```

## Educational Value

The GUI helps visualize:
- **Backtracking algorithm** in action
- **Parallel exploration** by multiple threads
- **Solution space** of constraint satisfaction problem
- **Performance characteristics** of threading

Perfect for:
- Operating Systems courses (threading concepts)
- Algorithms courses (backtracking)
- Demonstrations and presentations
- Self-learning and exploration
