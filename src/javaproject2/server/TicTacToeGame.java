package javaproject2.server;

import java.util.Arrays;

public class TicTacToeGame {
    private char[][] board;
    private char currentPlayer;
    private int playerCount;
    private boolean gameFinished;

    public TicTacToeGame() {
        board = new char[3][3];
        for (char[] row : board) {
            Arrays.fill(row, '-');
        }
        currentPlayer = 'X';
        playerCount = 0;
        gameFinished = false;
    }

    public char addPlayer() {
        if (playerCount == 0) {
            playerCount++;
            return 'X';
        } else if (playerCount == 1) {
            playerCount++;
            return 'O';
        } else {
            throw new IllegalStateException("Game is full");
        }
    }

    public String makeMove(int row, int col, char player) {
        if (gameFinished) {
            return "Game is already finished.";
        }

        if (player != currentPlayer) {
            return "It's not your turn.";
        }

        if (row < 0 || row >= 3 || col < 0 || col >= 3 || board[row][col] != '-') {
            return "Invalid move, try again.";
        }

        board[row][col] = currentPlayer;
        if (checkWin()) {
            gameFinished = true;
            return "Player " + currentPlayer + " wins!\n" + getBoard();
        }

        if (checkDraw()) {
            gameFinished = true;
            return "It's a draw!\n" + getBoard();
        }

        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        return getBoard();
    }

    public String getBoard() {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true;
            }
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return true;
            }
        }

        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return true;
        }

        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            return true;
        }

        return false;
    }

    private boolean checkDraw() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }
}

