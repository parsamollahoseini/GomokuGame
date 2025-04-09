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

import java.util.ArrayList; // Used for storing best moves
import java.util.List;      // Interface for ArrayList
import java.util.Random;    // Used for potentially randomizing equally good moves

/**
 * Implements the AI opponent logic using the Minimax algorithm with Alpha-Beta Pruning.
 * It evaluates board states and explores possible future moves to determine the optimal move.
 */
public class MinimaxAI {

    // --- Constants for Evaluation ---
    // These scores guide the AI's decision-making. Larger magnitudes indicate higher importance.
    /** Score assigned when the AI achieves a winning state. */
    private static final int WIN_SCORE = 100000;
    /** Score assigned when the opponent achieves a winning state (AI loses). Negative value. */
    private static final int LOSE_SCORE = -100000;
    /** Score assigned for a draw state. */
    private static final int DRAW_SCORE = 0;

    // --- Heuristic Scores (Used when max depth is reached without a win/loss/draw) ---
    // *** THESE ARE EXAMPLE VALUES! Tuning these is critical for AI strength! ***
    // A proper heuristic would consider open ends, blocking moves, patterns, etc.
    /** Example score for having 4 pieces in a row (potential win). */
    private static final int FOUR_IN_ROW = 5000;
    /** Example score for having 3 pieces in a row. */
    private static final int THREE_IN_ROW = 100;
    /** Example score for having 2 pieces in a row. */
    private static final int TWO_IN_ROW = 10;


    // --- Instance Variables ---
    /** The maximum depth the Minimax algorithm will search down the game tree. Higher depth = stronger AI but slower computation. */
    private final int searchDepth;
    /** The character symbol representing the AI player (e.g., 'B' or 'W'). */
    private final char aiSymbol;
    /** The character symbol representing the Human opponent. */
    private final char humanSymbol;
    /** Optional: Used to randomly select between moves that have the same best score, making the AI less predictable. */
    private final Random random = new Random();

    // --- Constructor ---
    /**
     * Creates a new MinimaxAI instance.
     * @param searchDepth The maximum lookahead depth for the algorithm.
     * @param aiSymbol The symbol ('B' or 'W') used by this AI player.
     * @param humanSymbol The symbol ('B' or 'W') used by the opponent.
     */
    public MinimaxAI(int searchDepth, char aiSymbol, char humanSymbol) {
        this.searchDepth = searchDepth;
        this.aiSymbol = aiSymbol;
        this.humanSymbol = humanSymbol;
    }

    // --- Public method to find the best move ---
    /**
     * Calculates and returns the best move for the AI based on the current board state.
     * This is the main entry point for the AI's turn.
     * @param board The current state of the game board.
     * @return An integer array `[row, col]` representing the best move coordinates.
     */
    public int[] findBestMove(Board board) {
        System.out.println("AI (" + aiSymbol + ") is thinking (depth " + searchDepth + ")...");
        long startTime = System.currentTimeMillis(); // Start timing AI calculation

        int bestScore = Integer.MIN_VALUE; // Initialize best score to the lowest possible value
        List<int[]> bestMoves = new ArrayList<>(); // Store potentially multiple moves with the same highest score

        // Iterate through all cells on the board
        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                // Check if the current cell is empty (a potential move)
                if (board.isEmpty(r, c)) {
                    // --- Simulate making the move ---
                    board.placeSymbol(r, c, aiSymbol);

                    // --- Call Minimax to evaluate this move ---
                    // The AI just made a move (maximizing player), so the next turn is the opponent's (minimizing player - hence 'false').
                    // Alpha starts at MIN_VALUE, Beta starts at MAX_VALUE for the initial call.
                    int score = minimax(board, searchDepth - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);

                    // --- Undo the simulated move (backtrack) ---
                    board.removeSymbol(r, c);

                    // --- Update tracking of the best move(s) ---
                    if (score > bestScore) {
                        // Found a move with a score better than any found so far
                        bestScore = score;
                        bestMoves.clear(); // Discard previous list of best moves
                        bestMoves.add(new int[]{r, c}); // Add the new best move
                    } else if (score == bestScore) {
                        // Found a move with a score equal to the current best score
                        bestMoves.add(new int[]{r, c}); // Add it to the list of equally good moves
                    }
                } // End if cell is empty
            } // End inner loop (columns)
        } // End outer loop (rows)

        long endTime = System.currentTimeMillis(); // Stop timing
        System.out.println("AI decision time: " + (endTime - startTime) + " ms. Best score evaluated: " + bestScore);

        // --- Select the final move ---
        if (bestMoves.isEmpty()) {
            // This should ideally not happen if there are empty spots on the board.
            // Provides a fallback just in case minimax somehow fails to find any move.
            System.err.println("Warning: AI could not find a valid scored move. Picking first available.");
            return findFirstAvailableMove(board); // Basic fallback
        }
        // If multiple moves have the same best score, pick one randomly.
        // This makes the AI less predictable.
        int randomIndex = random.nextInt(bestMoves.size());
        return bestMoves.get(randomIndex);
    }


    // --- Minimax algorithm with Alpha-Beta Pruning ---
    /**
     * The recursive core of the Minimax algorithm with Alpha-Beta pruning.
     * Explores the game tree to a specified depth to find the best score achievable from the current state.
     *
     * @param currentBoard The board state being evaluated (potentially after simulated moves).
     * @param depth The remaining depth to search.
     * @param isMaximizingPlayer True if the current turn is for the AI (maximizing score), False if for the opponent (minimizing score).
     * @param alpha The best score found so far for the maximizing player along the current path. Used for pruning.
     * @param beta The best score found so far for the minimizing player along the current path. Used for pruning.
     * @return The evaluated score for the board state at the end of the search from this node.
     */
    private int minimax(Board currentBoard, int depth, boolean isMaximizingPlayer, int alpha, int beta) {

        // --- Base Cases: Check for Terminal States or Max Depth ---

        // Evaluate the current board for immediate win/loss/draw first.
        int boardScore = evaluateBoardState(currentBoard);
        if (boardScore == WIN_SCORE || boardScore == LOSE_SCORE || currentBoard.isFull()) {
            // If it's a win, loss, or draw, return the corresponding terminal score immediately.
            return boardScore;
        }
        if (depth == 0) {
            // If we've reached the maximum search depth without a terminal state,
            // use the heuristic evaluation function to estimate the board's value.
            return evaluateHeuristic(currentBoard);
        }

        // --- Recursive Exploration ---

        if (isMaximizingPlayer) { // AI's Turn (Maximize Score)
            int maxEval = Integer.MIN_VALUE; // Initialize best score for maximizer
            // Explore all possible moves for the AI
            for (int r = 0; r < currentBoard.getSize(); r++) {
                for (int c = 0; c < currentBoard.getSize(); c++) {
                    if (currentBoard.isEmpty(r, c)) {
                        currentBoard.placeSymbol(r, c, aiSymbol); // Make the move
                        // Recursively call minimax for the opponent's turn (minimizing)
                        int eval = minimax(currentBoard, depth - 1, false, alpha, beta);
                        currentBoard.removeSymbol(r, c); // Undo the move (backtrack)

                        maxEval = Math.max(maxEval, eval); // Update the maximum score found
                        alpha = Math.max(alpha, eval);    // Update alpha (best score for maximizer on this path)

                        // Alpha-Beta Pruning: If beta <= alpha, the minimizing player (opponent)
                        // already has a better option earlier in the tree, so we can prune this branch.
                        if (beta <= alpha) {
                            return maxEval; // Prune
                        }
                    }
                } // End inner loop (columns)
                // Optimization: Check pruning condition also after finishing a row
                // if (beta <= alpha) { break; } // Not strictly necessary due to inner check, but can be added
            } // End outer loop (rows)
            return maxEval; // Return the best score found for the maximizing player

        } else { // Opponent's Turn (Minimize Score)
            int minEval = Integer.MAX_VALUE; // Initialize best score for minimizer
            // Explore all possible moves for the opponent
            for (int r = 0; r < currentBoard.getSize(); r++) {
                for (int c = 0; c < currentBoard.getSize(); c++) {
                    if (currentBoard.isEmpty(r, c)) {
                        currentBoard.placeSymbol(r, c, humanSymbol); // Make the move
                        // Recursively call minimax for the AI's turn (maximizing)
                        int eval = minimax(currentBoard, depth - 1, true, alpha, beta);
                        currentBoard.removeSymbol(r, c); // Undo the move (backtrack)

                        minEval = Math.min(minEval, eval); // Update the minimum score found
                        beta = Math.min(beta, eval);     // Update beta (best score for minimizer on this path)

                        // Alpha-Beta Pruning: If beta <= alpha, the maximizing player (AI)
                        // already has a better option earlier in the tree, so we can prune this branch.
                        if (beta <= alpha) {
                            return minEval; // Prune
                        }
                    }
                } // End inner loop (columns)
                // Optimization: Check pruning condition also after finishing a row
                // if (beta <= alpha) { break; } // Not strictly necessary due to inner check, but can be added
            } // End outer loop (rows)
            return minEval; // Return the best score found for the minimizing player (worst for AI)
        }
    } // End of minimax method

    // --- Evaluation Functions ---

    /**
     * Evaluates the current board state for immediate win, loss, or draw conditions.
     * Used as a primary check within the Minimax base cases.
     * @param board The board state to evaluate.
     * @return WIN_SCORE if AI has won, LOSE_SCORE if Human has won, DRAW_SCORE if it's a draw,
     * or 0 if the game is not in a terminal state yet.
     */
    private int evaluateBoardState(Board board) {
        // Check if AI has a winning line on the current board
        if (checkWinOverall(board, aiSymbol)) return WIN_SCORE;
        // Check if Human has a winning line on the current board
        if (checkWinOverall(board, humanSymbol)) return LOSE_SCORE;
        // Check if the board is full (draw)
        if (board.isFull()) return DRAW_SCORE;
        // If none of the above, the game is not over yet based on this board state.
        // Return 0 to indicate it's not a terminal win/loss/draw. The heuristic will be used if depth is 0.
        return 0;
    }

    /**
     * Helper method to check if a given player has won on the ENTIRE board.
     * This is needed for evaluating hypothetical board states within Minimax,
     * where we don't know the 'last move' that led to this state.
     * It iterates through all cells containing the player's symbol and uses the
     * Board's checkWin method starting from that cell.
     *
     * @param board The board state to check.
     * @param symbol The player's symbol to check for a win.
     * @return true if the specified player has a winning line, false otherwise.
     */
    private boolean checkWinOverall(Board board, char symbol) {
        // Iterate through all cells
        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                // If the cell contains the player's symbol...
                if (board.getSymbol(r, c) == symbol) {
                    // ...then check if a winning line originates from this cell.
                    // Reuse the efficient checkWin logic from the Board class.
                    if (board.checkWin(r, c, symbol)) {
                        return true; // Found a win for this player
                    }
                }
            }
        }
        // If no winning line found after checking all relevant cells
        return false;
    }

    /**
     * Heuristic evaluation function for non-terminal states (used when Minimax reaches its depth limit).
     * Estimates the "goodness" of the current board position for the AI.
     *
     * *** CRITICAL NOTE: This is a VERY BASIC placeholder heuristic! ***
     * A strong Gomoku AI requires a much more sophisticated heuristic function that considers:
     * - Open-ended lines (e.g., `_XXX_` is more valuable than `OXXX_`)
     * - Threats (forcing moves for the opponent)
     * - Blocking opponent's threats
     * - Captured patterns (if rules included captures)
     * - Center control or strategic positions
     *
     * The current implementation provides a minimal starting point.
     *
     * @param board The board state to evaluate heuristically.
     * @return An integer score representing the estimated advantage for the AI (positive) or disadvantage (negative).
     */
    private int evaluateHeuristic(Board board) {
        int aiScore = 0;      // Accumulate score based on AI's potential lines
        int humanScore = 0;   // Accumulate score based on Human's potential lines

        // --- Example: Simple scoring based on counts of 2, 3, 4 in a row ---
        // This calls a helper function to evaluate lines for each player.
        aiScore += scoreLines(board, aiSymbol);
        humanScore += scoreLines(board, humanSymbol);

        // The final heuristic score is the difference, representing the AI's relative advantage.
        return aiScore - humanScore;
    }

    /**
     * Helper function for the *basic* heuristic evaluation.
     * Scans the board and assigns scores based on finding streaks of a given player's symbol.
     *
     * *** NOTE: This is overly simplistic. It does NOT check if lines are blocked ("open ends"). ***
     * For example, it scores `OXXX.` the same as `.XXX.` or `.XXXO`. A real heuristic needs to differentiate.
     *
     * @param board The board state.
     * @param symbol The player's symbol to score lines for.
     * @return An accumulated score based on detected streaks.
     */
    private int scoreLines(Board board, char symbol) {
        int score = 0;
        int size = board.getSize();

        // Define directions to check (horizontal, vertical, diagonal\, diagonal/)
        int[] dr = {0, 1, 1, 1};
        int[] dc = {1, 0, 1, -1};

        // Iterate through each cell as a potential starting point of a line
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                // Only start checking from cells occupied by the current player's symbol
                if (board.getSymbol(r, c) == symbol) {
                    // Check in all 4 directions starting from this cell
                    for(int i=0; i<4; ++i) {
                        int streak = 1; // Start with the symbol at (r, c)
                        // Count consecutive symbols in this direction (up to 4 more steps, for a total of 5)
                        for(int k=1; k < 5; ++k){
                            int nr = r + dr[i]*k; // Next row in this direction
                            int nc = c + dc[i]*k; // Next column in this direction
                            // Check bounds and if the next cell contains the same symbol
                            if(board.isWithinBounds(nr, nc) && board.getSymbol(nr, nc) == symbol){
                                streak++;
                            } else {
                                break; // Stop counting in this direction if boundary or different symbol
                            }
                        }
                        // Add score based on the length of the streak found
                        // (Using predefined constants - these would need tuning)
                        if (streak == 4) score += FOUR_IN_ROW; // Add score for a 4-in-a-row
                        else if (streak == 3) score += THREE_IN_ROW; // Add score for a 3-in-a-row
                        else if (streak == 2) score += TWO_IN_ROW; // Add score for a 2-in-a-row
                    } // End direction check loop
                } // End if cell contains symbol
            } // End column loop
        } // End row loop
        return score; // Return the total heuristic score for this player based on line counts
    } // End scoreLines method


    /**
     * Fallback method used if the primary `findBestMove` logic somehow fails
     * to identify any valid move (e.g., if all evaluated scores were Integer.MIN_VALUE).
     * Simply finds the first available empty slot on the board.
     * @param board The current board state.
     * @return Coordinates `[row, col]` of the first found empty slot, or `[-1, -1]` if board is full.
     */
    private int[] findFirstAvailableMove(Board board){
        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                if (board.isEmpty(r,c)) {
                    // Return the coordinates of the first empty slot found
                    return new int[]{r, c};
                }
            }
        }
        // Should not be reached if called correctly (i.e., only when board isn't full)
        return new int[]{-1,-1};
    }

} 