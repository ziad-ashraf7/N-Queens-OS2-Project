package com.example.os2;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for N-Queens Problem GUI
 */
public class NQueenController {
    @FXML
    private TextField boardSizeField;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Label statusLabel;
    @FXML
    private Label solutionsCountLabel;
    @FXML
    private ScrollPane threadsScrollPane;
    @FXML
    private FlowPane threadsContainer;
    @FXML
    private TextArea solutionsArea;

    private List<NQueenSolver> solvers;
    private List<GridPane> threadBoards;
    private List<Label> threadLabels;
    private List<Label> threadStatusLabels;
    private int totalSolutions = 0;
    private int currentBoardSize = 0;
    private final AtomicBoolean firstSolutionFound = new AtomicBoolean(false);
    private int firstWinnerThreadId = -1;

    @FXML
    public void initialize() {
        solvers = new ArrayList<>();
        threadBoards = new ArrayList<>();
        threadLabels = new ArrayList<>();
        threadStatusLabels = new ArrayList<>();
        stopButton.setDisable(true);

        // Set default board size
        boardSizeField.setText("8");
    }

    @FXML
    protected void onStartClick() {
        try {
            int n = Integer.parseInt(boardSizeField.getText());
            if (n < 4 || n > 12) {
                showAlert("Input Error", "Please enter a board size between 4 and 12");
                return;
            }

            startSolving(n);
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid number");
        }
    }

    @FXML
    protected void onStopClick() {
        stopSolving();
    }

    /**
     * Check if this is the first solution found
     */
    public boolean isFirstSolution() {
        return firstSolutionFound.compareAndSet(false, true);
    }

    /**
     * Start solving the N-Queens problem with multiple threads
     */
    private void startSolving(int n) {
        // Disable/enable buttons
        startButton.setDisable(true);
        stopButton.setDisable(false);
        boardSizeField.setDisable(true);

        // Clear previous data
        solutionsArea.clear();
        totalSolutions = 0;
        currentBoardSize = n;
        firstSolutionFound.set(false);
        firstWinnerThreadId = -1;
        threadsContainer.getChildren().clear();
        solvers.clear();
        threadBoards.clear();
        threadLabels.clear();
        threadStatusLabels.clear();

        // Create one thread per starting column (up to n threads)
        int numThreads = n;

        statusLabel.setText("Running " + numThreads + " threads...");
        solutionsCountLabel.setText("Solutions found: 0");

        // Create and start threads - one thread per starting column
        for (int col = 0; col < n; col++) {
            // Create UI container for this thread
            VBox threadBox = createThreadBox(col, n);
            threadsContainer.getChildren().add(threadBox);

            // Create one solver per thread, each starting at a different column
            NQueenSolver solver = new NQueenSolver(col, n, this, col);
            solvers.add(solver);
            solver.start();
        }

        solutionsArea.appendText("Started " + numThreads + " threads for N=" + n + "\n");
        solutionsArea.appendText("Each thread explores starting column 0 to " + (n-1) + "\n\n");
    }

    /**
     * Create UI components for a thread
     */
    private VBox createThreadBox(int threadId, int boardSize) {
        VBox threadBox = new VBox(8);
        threadBox.setPadding(new Insets(10));
        threadBox.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2; -fx-background-color: #f5f5f5; -fx-border-radius: 5; -fx-background-radius: 5;");
        threadBox.setAlignment(Pos.TOP_CENTER);
        threadBox.setPrefWidth(Math.min(350, 60 * boardSize + 50));

        // Thread title
        Label threadLabel = new Label("Thread " + threadId);
        threadLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        threadLabel.setTextFill(Color.web("#1976D2"));
        threadLabels.add(threadLabel);

        // Status label
        Label statusLabel = new Label("Initializing...");
        statusLabel.setFont(Font.font("System", 11));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(Math.min(330, 60 * boardSize + 30));
        threadStatusLabels.add(statusLabel);

        // Board grid
        GridPane boardGrid = createBoardGrid(boardSize);
        threadBoards.add(boardGrid);

        threadBox.getChildren().addAll(threadLabel, statusLabel, boardGrid);

        return threadBox;
    }

    /**
     * Create a visual chess board grid
     */
    private GridPane createBoardGrid(int size) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        // Calculate cell size based on board size
        double cellSize = Math.max(25, Math.min(50, 400.0 / size));

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(cellSize, cellSize);
                cell.setMinSize(cellSize, cellSize);
                cell.setMaxSize(cellSize, cellSize);

                // Checkerboard pattern
                if ((row + col) % 2 == 0) {
                    cell.setStyle("-fx-background-color: #f0d9b5; -fx-border-color: #b58863; -fx-border-width: 0.5;");
                } else {
                    cell.setStyle("-fx-background-color: #b58863; -fx-border-color: #f0d9b5; -fx-border-width: 0.5;");
                }

                grid.add(cell, col, row);
            }
        }

        return grid;
    }

    /**
     * Update board visualization for a specific thread
     */
    public synchronized void updateBoard(int threadId, Board board, String status) {
        if (threadId >= threadBoards.size()) {
            return;
        }

        GridPane grid = threadBoards.get(threadId);
        Label statusLabel = threadStatusLabels.get(threadId);

        // Update status
        statusLabel.setText(status);

        // Calculate cell size
        double cellSize = Math.max(25, Math.min(50, 400.0 / board.getSize()));
        double queenRadius = cellSize * 0.35;

        // Update board cells
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                int cellIndex = row * board.getSize() + col;
                StackPane cell = (StackPane) grid.getChildren().get(cellIndex);
                cell.getChildren().clear();

                // Place queen if present
                if (board.getQueenPosition(row) == col) {
                    Circle queen = new Circle(queenRadius);
                    queen.setFill(Color.web("#D32F2F"));
                    queen.setStroke(Color.web("#B71C1C"));
                    queen.setStrokeWidth(2);

                    Label qLabel = new Label("♛");
                    qLabel.setFont(Font.font("System", FontWeight.BOLD, cellSize * 0.6));
                    qLabel.setTextFill(Color.WHITE);

                    cell.getChildren().addAll(queen, qLabel);
                }
            }
        }
    }

    /**
     * Called when a solution is found
     */
    public synchronized void solutionFound(int threadId, Board solution) {
        totalSolutions++;
        solutionsCountLabel.setText("Solutions found: " + totalSolutions);

        // Check if this is the first solution
        boolean isFirst = isFirstSolution();

        if (isFirst) {
            firstWinnerThreadId = threadId;
            solutionsArea.appendText("🏆🏆🏆 FIRST SOLUTION - THREAD " + threadId + " WINS! 🏆🏆🏆\n");
            solutionsArea.appendText(solution.toString());
            solutionsArea.appendText("---\n");

            // Mark this thread as the winner
            if (threadId < threadStatusLabels.size()) {
                threadStatusLabels.get(threadId).setText("🏆 WINNER - First solution found! (FROZEN)");
                threadStatusLabels.get(threadId).setTextFill(Color.web("#1B5E20"));
                threadStatusLabels.get(threadId).setFont(Font.font("System", FontWeight.BOLD, 12));
            }

            statusLabel.setText("🏆 First solution found by Thread " + threadId + "! Other threads continuing...");
            statusLabel.setTextFill(Color.web("#2E7D32"));
        } else {
            // Subsequent solutions
            solutionsArea.appendText("✓ Solution #" + totalSolutions + " found by Thread " + threadId + "\n");
            solutionsArea.appendText(solution.toString());
            solutionsArea.appendText("---\n");

            if (threadId < threadStatusLabels.size()) {
                threadStatusLabels.get(threadId).setText("✓ Found solution #" + totalSolutions + " (FROZEN)");
                threadStatusLabels.get(threadId).setTextFill(Color.web("#2E7D32"));
                threadStatusLabels.get(threadId).setFont(Font.font("System", FontWeight.BOLD, 11));
            }
        }

        // Auto-scroll to bottom
        solutionsArea.setScrollTop(Double.MAX_VALUE);
    }

    /**
     * Called when a thread completes its work
     */
    public synchronized void threadCompleted(int threadId, int solutionsFound, int stepsExplored) {
        if (threadId < threadStatusLabels.size()) {
            // Only update status if the thread didn't find a solution
            if (solutionsFound == 0) {
                threadStatusLabels.get(threadId).setText(
                        "✓ Exhausted all paths - No solution exists from column " + threadId + " (Steps: " + stepsExplored + ")"
                );
                threadStatusLabels.get(threadId).setTextFill(Color.web("#FF6F00"));

                // Add to solutions area
                solutionsArea.appendText("Thread " + threadId + ": Completed search from starting column " + threadId +
                    " - No valid solution path exists from this position (explored " + stepsExplored + " steps)\n");
            }
            // If thread found a solution, its status was already set in solutionFound()
        }

        // Check if all threads are done
        boolean allCompleted = solvers.stream().noneMatch(Thread::isAlive);
        if (allCompleted) {
            int totalSteps = solvers.stream().mapToInt(NQueenSolver::getStepsExplored).sum();
            statusLabel.setText("✓ All threads completed! Total solutions: " + totalSolutions + ", Total steps: " + totalSteps);
            statusLabel.setTextFill(Color.web("#2E7D32"));
            startButton.setDisable(false);
            stopButton.setDisable(true);
            boardSizeField.setDisable(false);

            solutionsArea.appendText("\n=== SUMMARY ===\n");
            solutionsArea.appendText("Board size: " + currentBoardSize + "x" + currentBoardSize + "\n");
            solutionsArea.appendText("Total solutions found: " + totalSolutions + "\n");
            solutionsArea.appendText("Total steps explored: " + totalSteps + "\n");
            solutionsArea.appendText("Threads used: " + threadBoards.size() + "\n");

            if (firstWinnerThreadId >= 0) {
                solutionsArea.appendText("First winner: Thread " + firstWinnerThreadId + "\n");
            }

            // Count threads that didn't find solutions
            int noSolutionCount = (int) solvers.stream()
                .filter(s -> s.getSolutions().isEmpty())
                .count();
            if (noSolutionCount > 0) {
                solutionsArea.appendText("\nThreads with no solution: " + noSolutionCount + "\n");
                solutionsArea.appendText("NOTE: This is expected! Not every starting column position\n");
                solutionsArea.appendText("      leads to a valid N-Queens solution. These threads\n");
                solutionsArea.appendText("      correctly explored all possibilities and determined\n");
                solutionsArea.appendText("      no solution exists from their starting positions.\n");

                // List which threads had no solutions
                solutionsArea.appendText("\nStarting columns with no solutions: ");
                List<Integer> noSolutionThreads = new ArrayList<>();
                for (int i = 0; i < solvers.size(); i++) {
                    if (solvers.get(i).getSolutions().isEmpty()) {
                        noSolutionThreads.add(i);
                    }
                }
                solutionsArea.appendText(noSolutionThreads.toString() + "\n");
            }
        }
    }

    /**
     * Stop all running solver threads
     */
    private void stopSolving() {
        for (NQueenSolver solver : solvers) {
            solver.stopSolver();
        }

        statusLabel.setText("Stopped by user");
        statusLabel.setTextFill(Color.web("#D32F2F"));
        startButton.setDisable(false);
        stopButton.setDisable(true);
        boardSizeField.setDisable(false);

        solutionsArea.appendText("\n[STOPPED BY USER]\n");
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

