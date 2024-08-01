package javaproject2.server;

import java.util.ArrayList;
import java.util.List;

public class GameRoom implements Runnable {
    private TicTacToeGame game;
    private List<ClientHandler> players = new ArrayList<>();

    public GameRoom(ClientHandler player1, ClientHandler player2) {
        this.game = new TicTacToeGame();
        this.players.add(player1);
        this.players.add(player2);

        player1.setGame(game);
        player1.setPlayerSymbol('X');
        player2.setGame(game);
        player2.setPlayerSymbol('O');

        player1.setGameRoom(this);
        player2.setGameRoom(this);

        player1.getOut().println("Game starting... You are player X");
        player2.getOut().println("Game starting... You are player O");

        player1.getOut().println(game.getBoard());
        player2.getOut().println(game.getBoard());

        if (player1.getPlayerSymbol() == 'X') {
                player1.getOut().println("Make your move: ");
        }
    }

    @Override
    public void run() {
        while (!game.isGameFinished()) {
            // The game loop is handled by the ClientHandler's run method
        }
        // When the game is finished, notify players
        for (ClientHandler player : players) {
            player.getOut().println("Game over.");
        }
    }

    public void broadcast(String message) {
        for (ClientHandler player : players) {
            player.getOut().println(message);
        }
    }

    public void removePlayer(ClientHandler player) {
        players.remove(player);
        if (players.isEmpty()) {
            // If both players leave, terminate the game
            game = null;
        }
    }

    public List<ClientHandler> getPlayers() {
        return players;
    }
}
