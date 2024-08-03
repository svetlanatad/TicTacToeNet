package javaproject2.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private TicTacToeGame game;
    private char playerSymbol;
    private PrintWriter out;
    private BufferedReader in;
    private GameRoom gameRoom;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGame(TicTacToeGame game) {
        this.game = game;
    }

    public void setPlayerSymbol(char playerSymbol) {
        this.playerSymbol = playerSymbol;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
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
            String move;
            while (true) {
                move = in.readLine();

                if (move.equalsIgnoreCase("q")) {
                    out.println("You have quit the game.");
                    break;
                }

                String[] moveParts = move.split(",");
                if (moveParts.length != 2) {
                    out.println("Invalid input format. Use row,col (e.g., 1,2).");
                    for (ClientHandler handler : gameRoom.getPlayers()) {
                        if (handler.getPlayerSymbol() == game.getCurrentPlayer()) {
                            handler.getOut().println("Make your move: ");
                        }
                    }
                    continue;
                }

                int row, col;
                try {
                    row = Integer.parseInt(moveParts[0].trim()) - 1;
                    col = Integer.parseInt(moveParts[1].trim()) - 1;
                } catch (NumberFormatException e) {
                    out.println("Invalid input format. Use row,col (e.g., 1,2).");
                    for (ClientHandler handler : gameRoom.getPlayers()) {
                        if (handler.getPlayerSymbol() == game.getCurrentPlayer()) {
                            handler.getOut().println("Make your move: ");
                        }
                    }
                    continue;
                }

                synchronized (game) {
                    String response = game.makeMove(row, col, playerSymbol);

                    if (response.equals("It's not your turn.") || response.equals("Invalid move, try again.") || response.equals("Game is already finished.")) {
                        out.println(response);
                    } else {
                        // Send updated board to both players
                        gameRoom.broadcast(response);
                        if (game.isGameFinished()) {
                            gameRoom.broadcast("Game over. " + response);
                            clientSocket.close();
                            break;
                        } else {
                            for (ClientHandler handler : gameRoom.getPlayers()) {
                                if (handler.getPlayerSymbol() == game.getCurrentPlayer()) {
                                    handler.getOut().println("Make your move: ");
                                }
                            }
                        }
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
            Server.removeClient(this);
            if (gameRoom != null) {
                gameRoom.removePlayer(this);
            }
        }
    }
}
