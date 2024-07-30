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
                int row = Integer.parseInt(moveParts[0].trim()) - 1;
                int col = Integer.parseInt(moveParts[1].trim()) - 1;

                String response = game.makeMove(row, col, playerSymbol);
                out.println(response);

                // Send updated board to both players
                for (ClientHandler handler : Server.clientHandlers) {
                    handler.getOut().println(game.getBoard());
                    if (handler.getPlayerSymbol() == game.getCurrentPlayer()) {
                        handler.getOut().println("Make your move: ");
                    }
                }

                if (response.contains("wins") || response.contains("draw")) {
                    break;
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

