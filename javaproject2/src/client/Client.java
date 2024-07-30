package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
                if (response.contains("wins") || response.contains("draw")) {
                    break;
                }

                if (response.contains("Enter your move")) {
                    String move = scanner.nextLine();
                    out.println(move);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

