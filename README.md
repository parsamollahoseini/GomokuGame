# Gomoku Game with Advanced Minimax AI

[![Java](https://img.shields.io/badge/Java-1.8%2B-blue.svg)](https://www.java.com) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

An engaging console-based implementation of [Gomoku](https://en.wikipedia.org/wiki/Gomoku) with an intelligent AI opponent using a **Minimax algorithm with Alpha-Beta Pruning**. Play against the AI or challenge a friend in Human vs Human mode.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Algorithm Details](#algorithm-details)
- [Contribution Guidelines](#contribution-guidelines)
- [License](#license)
- [Contact](#contact)

---

## Overview

This project was developed for the COMP 2080 Data Structures and Algorithms course (Winter 2025). It demonstrates a robust Gomoku game along with:
- Implementation of win-condition detection.
- A challenging AI opponent using a depth-limited Minimax algorithm with Alpha-Beta Pruning.
- Support for both Human vs AI and Human vs Human game modes.

---

## Features

- **Multiple Game Modes:**  
  - *Human vs AI*: Play against an AI that makes smart decisions using Minimax.
  - *Human vs Human*: Two players can compete using turn-based gameplay.

- **Intelligent AI:**  
  - Utilizes a depth-limited Minimax algorithm with Alpha-Beta Pruning.
  - Adjustable search depth to balance performance and difficulty.
  
- **Interactive Console Interface:**  
  - Displays a dynamic board with row and column indices.
  - Ensures robust input validation for a seamless gaming experience.

- **Modular Codebase:**  
  - Clearly separated classes for game logic (`GomokuGame`), AI implementation (`MinimaxAI`), and board management (`Board`).
  - Inline documentation for easy maintenance and future improvements.

---

## Architecture

The project consists of three main components:

- **GomokuGame.java:**  
  Manages the overall game flow, user input, and game mode selection.

- **MinimaxAI.java:**  
  Contains the AI logic with a Minimax algorithm enhanced by Alpha-Beta Pruning, along with heuristic evaluation.

- **Board.java:**  
  Handles board initialization, symbol placement, win checking, and board display.

---

## Installation

### Prerequisites

- **Java Development Kit (JDK) 1.8 or later**  
  Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.java.net/).

### Setup Instructions

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/yourusername/GomokuGame.git
   cd GomokuGame
