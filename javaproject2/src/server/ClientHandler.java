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

    @Override
    public void run() {
        try {
            out.println("Game starting... You are player " + playerSymbol);
            String boardState = game.getBoard();
            out.println(boardState);

            String move;
            while ((move = in.readLine()) != null) {
                String[] moveParts = move.split(",");
                int row = Integer.parseInt(moveParts[0].trim()) - 1;
                int col = Integer.parseInt(moveParts[1].trim()) - 1;

                String response = game.makeMove(row, col, playerSymbol);
                out.println(response);

                if (response.contains("wins") || response.contains("draw")) {
                    break;
                }

                // Send updated board to the other player
                for (ClientHandler handler : Server.clientHandlers) {
                    if (handler != this) {
                        handler.getOut().println(game.getBoard());
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

