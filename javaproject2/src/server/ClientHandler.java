package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private TicTacToeGame game;
    private char playerSymbol;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, TicTacToeGame game, char playerSymbol) {
        this.clientSocket = socket;
        this.game = game;
        this.playerSymbol = playerSymbol;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char getPlayerSymbol() {
        return playerSymbol;
    }

    public PrintWriter getOut() {
        return out;
    }

    public TicTacToeGame getGame() {
        return game;
    }

    @Override
    public void run() {
        try {
            String move;
            while ((move = in.readLine()) != null) {
                String[] moveParts = move.split(",");
                if (moveParts.length != 2) {
                    out.println("Invalid input format. Use row,col (e.g., 1,2).");
                    continue;
                }

                int row, col;
                try {
                    row = Integer.parseInt(moveParts[0].trim()) - 1;
                    col = Integer.parseInt(moveParts[1].trim()) - 1;
                } catch (NumberFormatException e) {
                    out.println("Invalid input format. Use row,col (e.g., 1,2).");
                    continue;
                }

                String response = game.makeMove(row, col, playerSymbol);

                if (response.equals("It's not your turn.") || response.equals("Invalid move, try again.") || response.equals("Game is already finished.")) {
                    out.println(response);
                } else {
                    // Send updated board to both players
                    for (ClientHandler handler : Server.clientHandlers) {
                        handler.getOut().println(response);
                        if (game.isGameFinished()) {
                            handler.getOut().println("Game over. " + response);
                            handler.clientSocket.close();
                        } else if (handler.getPlayerSymbol() == game.getCurrentPlayer()) {
                            handler.getOut().println("Make your move: ");
                        }
                    }

                    if (game.isGameFinished()) {
                        Server.clientHandlers.clear();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

