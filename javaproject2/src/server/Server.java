package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for players...");

            TicTacToeGame game = new TicTacToeGame();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                char playerSymbol = game.addPlayer();
                ClientHandler clientHandler = new ClientHandler(clientSocket, game, playerSymbol);

                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();

                if (clientHandlers.size() == 2) {
                    System.out.println("Two players connected. Starting the game.");
                    // Notify players to start the game
                    for (ClientHandler handler : clientHandlers) {
                        handler.getOut().println("Game starting... You are player " + handler.getPlayerSymbol());
                        handler.getOut().println(handler.getGame().getBoard());
                        if (handler.getPlayerSymbol() == 'X') {
                            handler.getOut().println("Make your move: ");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

