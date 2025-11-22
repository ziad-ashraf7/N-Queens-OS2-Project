package com.nqueens.gui;

import com.nqueens.solver.NQueensSolver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JavaFX GUI application for N-Queens solver with real-time visualization.
 */
public class NQueensApp extends Application {
    
    private static final int CANVAS_SIZE = 600;
    private static final Color LIGHT_SQUARE = Color.web("#F0D9B5");
    private static final Color DARK_SQUARE = Color.web("#B58863");
    private static final Color HIGHLIGHT_COLOR = Color.web("#FFE5B4");
    private static final Color QUEEN_COLOR = Color.web("#8B0000");
    
    private Canvas canvas;
    private TextField nTextField;
    private ComboBox<String> threadModeCombo;
    private ComboBox<String> speedCombo;
    private Button startButton;
    private Button stopButton;
    private Button prevButton;
    private Button nextButton;
    private Label statsLabel;
    private Label solutionLabel;
    
    private NQueensSolver solver;
    private Thread solverThread;
    private final AtomicBoolean solving = new AtomicBoolean(false);
    private List<int[]> solutions;
    private int currentSolutionIndex = 0;
    private int[] currentBoard;
    private int currentHighlightRow = -1;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("N-Queens Solver - Multi-threaded");
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Top: Controls
        VBox topBox = new VBox(10);
        topBox.getChildren().addAll(createControlsPanel(), createStatsPanel());
        root.setTop(topBox);
        
        // Center: Canvas
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        
        Label canvasLabel = new Label("Board Visualization");
        canvasLabel.setFont(Font.font(14));
        canvasLabel.setStyle("-fx-font-weight: bold;");
        
        canvas = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        centerBox.getChildren().addAll(canvasLabel, canvas);
        root.setCenter(centerBox);
        
        // Bottom: Navigation
        root.setBottom(createNavigationPanel());
        
        // Create scene
        Scene scene = new Scene(root, 900, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Draw empty board initially
        drawBoard(new int[8], -1);
    }
    
    private TitledPane createControlsPanel() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        // Board size
        grid.add(new Label("Board Size (N):"), 0, 0);
        nTextField = new TextField("8");
        nTextField.setPrefWidth(80);
        grid.add(nTextField, 1, 0);
        
        // Thread mode
        grid.add(new Label("Thread Mode:"), 2, 0);
        threadModeCombo = new ComboBox<>();
        threadModeCombo.getItems().addAll("auto", "all");
        threadModeCombo.setValue("auto");
        threadModeCombo.setPrefWidth(100);
        grid.add(threadModeCombo, 3, 0);
        
        // Buttons
        startButton = new Button("Start");
        startButton.setOnAction(e -> startSolving());
        grid.add(startButton, 4, 0);
        
        stopButton = new Button("Stop");
        stopButton.setDisable(true);
        stopButton.setOnAction(e -> stopSolving());
        grid.add(stopButton, 5, 0);
        
        // Visualization speed
        grid.add(new Label("Visualization Speed:"), 0, 1);
        speedCombo = new ComboBox<>();
        speedCombo.getItems().addAll("instant", "fast", "medium", "slow");
        speedCombo.setValue("fast");
        speedCombo.setPrefWidth(100);
        grid.add(speedCombo, 1, 1);
        
        TitledPane pane = new TitledPane("Controls", grid);
        pane.setCollapsible(false);
        return pane;
    }
    
    private TitledPane createStatsPanel() {
        HBox box = new HBox();
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER_LEFT);
        
        statsLabel = new Label("Ready to solve...");
        statsLabel.setFont(Font.font(12));
        box.getChildren().add(statsLabel);
        
        TitledPane pane = new TitledPane("Statistics", box);
        pane.setCollapsible(false);
        return pane;
    }
    
    private HBox createNavigationPanel() {
        HBox box = new HBox(20);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        
        prevButton = new Button("<< Previous");
        prevButton.setDisable(true);
        prevButton.setOnAction(e -> showPreviousSolution());
        
        solutionLabel = new Label("No solutions yet");
        solutionLabel.setFont(Font.font(12));
        
        nextButton = new Button("Next >>");
        nextButton.setDisable(true);
        nextButton.setOnAction(e -> showNextSolution());
        
        box.getChildren().addAll(prevButton, solutionLabel, nextButton);
        return box;
    }
    
    private void startSolving() {
        try {
            int n = Integer.parseInt(nTextField.getText());
            if (n < 4) {
                showAlert("Invalid Input", "N must be at least 4");
                return;
            }
            if (n > 20) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "N=" + n + " may take a long time to solve. Continue?",
                        ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() != ButtonType.YES) {
                    return;
                }
            }
            
            solving.set(true);
            solutions = null;
            currentSolutionIndex = 0;
            
            // Update UI state
            startButton.setDisable(true);
            stopButton.setDisable(false);
            prevButton.setDisable(true);
            nextButton.setDisable(true);
            solutionLabel.setText("Solving...");
            
            // Create solver with callback
            boolean useAllThreads = threadModeCombo.getValue().equals("all");
            solver = new NQueensSolver(n, this::visualizationCallback, useAllThreads);
            
            // Start solving in a separate thread
            solverThread = new Thread(() -> {
                long startTime = System.currentTimeMillis();
                List<int[]> sols = solver.solve();
                long endTime = System.currentTimeMillis();
                
                Platform.runLater(() -> solvingComplete(sols, endTime - startTime));
            });
            solverThread.setDaemon(true);
            solverThread.start();
            
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for N");
        }
    }
    
    private void stopSolving() {
        if (solver != null) {
            solver.stop();
        }
        solving.set(false);
        resetUI();
        showAlert("Stopped", "Solving process stopped");
    }
    
    private void visualizationCallback(int[] board, int row) {
        if (!solving.get()) {
            return;
        }
        
        int delay = getVisualizationDelay();
        if (delay > 0) {
            currentBoard = board.clone();
            currentHighlightRow = row;
            Platform.runLater(() -> drawBoard(currentBoard, currentHighlightRow));
            
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private int getVisualizationDelay() {
        String speed = speedCombo.getValue();
        switch (speed) {
            case "slow": return 100;
            case "medium": return 50;
            case "fast": return 10;
            case "instant":
            default: return 0;
        }
    }
    
    private void solvingComplete(List<int[]> sols, long elapsedTime) {
        solving.set(false);
        solutions = sols;
        
        NQueensSolver.SolverStats stats = solver.getStats();
        String statsMsg = String.format("Solutions: %d | States: %,d | Threads: %d | Time: %.2fs",
                stats.solutionsFound, stats.statesExplored, stats.threadsUsed, elapsedTime / 1000.0);
        statsLabel.setText(statsMsg);
        
        startButton.setDisable(false);
        stopButton.setDisable(true);
        
        if (solutions != null && !solutions.isEmpty()) {
            currentSolutionIndex = 0;
            displayCurrentSolution();
            
            if (solutions.size() > 1) {
                nextButton.setDisable(false);
            }
        } else {
            solutionLabel.setText("No solutions found");
            showAlert("Complete", "No solutions found!");
        }
    }
    
    private void resetUI() {
        startButton.setDisable(false);
        stopButton.setDisable(true);
        prevButton.setDisable(true);
        nextButton.setDisable(true);
    }
    
    private void displayCurrentSolution() {
        if (solutions == null || solutions.isEmpty()) {
            return;
        }
        
        int[] solution = solutions.get(currentSolutionIndex);
        drawBoard(solution, -1);
        solutionLabel.setText(String.format("Solution %d of %d", 
                currentSolutionIndex + 1, solutions.size()));
        
        prevButton.setDisable(currentSolutionIndex == 0);
        nextButton.setDisable(currentSolutionIndex >= solutions.size() - 1);
    }
    
    private void showPreviousSolution() {
        if (currentSolutionIndex > 0) {
            currentSolutionIndex--;
            displayCurrentSolution();
        }
    }
    
    private void showNextSolution() {
        if (currentSolutionIndex < solutions.size() - 1) {
            currentSolutionIndex++;
            displayCurrentSolution();
        }
    }
    
    private void drawBoard(int[] board, int highlightRow) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
        
        if (board == null || board.length == 0) {
            return;
        }
        
        int n = board.length;
        double cellSize = (CANVAS_SIZE * 0.9) / n;
        double offsetX = (CANVAS_SIZE - cellSize * n) / 2;
        double offsetY = (CANVAS_SIZE - cellSize * n) / 2;
        
        // Draw board squares
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double x = offsetX + j * cellSize;
                double y = offsetY + i * cellSize;
                
                // Determine square color
                Color color;
                if (i == highlightRow) {
                    color = HIGHLIGHT_COLOR;
                } else {
                    color = (i + j) % 2 == 0 ? LIGHT_SQUARE : DARK_SQUARE;
                }
                
                gc.setFill(color);
                gc.fillRect(x, y, cellSize, cellSize);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(x, y, cellSize, cellSize);
            }
        }
        
        // Draw queens
        gc.setFill(QUEEN_COLOR);
        gc.setFont(Font.font(Math.max(12, cellSize * 0.6)));
        for (int i = 0; i < n; i++) {
            if (board[i] >= 0) {
                int j = board[i];
                double x = offsetX + j * cellSize + cellSize / 2;
                double y = offsetY + i * cellSize + cellSize / 2;
                
                // Draw queen symbol
                gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
                gc.setTextBaseline(javafx.geometry.VPos.CENTER);
                gc.fillText("♕", x, y);
            }
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
