# N-Queens Solver - Multi-threaded Java Implementation

A multi-threaded solution to the classic N-Queens problem with JavaFX real-time GUI visualization.

## Overview

This project solves the N-Queens problem by placing N queens on an N×N chessboard so that no two queens attack each other (no shared row, column, or diagonal). The solution uses multi-threading to explore the solution space efficiently.

## Features

- **Multi-threaded Solver**: Uses N threads or N/x threads (where x = number of processors) to explore different branches of the solution space in parallel
- **Real-time Visualization**: JavaFX GUI displays the board exploration in real-time as threads search for solutions
- **Flexible Threading**: Choose between automatic thread count (N/processors) or using N threads
- **Solution Navigation**: Browse through all found solutions
- **Adjustable Visualization Speed**: Control the speed of real-time board exploration
- **Performance Statistics**: View number of solutions found, states explored, threads used, and solving time

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- JavaFX 17 (automatically managed by Maven)

## Building the Project

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn package
```

## Running the Application

### GUI Application

```bash
# Run with Maven
mvn javafx:run
```

### Command Line Solver

```bash
# Compile and run
mvn compile
mvn exec:java -Dexec.mainClass="com.nqueens.solver.NQueensSolver"
```

## Usage

### GUI Application

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

### Programmatic Usage

```java
import com.nqueens.solver.NQueensSolver;
import java.util.List;

// Create solver for 8x8 board
NQueensSolver solver = new NQueensSolver(8, false);

// Solve
List<int[]> solutions = solver.solve();

// Get statistics
NQueensSolver.SolverStats stats = solver.getStats();
System.out.println(stats);

// Print first solution
if (!solutions.isEmpty()) {
    NQueensSolver.printBoard(solutions.get(0));
}
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

3. **Thread Safety**: Solutions are collected in a thread-safe `CopyOnWriteArrayList`

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
- States explored: ~850,000+
- Threads used: 3-12 (depending on mode and CPU count)

## Project Structure

```
N-Queens-OS2-Project/
├── pom.xml                           # Maven configuration
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── nqueens/
│   │               ├── solver/
│   │               │   └── NQueensSolver.java    # Core solver
│   │               └── gui/
│   │                   └── NQueensApp.java       # JavaFX GUI
│   └── test/
│       └── java/
│           └── com/
│               └── nqueens/
│                   └── solver/
│                       └── NQueensSolverTest.java # Unit tests
└── README.md                         # This file
```

## Testing

Run the test suite:
```bash
mvn test
```

Expected test results:
- 4-Queens: 2 solutions
- 8-Queens: 92 solutions
- Solution validation: All solutions correct
- Threading modes: Both auto and all-threads working
- Stop functionality: Working correctly

## OS Concepts Demonstrated

This project demonstrates several important Operating Systems concepts:

1. **Multi-threading**: Multiple threads working on independent sub-problems
2. **Thread Synchronization**: Using thread-safe data structures
3. **Work Distribution**: Dividing work among threads based on system resources
4. **Parallel Computation**: Achieving speedup through concurrent execution
5. **Atomic Operations**: Using `AtomicInteger` and `AtomicBoolean` for thread-safe counters

## License

This is an educational project for Operating Systems coursework.

## Author

Created for OS2 (Operating Systems 2) coursework.
