Gomoku Game with Advanced Minimax AI
An advanced implementation of the classic Gomoku (Five in a Row) game that features both Human vs Human and Human vs AI modes. The AI utilizes a Minimax algorithm enhanced with Alpha-Beta Pruning, providing a challenging opponent that evaluates board states using a heuristic function.

Table of Contents
Overview

Features

Architecture and Design

Project Structure

Implementation Details

How to Run the Game

Game Instructions

Future Improvements

Credits

License

Overview
This project is developed as part of the COMP 2080 Data Structures and Algorithms course (Winter 2025). The application is a console-based Gomoku game that supports both two-player and player versus AI game modes. The AI opponent leverages a Minimax algorithm with Alpha-Beta Pruning to decide optimal moves by evaluating potential board states. While the heuristic evaluation used is a foundational version, it serves as a base for future refinements and enhancements.

Features
Multiple Game Modes:

Human vs Human: Traditional two-player mode.

Human vs AI: Play against an AI that uses the Minimax algorithm.

Advanced AI Opponent:

Uses a recursive Minimax algorithm with Alpha-Beta Pruning for efficient decision making.

Configurable search depth allows a trade-off between speed and AI strength.

Robust Game Mechanics:

Comprehensive input validation ensures reliable user interactions.

Dynamic board display with row and column indices for intuitive gameplay.

Modular Design:

Organized into separate classes (GomokuGame, MinimaxAI, and Board) for clear separation of concerns.

Easy to extend and maintain, with scope for enhancing the heuristic function and game logic.

Architecture and Design
Core Components
GomokuGame.java:
This is the main driver class that initializes the game, handles user input, manages game modes, and maintains the main game loop. It seamlessly integrates Human vs Human and Human vs AI modes.

MinimaxAI.java:
Implements the AI opponent’s logic. The AI simulates moves on a temporary board using the Minimax algorithm, applies Alpha-Beta Pruning to optimize search, and uses a heuristic evaluation to score non-terminal game states.

Board.java:
Responsible for managing the game board. It maintains a 2D array representing the grid, handles cell updates, and includes methods for checking win conditions and drawing the board.

Design Considerations
Efficiency:
The AI decision process is optimized with Alpha-Beta Pruning, reducing the computational overhead when evaluating numerous board configurations.

Extendability:
Each module is designed to be self-contained. For example, the heuristic function in MinimaxAI can be easily replaced or enhanced to consider more complex game scenarios (like open-ended lines or advanced pattern recognition).

Robustness:
Input validation and proper handling of exceptional cases (such as invalid user input) make the game reliable during play.

Project Structure
cpp
Copy
/GomokuGameProject
├── Board.java         // Manages the game board and win conditions.
├── GomokuGame.java    // Main class for game orchestration and user interaction.
├── MinimaxAI.java     // Implements the AI logic using the Minimax algorithm.
└── README.md          // This comprehensive project documentation.
Each Java file contains detailed comments outlining class responsibilities, methods, and key logic sections. The team has followed rigorous documentation practices to support maintainability and future development.

Implementation Details
AI and Decision Making
Minimax Algorithm:
The AI employs a depth-limited minimax algorithm to examine possible moves. It uses a recursive approach to evaluate the optimal move from a given board state.

Alpha-Beta Pruning:
To improve performance by eliminating branches of the game tree that won’t affect the final decision, the algorithm incorporates Alpha-Beta Pruning. This optimization ensures that the AI can search deeper within a reasonable amount of time.

Heuristic Evaluation:
When the maximum search depth is reached without a decisive outcome (win, loss, or draw), the AI uses a basic heuristic that assesses board configurations by counting potential streaks (2, 3, or 4 in a row). Although simple, this evaluation function can be fine-tuned to reflect more complex positional advantages and threats.

Board Management
Grid Structure:
The board is implemented as a 2D character array. Each cell is initialized with an EMPTY_SLOT and updates as players make their moves.

Win Condition Check:
After each move, the Board class checks for winning sequences (5 in a row) in all directions (horizontal, vertical, and two diagonals).

How to Run the Game
Prerequisites
Java Development Kit (JDK):
Ensure you have JDK 8 or later installed. You can download the JDK from Oracle or use OpenJDK.

Compile and Run
Compile the Java Files:
Open a terminal in the project directory and run:

bash
Copy
javac GomokuGame.java MinimaxAI.java Board.java
Run the Game:
After successful compilation, start the game with:

bash
Copy
java GomokuGame
Game Instructions
Startup:
On launch, the game welcomes you and prompts you to select between Human vs AI or Human vs Human modes.

Player Setup:

For Human vs AI, you will input your name and choose your playing symbol (B or W). The AI will automatically use the alternate symbol.

For Human vs Human, both players enter their names, and symbols are pre-assigned (Player 1 as Black ‘B’, Player 2 as White ‘W’).

Game Loop:
The game displays the board with row and column numbers. Each turn, the active player enters their move by specifying the row and column numbers.

Win/Draw Detection:
After every move, the board checks for a winning condition (5 consecutive symbols) or if the board is full (resulting in a draw).

Game End:
The game concludes by displaying the final board state and announcing the winner or declaring a draw.

Future Improvements
Enhanced Heuristic:
Develop a more sophisticated evaluation function that considers open-ended lines, potential threats, and strategic patterns.

Graphical User Interface (GUI):
Transition from a console-based interface to a rich GUI using libraries such as JavaFX or Swing.

Multithreading:
Employ multithreaded processing for the AI calculations, enabling a deeper search depth and smoother gameplay on multi-core systems.

Custom Game Options:
Allow for customizable board sizes, varied win conditions, and adjustable AI difficulty levels.

Credits
Team Members:

Parsa Molahosseini (Student ID: 101491591)

Mehrad Bayat (Student ID: 101533701)

Jerry-lee Somera (Student ID: 101521229)

Soroush Salari (Student ID: 101537771)

Developed as part of the COMP 2080 Group Project for Winter 2025.

License
This project is licensed under the MIT License. Feel free to use, modify, and distribute the code as permitted by the license.
