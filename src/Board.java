/**
 * * COMP 2080 - Data Structures and Algorithms
 *  * Winter 2025 - Group Project
 *  * 
 *  * Team Members:
 *  * - Parsa Molahosseini (Student ID: 101491591)
 *  * - Mehrad Bayat (Student ID: 101533701)
 *  * - Jerry-lee Somera (Student ID: 101521229)
 *  * - Soroush Salari (Student ID: 101537771)
 */

import java.util.Arrays; // Used for Arrays.fill() and Arrays.copyOf()

/**
 * Represents the Gomoku game board.
 * Handles the state of the board (grid), placing symbols,
 * checking for win conditions, and displaying the board.
 * Adheres to the requirement of using a basic 2D array for the board state.
 */
public class Board {

    // --- Constants ---

    /** The dimension of the square board (e.g., 9 for a 9x9 grid). */
    public static final int BOARD_SIZE = 9;

    /** Character representing an empty slot on the board. */
    public static final char EMPTY_SLOT = '.';

    /** The number of consecutive symbols required to win. */
    private static final int WIN_STREAK = 5;

    // --- Instance Variable ---

    /**
     * The core data structure for the board state.
     * MUST be a basic array (char[][] or int[][]) as per project requirements.
     * 'final' means the 'grid' variable itself cannot be reassigned to a different array,
     * but the contents of the array can be modified.
     */
    private final char[][] grid;

    // --- Constructor ---

    /**
     * Creates a new Board object, initializing the grid
     * to the specified BOARD_SIZE and filling it with EMPTY_SLOT characters.
     */
    public Board() {
        // Instantiate the 2D char array
        grid = new char[BOARD_SIZE][BOARD_SIZE];
        // Fill the newly created grid with empty slots
        initialize();
    }

    // --- Initialization ---

    /**
     * Fills every cell of the grid with the EMPTY_SLOT character.
     * Ensures the board starts empty.
     */
    public void initialize() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            // Use Arrays.fill for efficient initialization of each row
            Arrays.fill(grid[i], EMPTY_SLOT);
        }
    }

    // --- Display ---

    /**
     * Prints the current state of the board to the console,
     * including row and column numbers for user convenience.
     */
    public void display() {
        // Print column headers
        System.out.print("  "); // Indent for row numbers
        for (int j = 0; j < BOARD_SIZE; j++) {
            System.out.print(j + " "); // Print column index
        }
        System.out.println(); // Newline after headers

        // Print rows with row numbers
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print(i + " "); // Print row index
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(grid[i][j] + " "); // Print cell content
            }
            System.out.println(); // Newline after each row
        }
        System.out.println("--------------------"); // Separator after board display
    }

    // --- Basic Operations & Checks ---

    /**
     * Checks if the given row and column coordinates are within the valid bounds of the board.
     * @param r Row index.
     * @param c Column index.
     * @return true if coordinates are within the board, false otherwise.
     */
    public boolean isWithinBounds(int r, int c) {
        return r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE;
    }

    /**
     * Checks if the cell at the given coordinates is empty (contains EMPTY_SLOT).
     * Also implicitly checks if the coordinates are within bounds.
     * @param r Row index.
     * @param c Column index.
     * @return true if the cell is within bounds and empty, false otherwise.
     */
    public boolean isEmpty(int r, int c) {
        // Use short-circuiting: isWithinBounds is checked first.
        return isWithinBounds(r, c) && grid[r][c] == EMPTY_SLOT;
    }

    /**
     * Places the given player symbol onto the board at the specified coordinates.
     * Checks for validity (within bounds, cell empty) before placing.
     * @param r Row index.
     * @param c Column index.
     * @param symbol The player's symbol ('B' or 'W').
     * @return true if the symbol was placed successfully, false if the move was invalid.
     */
    public boolean placeSymbol(int r, int c, char symbol) {
        // Check if the move is valid before modifying the grid
        if (isWithinBounds(r, c) && grid[r][c] == EMPTY_SLOT) {
            grid[r][c] = symbol;
            return true; // Placement successful
        }
        // Move was invalid (out of bounds or cell already occupied)
        return false;
    }

    /**
     * Removes a symbol from the board, setting the cell back to EMPTY_SLOT.
     * Primarily used by the AI's minimax algorithm for backtracking (undoing simulated moves).
     * @param r Row index.
     * @param c Column index.
     */
    public void removeSymbol(int r, int c) {
        // Only attempt to remove if within bounds
        if (isWithinBounds(r, c)) {
            grid[r][c] = EMPTY_SLOT;
        }
    }

    /**
     * Retrieves the symbol currently at the specified coordinates.
     * @param r Row index.
     * @param c Column index.
     * @return The character at grid[r][c] if coordinates are valid, otherwise returns the null character ('\0').
     */
    public char getSymbol(int r, int c) {
        if (isWithinBounds(r, c)) {
            return grid[r][c];
        }
        // Return null character to indicate an invalid location was requested
        return '\0';
    }

    /**
     * Checks if the board is completely full (no EMPTY_SLOT cells remain).
     * Used to determine a draw condition.
     * @return true if the board is full, false otherwise.
     */
    public boolean isFull() {
        // Iterate through every cell
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // If any empty slot is found, the board is not full
                if (grid[i][j] == EMPTY_SLOT) {
                    return false;
                }
            }
        }
        // If the loops complete without finding an empty slot, the board is full
        return true;
    }

    /**
     * Gets the size (dimension) of the board.
     * @return The value of BOARD_SIZE.
     */
    public int getSize() {
        return BOARD_SIZE;
    }

    // --- Win Condition Check ---

    /**
     * Checks if placing the given symbol at coordinates (r, c) resulted in a win
     * (WIN_STREAK consecutive symbols) along any horizontal, vertical, or diagonal line passing through (r, c).
     * Assumes the symbol was just placed at (r, c).
     * @param r Row index of the last placed symbol.
     * @param c Column index of the last placed symbol.
     * @param symbol The symbol that was just placed ('B' or 'W').
     * @return true if the move resulted in a win for the player, false otherwise.
     */
    public boolean checkWin(int r, int c, char symbol) {
        // Basic sanity check - should not be called on an empty or opponent's cell
        if (!isWithinBounds(r,c) || grid[r][c] != symbol) {
            return false;
        }

        // Define direction vectors for checking lines:
        // dr[i], dc[i] represents one direction along a line.
        // -dr[i], -dc[i] represents the opposite direction along the same line.
        // Indices correspond to: 0:Horizontal(+), 1:Vertical(+), 2:Diagonal(\,+), 3:Anti-Diagonal(/,+)
        int[] dr = {0, 1, 1,  1}; // Row changes
        int[] dc = {1, 0, 1, -1}; // Column changes

        // Iterate through the 4 primary line directions (Horizontal, Vertical, Diagonal, Anti-Diagonal)
        for (int i = 0; i < 4; i++) {
            // Initialize count for the current line check, including the piece just placed
            int count = 1;

            // --- Check in the "positive" direction (using dr[i], dc[i]) ---
            // Look up to WIN_STREAK-1 steps away along the line
            for (int j = 1; j < WIN_STREAK; j++) {
                int nr = r + dr[i] * j; // Calculate next row in this direction
                int nc = c + dc[i] * j; // Calculate next column in this direction

                // Check if the next cell is within bounds AND contains the same symbol
                if (isWithinBounds(nr, nc) && grid[nr][nc] == symbol) {
                    count++; // Increment count for this line
                } else {
                    break; // Stop counting in this direction if boundary or different symbol is hit
                }
            }

            // --- Check in the "negative" direction (using -dr[i], -dc[i]) ---
            // Look up to WIN_STREAK-1 steps away along the line
            for (int j = 1; j < WIN_STREAK; j++) {
                int nr = r - dr[i] * j; // Calculate next row in the opposite direction
                int nc = c - dc[i] * j; // Calculate next column in the opposite direction

                // Check if the next cell is within bounds AND contains the same symbol
                if (isWithinBounds(nr, nc) && grid[nr][nc] == symbol) {
                    count++; // Increment count for this line
                } else {
                    break; // Stop counting in this direction
                }
            }

            // --- Check if the total count for this line meets the win condition ---
            if (count >= WIN_STREAK) {
                return true; // Found a winning line of sufficient length
            }
        }

        // If loops complete without finding a winning line in any direction
        return false;
    }

    /**
     * Creates and returns a deep copy of the current board grid.
     * This is crucial for the AI, allowing it to simulate moves on a temporary copy
     * without affecting the actual game board state.
     * @return A new 2D char array containing a copy of the current grid state.
     */
    public char[][] getGridCopy() {
        // Create a new array with the same dimensions
        char[][] copy = new char[BOARD_SIZE][BOARD_SIZE];
        // Copy each row individually
        for(int i = 0; i < BOARD_SIZE; i++) {
            // Arrays.copyOf provides a safe way to copy primitive arrays (like char[])
            copy[i] = Arrays.copyOf(grid[i], BOARD_SIZE);
        }
        return copy;
    }
} 