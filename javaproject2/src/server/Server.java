package javaproject2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<ClientHandler> waitingClients = new ArrayList<>();
    public static List<GameRoom> gameRooms = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for players...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                waitingClients.add(clientHandler);
                new Thread(clientHandler).start();

                if (waitingClients.size() >= 2) {
                    ClientHandler player1 = waitingClients.remove(0);
                    ClientHandler player2 = waitingClients.remove(0);
                    GameRoom gameRoom = new GameRoom(player1, player2);
                    gameRooms.add(gameRoom);
                    new Thread(gameRoom).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void removeClient(ClientHandler clientHandler) {
        waitingClients.remove(clientHandler);
        System.out.println("Client " + clientHandler + " disconnected.");
    }
}
