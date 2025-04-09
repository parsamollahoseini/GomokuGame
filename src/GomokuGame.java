

import java.util.InputMismatchException; // Used for handling non-integer input
import java.util.Scanner;               // Used for getting user input from console

/**
 * The main class for the Gomoku (Five in a Row) game.
 * It orchestrates the game flow, handles user interaction (setup, moves),
 * manages player turns, and utilizes the Board and MinimaxAI classes.
 */
public class GomokuGame {

    // --- Constants ---
    /** Symbol for Player 1 (always Black, goes first). */
    private static final char PLAYER1_SYMBOL = 'B';
    /** Symbol for Player 2 (always White). */
    private static final char PLAYER2_SYMBOL = 'W';
    /** Default search depth for the Minimax AI. Higher values increase difficulty and computation time. */
    private static final int AI_DEPTH = 3; // Can be adjusted

    // --- Instance Variables ---
    /** The game board object, managing the grid state. */
    private final Board board;
    /** The AI opponent object. Initialized only if playing against AI. */
    private MinimaxAI ai;
    /** Scanner object for reading user input from the console. */
    private final Scanner scanner;
    /** The symbol ('B' or 'W') of the player whose turn it currently is. */
    private char currentPlayerSymbol;
    /** Flag indicating if the game is Human vs AI (true) or Human vs Human (false). */
    private boolean isVsAI;
    /** Name of Player 1 (could be human name or "AI"). */
    private String player1Name;
    /** Name of Player 2 (could be human name or "AI"). */
    private String player2Name;
    /** Stores the symbol chosen by the human player in Human vs AI mode. */
    private char humanPlayerSymbol = '\0'; // Using null char as uninitialized indicator
    /** Stores the symbol assigned to the AI player in Human vs AI mode. */
    private char aiPlayerSymbol = '\0';    // Using null char as uninitialized indicator

    // --- Constructor ---
    /**
     * Initializes a new Gomoku game instance.
     * Creates the game board and the scanner for user input.
     */
    public GomokuGame() {
        // Create the board object (which initializes the 9x9 grid)
        board = new Board();
        // Create the scanner object to read input from System.in (console)
        scanner = new Scanner(System.in);
    }

    // --- Main Game Orchestration ---
    /**
     * Runs the entire game sequence: Welcome message, mode selection,
     * player setup, game loop execution, and closing resources.
     */
    public void run() {
        System.out.println("Welcome to Gomoku (Five in a Row)!");
        // Ask user to select game mode (HvH or HvAI)
        selectGameMode();
        // Set up player names and symbols based on the selected mode
        setupPlayers();
        // Ensure the board is cleared before starting
        board.initialize();
        // Start the main loop where turns are taken
        gameLoop();
        // Close the scanner resource when the game is finished
        scanner.close();
        System.out.println("Thank you for playing!");
    }

    // --- Setup Methods ---

    /**
     * Prompts the user to select the game mode (1 for Human vs AI, 2 for Human vs Human).
     * Handles input validation.
     */
    private void selectGameMode() {
        System.out.println("Select Game Mode:");
        System.out.println("1. Human vs AI");
        System.out.println("2. Human vs Human");
        int choice = -1; // Initialize with an invalid choice
        // Loop until a valid choice (1 or 2) is entered
        while (choice != 1 && choice != 2) {
            System.out.print("Enter choice (1 or 2): ");
            try {
                // Attempt to read an integer from the input
                choice = scanner.nextInt();
                // Check if the entered integer is valid
                if (choice != 1 && choice != 2) {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } catch (InputMismatchException e) {
                // Handle cases where the user enters non-numeric input
                System.out.println("Invalid input. Please enter a number (1 or 2).");
                scanner.next(); // IMPORTANT: Consume the invalid token to prevent infinite loop
            }
        }
        // Set the isVsAI flag based on the valid choice
        isVsAI = (choice == 1);
        // Consume the leftover newline character after reading the integer
        scanner.nextLine();
    }

    /**
     * Sets up player names and symbols based on the selected game mode (isVsAI).
     * Initializes the MinimaxAI object if playing against the computer.
     */
    private void setupPlayers() {
        if (isVsAI) {
            // --- Human vs AI Setup ---
            System.out.print("Enter your name: ");
            String humanName = scanner.nextLine();

            // Ask the human player to choose their symbol ('B' or 'W')
            char choice = ' '; // Initialize with invalid char
            while (choice != 'B' && choice != 'W') {
                System.out.print(humanName + ", choose your symbol ('B' or 'W'): ");
                String input = scanner.nextLine().toUpperCase(); // Read input and convert to uppercase
                // Validate the input
                if (input.length() == 1 && (input.charAt(0) == 'B' || input.charAt(0) == 'W')) {
                    choice = input.charAt(0); // Valid choice
                } else {
                    System.out.println("Invalid input. Please enter 'B' or 'W'.");
                }
            }
            humanPlayerSymbol = choice;
            // Assign the remaining symbol to the AI
            aiPlayerSymbol = (humanPlayerSymbol == PLAYER1_SYMBOL) ? PLAYER2_SYMBOL : PLAYER1_SYMBOL;

            // Assign player names based on who gets 'B' (Player 1 always starts)
            if (humanPlayerSymbol == PLAYER1_SYMBOL) {
                player1Name = humanName; // Human is Player 1 (Black)
                player2Name = "AI";      // AI is Player 2 (White)
            } else {
                player1Name = "AI";      // AI is Player 1 (Black)
                player2Name = humanName; // Human is Player 2 (White)
            }

            System.out.println(humanName + " is " + humanPlayerSymbol);
            System.out.println("AI is " + aiPlayerSymbol);

            // Initialize the AI object with the chosen depth and assigned symbols
            ai = new MinimaxAI(AI_DEPTH, aiPlayerSymbol, humanPlayerSymbol);

        } else {
            // --- Human vs Human Setup ---
            System.out.print("Enter Player 1's name (will be Black '" + PLAYER1_SYMBOL + "'): ");
            player1Name = scanner.nextLine();
            System.out.print("Enter Player 2's name (will be White '" + PLAYER2_SYMBOL + "'): ");
            player2Name = scanner.nextLine();
            // Display assigned symbols
            System.out.println(player1Name + " is " + PLAYER1_SYMBOL + " (Black)");
            System.out.println(player2Name + " is " + PLAYER2_SYMBOL + " (White)");
        }
        // Set the starting player (Black always starts)
        currentPlayerSymbol = PLAYER1_SYMBOL;
    }


    // --- Core Game Loop ---
    /**
     * Contains the main loop of the game where players take turns until a win or draw occurs.
     */
    private void gameLoop() {
        boolean gameWon = false;  // Flag to indicate if a player has won
        boolean boardFull = false; // Flag to indicate if the board is full (draw)
        int lastRow = -1, lastCol = -1; // Store coordinates of the last move for win checking

        // Loop continues as long as no one has won AND the board is not full
        while (!gameWon && !boardFull) {
            // Display the current board state
            board.display();
            // Get the name of the player whose turn it is
            String currentPlayerName = getCurrentPlayerName();
            // Determine if the current player is the AI
            boolean isCurrentPlayerAI = isVsAI && (currentPlayerSymbol == aiPlayerSymbol);

            int[] move; // To store the chosen move coordinates [row, col]

            // --- Get Move ---
            if (isCurrentPlayerAI) {
                // If it's the AI's turn, call the AI's method to find the best move
                move = ai.findBestMove(board);
                // Display the AI's chosen move
                System.out.println(currentPlayerName + " (" + currentPlayerSymbol + ") places at (" + move[0] + ", " + move[1] + ")");
            } else {
                // If it's a human player's turn, prompt for input
                move = getPlayerMove(currentPlayerName);
            }

            // Store the coordinates of the move made
            lastRow = move[0];
            lastCol = move[1];

            // --- Make Move ---
            // Attempt to place the current player's symbol on the board at the chosen coordinates
            boolean moveSuccess = board.placeSymbol(lastRow, lastCol, currentPlayerSymbol);

            // This check should ideally not fail due to prior validation, but good practice
            if (!moveSuccess) {
                System.out.println("Error: Invalid move attempted during placement. Turn skipped (THIS SHOULD NOT HAPPEN).");
                continue; // Skip to the next iteration (potentially problematic, should ideally not be reachable)
            }

            // --- Check Game End Conditions ---
            // Check if the move just made resulted in a win for the current player
            gameWon = board.checkWin(lastRow, lastCol, currentPlayerSymbol);

            // If no win occurred, check if the board is now full (resulting in a draw)
            if (!gameWon) {
                boardFull = board.isFull();
            }

            // --- Switch Player ---
            // If the game is not over (no win and not full), switch to the other player for the next turn
            if (!gameWon && !boardFull) {
                switchPlayer();
            }
        } // End of game loop

        // --- Game Over ---
        // Display the final result (win or draw)
        displayResult(gameWon, boardFull);
    } // End of gameLoop method

    // --- Helper Methods ---

    /**
     * Prompts the current human player for their move (row and column).
     * Includes input validation to ensure the move is within bounds and on an empty cell.
     * Handles non-numeric input gracefully.
     * @param playerName The name of the player whose turn it is.
     * @return An integer array `[row, col]` representing the valid move coordinates entered by the user.
     */
    private int[] getPlayerMove(String playerName) {
        int row = -1, col = -1; // Initialize with invalid values
        boolean validInput = false; // Flag to track if valid input has been received
        int boardSize = board.getSize(); // Get board dimension for prompts/validation

        // Loop until valid input is received
        while (!validInput) {
            System.out.println(playerName + "'s (" + currentPlayerSymbol + ") turn.");
            System.out.print("Enter row (0-" + (boardSize - 1) + "): ");
            try {
                // Attempt to read the row number
                row = scanner.nextInt();
                System.out.print("Enter column (0-" + (boardSize - 1) + "): ");
                // Attempt to read the column number
                col = scanner.nextInt();

                // --- Validate the coordinates ---
                if (board.isWithinBounds(row, col)) {
                    // Check if the chosen cell is empty
                    if (board.isEmpty(row, col)) {
                        validInput = true; // Input is valid! Exit the loop.
                    } else {
                        // Cell is already occupied
                        System.out.println("Cell (" + row + "," + col + ") is already occupied. Try again.");
                    }
                } else {
                    // Coordinates are outside the board boundaries
                    System.out.println("Invalid coordinates. Row/Col must be between 0 and " + (boardSize - 1) + ". Try again.");
                }
            } catch (InputMismatchException e) {
                // Catch errors if the user enters something that's not an integer
                System.out.println("Invalid input. Please enter numbers for row and column.");
                // scanner.next(); // Consume the invalid token that caused the exception
                // It's safer to consume the entire line after an error to clear the buffer
                if(scanner.hasNextLine()) scanner.nextLine();

            } finally {
                // This block executes regardless of whether an exception occurred.
                // It's crucial for handling the leftover newline character after nextInt().
                if(validInput && scanner.hasNextLine()) {
                    // After valid input (two nextInt()), there might still be a newline waiting.
                    // Read and discard the rest of the line to prevent issues on the next input read (like nextLine()).
                    String leftover = scanner.nextLine();
                    if (!leftover.trim().isEmpty()) {
                        // If the user typed extra characters after the valid column number (e.g., "3 abc")
                        System.out.println("Unexpected input after column number. Please enter row and column only.");
                        validInput = false; // Mark input as invalid again to re-prompt
                    }
                } else if (!validInput && scanner.hasNextLine()){
                    // If input was invalid (e.g., text entered), ensure the rest of that invalid line is consumed.
                    // This might already be handled by the nextLine() in the catch block, but adds robustness.
                    scanner.nextLine();
                }
            }
        } // End validation loop
        // Return the validated coordinates
        return new int[]{row, col};
    }


    /**
     * Switches the `currentPlayerSymbol` between PLAYER1_SYMBOL ('B') and PLAYER2_SYMBOL ('W').
     */
    private void switchPlayer() {
        currentPlayerSymbol = (currentPlayerSymbol == PLAYER1_SYMBOL) ? PLAYER2_SYMBOL : PLAYER1_SYMBOL;
    }

    /**
     * Gets the name of the player whose turn it currently is.
     * @return The name (`player1Name` or `player2Name`) corresponding to the `currentPlayerSymbol`.
     */
    private String getCurrentPlayerName() {
        // Determine the name based on the current player's symbol
        if (currentPlayerSymbol == PLAYER1_SYMBOL) {
            return player1Name; // Player 1's name
        } else {
            return player2Name; // Player 2's name
        }
    }

    /**
     * Displays the final game result message (Win or Draw) after the game loop finishes.
     * Also shows the final state of the board.
     * @param win True if a player won, false otherwise.
     * @param draw True if the game ended in a draw (board full, no winner), false otherwise.
     */
    private void displayResult(boolean win, boolean draw) {
        board.display(); // Show the final board configuration
        System.out.println("***********************************"); // Separator line
        if (win) {
            // If the 'win' flag is true, display the winner's name and symbol
            System.out.println("GAME OVER! " + getCurrentPlayerName() + " (" + currentPlayerSymbol + ") wins!");
        } else if (draw) {
            // If the 'draw' flag is true, declare a draw
            System.out.println("GAME OVER! It's a draw!");
        } else {
            // This case should ideally not be reached if the game loop logic is correct
            System.out.println("GAME OVER! (Unexpected state - No win or draw detected)");
        }
        System.out.println("***********************************"); // Separator line
    }


    // --- Entry Point ---
    /**
     * The main method where program execution begins.
     * Creates an instance of the GomokuGame and starts the game by calling the run() method.
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Create a new GomokuGame object
        GomokuGame game = new GomokuGame();
        // Start the game execution flow
        game.run();
    } 

} 
