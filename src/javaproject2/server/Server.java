package javaproject2.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<ClientHandler> waitingClients = new ArrayList<>();
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    public static Socket socket;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for players...");

            while (true) {
                socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socket);

                waitingClients.add(clientHandler);
                new Thread(clientHandler).start();

                if (waitingClients.size() >= 2) {
                    ClientHandler player1 = waitingClients.remove(0);
                    ClientHandler player2 = waitingClients.remove(0);
                    clientHandlers.add(player1);
                    clientHandlers.add(player2);
                    startGame(player1, player2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startGame(ClientHandler player1, ClientHandler player2) {
        TicTacToeGame game = new TicTacToeGame();

        player1.setGame(game);
        player1.setPlayerSymbol('X');
        player2.setGame(game);
        player2.setPlayerSymbol('O');

        player1.getOut().println("Game starting... You are player " + player1.getPlayerSymbol());
        player2.getOut().println("Game starting... You are player " + player2.getPlayerSymbol());

        player1.getOut().println(game.getBoard());
        player2.getOut().println(game.getBoard());

        if (player1.getPlayerSymbol() == 'X') {
            player1.getOut().println("Make your move: ");
        } else {
            player2.getOut().println("Make your move: ");
        }
    }

    public static void removePlayer(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        waitingClients.remove(clientHandler);
    }
}
