"""
N-Queens GUI Application

This module provides a graphical user interface for the N-Queens solver.
It displays real-time board exploration and solutions.
"""

import tkinter as tk
from tkinter import ttk, messagebox
import threading
import time
from nqueens_solver import NQueensSolver
from typing import List


class NQueensGUI:
    """GUI application for N-Queens solver with real-time visualization."""
    
    def __init__(self, root):
        """
        Initialize the GUI.
        
        Args:
            root: Tkinter root window
        """
        self.root = root
        self.root.title("N-Queens Solver - Multi-threaded")
        self.root.geometry("900x700")
        
        self.solver = None
        self.solving = False
        self.current_board = None
        self.solutions = []
        self.current_solution_index = 0
        
        self.setup_ui()
        
    def setup_ui(self):
        """Set up the user interface components."""
        # Control Panel
        control_frame = ttk.LabelFrame(self.root, text="Controls", padding=10)
        control_frame.pack(side=tk.TOP, fill=tk.X, padx=10, pady=5)
        
        # N input
        ttk.Label(control_frame, text="Board Size (N):").grid(row=0, column=0, padx=5, pady=5)
        self.n_var = tk.StringVar(value="8")
        n_entry = ttk.Entry(control_frame, textvariable=self.n_var, width=10)
        n_entry.grid(row=0, column=1, padx=5, pady=5)
        
        # Thread mode selection
        ttk.Label(control_frame, text="Thread Mode:").grid(row=0, column=2, padx=5, pady=5)
        self.thread_mode_var = tk.StringVar(value="auto")
        thread_combo = ttk.Combobox(control_frame, textvariable=self.thread_mode_var, 
                                    values=["auto", "all"], width=10, state="readonly")
        thread_combo.grid(row=0, column=3, padx=5, pady=5)
        
        # Buttons
        self.start_btn = ttk.Button(control_frame, text="Start", command=self.start_solving)
        self.start_btn.grid(row=0, column=4, padx=5, pady=5)
        
        self.stop_btn = ttk.Button(control_frame, text="Stop", command=self.stop_solving, state=tk.DISABLED)
        self.stop_btn.grid(row=0, column=5, padx=5, pady=5)
        
        # Visualization speed control
        ttk.Label(control_frame, text="Visualization Speed:").grid(row=1, column=0, padx=5, pady=5)
        self.speed_var = tk.StringVar(value="fast")
        speed_combo = ttk.Combobox(control_frame, textvariable=self.speed_var,
                                   values=["slow", "medium", "fast", "instant"], 
                                   width=10, state="readonly")
        speed_combo.grid(row=1, column=1, padx=5, pady=5)
        
        # Statistics Panel
        stats_frame = ttk.LabelFrame(self.root, text="Statistics", padding=10)
        stats_frame.pack(side=tk.TOP, fill=tk.X, padx=10, pady=5)
        
        self.stats_text = tk.StringVar(value="Ready to solve...")
        stats_label = ttk.Label(stats_frame, textvariable=self.stats_text)
        stats_label.pack()
        
        # Board Canvas Frame
        canvas_frame = ttk.LabelFrame(self.root, text="Board Visualization", padding=10)
        canvas_frame.pack(side=tk.TOP, fill=tk.BOTH, expand=True, padx=10, pady=5)
        
        # Canvas for drawing the board
        self.canvas = tk.Canvas(canvas_frame, bg="white")
        self.canvas.pack(fill=tk.BOTH, expand=True)
        
        # Solution navigation
        nav_frame = ttk.Frame(self.root)
        nav_frame.pack(side=tk.TOP, fill=tk.X, padx=10, pady=5)
        
        self.prev_btn = ttk.Button(nav_frame, text="<< Previous", command=self.prev_solution, state=tk.DISABLED)
        self.prev_btn.pack(side=tk.LEFT, padx=5)
        
        self.solution_label = tk.StringVar(value="No solutions yet")
        ttk.Label(nav_frame, textvariable=self.solution_label).pack(side=tk.LEFT, padx=20)
        
        self.next_btn = ttk.Button(nav_frame, text="Next >>", command=self.next_solution, state=tk.DISABLED)
        self.next_btn.pack(side=tk.LEFT, padx=5)
        
    def get_visualization_delay(self) -> float:
        """Get delay between visualization updates based on speed setting."""
        speed = self.speed_var.get()
        delays = {
            "slow": 0.1,
            "medium": 0.05,
            "fast": 0.01,
            "instant": 0.0
        }
        return delays.get(speed, 0.01)
    
    def draw_board(self, board: List[int], highlight_row: int = -1):
        """
        Draw the chess board with queens.
        
        Args:
            board: Board state (board[i] = j means queen at row i, col j)
            highlight_row: Row to highlight (currently being processed)
        """
        self.canvas.delete("all")
        
        if not board:
            return
        
        n = len(board)
        
        # Calculate cell size based on canvas size
        canvas_width = self.canvas.winfo_width()
        canvas_height = self.canvas.winfo_height()
        
        if canvas_width <= 1 or canvas_height <= 1:
            # Canvas not yet initialized, use defaults
            canvas_width = 500
            canvas_height = 500
        
        cell_size = min(canvas_width, canvas_height) // (n + 1)
        offset_x = (canvas_width - cell_size * n) // 2
        offset_y = (canvas_height - cell_size * n) // 2
        
        # Draw board squares
        for i in range(n):
            for j in range(n):
                x1 = offset_x + j * cell_size
                y1 = offset_y + i * cell_size
                x2 = x1 + cell_size
                y2 = y1 + cell_size
                
                # Alternate colors for chess board
                color = "#F0D9B5" if (i + j) % 2 == 0 else "#B58863"
                
                # Highlight current row being explored
                if i == highlight_row:
                    color = "#FFE5B4"
                
                self.canvas.create_rectangle(x1, y1, x2, y2, fill=color, outline="black")
        
        # Draw queens
        for i in range(n):
            if board[i] >= 0:  # Queen placed
                j = board[i]
                x = offset_x + j * cell_size + cell_size // 2
                y = offset_y + i * cell_size + cell_size // 2
                
                # Draw queen symbol - try unicode, fallback to 'Q'
                try:
                    queen_symbol = "♕"
                    self.canvas.create_text(x, y, text=queen_symbol, 
                                          font=("Arial", max(12, cell_size // 2)),
                                          fill="darkred")
                except:
                    # Fallback to simple 'Q' if unicode fails
                    self.canvas.create_text(x, y, text="Q", 
                                          font=("Arial", max(12, cell_size // 2), "bold"),
                                          fill="darkred")
    
    def visualization_callback(self, board: List[int], row: int):
        """
        Callback function for visualization during solving.
        
        Args:
            board: Current board state
            row: Current row being processed
        """
        if not self.solving:
            return
        
        delay = self.get_visualization_delay()
        if delay > 0:
            # Update visualization in GUI thread
            self.current_board = board[:]
            self.root.after(0, lambda: self.draw_board(board, row))
            time.sleep(delay)
    
    def start_solving(self):
        """Start solving the N-Queens problem."""
        try:
            n = int(self.n_var.get())
            if n < 4:
                messagebox.showerror("Invalid Input", "N must be at least 4")
                return
            if n > 20:
                response = messagebox.askyesno("Large N Warning", 
                    f"N={n} may take a long time to solve. Continue?")
                if not response:
                    return
        except ValueError:
            messagebox.showerror("Invalid Input", "Please enter a valid integer for N")
            return
        
        self.solving = True
        self.solutions = []
        self.current_solution_index = 0
        
        # Update UI state
        self.start_btn.config(state=tk.DISABLED)
        self.stop_btn.config(state=tk.NORMAL)
        self.prev_btn.config(state=tk.DISABLED)
        self.next_btn.config(state=tk.DISABLED)
        self.solution_label.set("Solving...")
        
        # Create solver
        use_all_threads = self.thread_mode_var.get() == "all"
        self.solver = NQueensSolver(n, callback=self.visualization_callback, 
                                    use_all_threads=use_all_threads)
        
        # Start solving in a separate thread
        solve_thread = threading.Thread(target=self.solve_thread)
        solve_thread.daemon = True
        solve_thread.start()
    
    def solve_thread(self):
        """Thread function that runs the solver."""
        try:
            start_time = time.time()
            solutions = self.solver.solve()
            end_time = time.time()
            
            self.solutions = solutions
            stats = self.solver.get_stats()
            
            # Update UI in main thread
            self.root.after(0, lambda: self.solving_complete(stats, end_time - start_time))
        except Exception as e:
            self.root.after(0, lambda: messagebox.showerror("Error", f"Solving error: {str(e)}"))
            self.root.after(0, self.reset_ui)
    
    def solving_complete(self, stats: dict, elapsed_time: float):
        """
        Called when solving is complete.
        
        Args:
            stats: Solving statistics
            elapsed_time: Time taken to solve
        """
        self.solving = False
        
        # Update statistics
        stats_msg = (f"Solutions: {stats['solutions_found']} | "
                    f"States Explored: {stats['states_explored']} | "
                    f"Threads: {stats['threads_used']} | "
                    f"Time: {elapsed_time:.2f}s")
        self.stats_text.set(stats_msg)
        
        # Update UI state
        self.start_btn.config(state=tk.NORMAL)
        self.stop_btn.config(state=tk.DISABLED)
        
        if self.solutions:
            self.current_solution_index = 0
            self.display_current_solution()
            
            if len(self.solutions) > 1:
                self.next_btn.config(state=tk.NORMAL)
        else:
            self.solution_label.set("No solutions found")
            messagebox.showinfo("Complete", "No solutions found!")
    
    def stop_solving(self):
        """Stop the solving process."""
        if self.solver:
            self.solver.stop()
        self.solving = False
        self.reset_ui()
        messagebox.showinfo("Stopped", "Solving process stopped")
    
    def reset_ui(self):
        """Reset UI to initial state."""
        self.start_btn.config(state=tk.NORMAL)
        self.stop_btn.config(state=tk.DISABLED)
        self.prev_btn.config(state=tk.DISABLED)
        self.next_btn.config(state=tk.DISABLED)
    
    def display_current_solution(self):
        """Display the current solution."""
        if not self.solutions:
            return
        
        solution = self.solutions[self.current_solution_index]
        self.draw_board(solution)
        self.solution_label.set(f"Solution {self.current_solution_index + 1} of {len(self.solutions)}")
        
        # Update navigation buttons
        self.prev_btn.config(state=tk.NORMAL if self.current_solution_index > 0 else tk.DISABLED)
        self.next_btn.config(state=tk.NORMAL if self.current_solution_index < len(self.solutions) - 1 else tk.DISABLED)
    
    def prev_solution(self):
        """Show previous solution."""
        if self.current_solution_index > 0:
            self.current_solution_index -= 1
            self.display_current_solution()
    
    def next_solution(self):
        """Show next solution."""
        if self.current_solution_index < len(self.solutions) - 1:
            self.current_solution_index += 1
            self.display_current_solution()


def main():
    """Main function to run the GUI application."""
    root = tk.Tk()
    app = NQueensGUI(root)
    root.mainloop()


if __name__ == '__main__':
    main()
