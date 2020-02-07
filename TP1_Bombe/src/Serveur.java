package lab1;

	import java.io.*;
	import java.net.ServerSocket;
	import java.net.Socket;
	import java.util.Scanner;
	import java.lang.*;

	public class Serveur {

	    public static void main(String[] args) throws IOException {

	        ServerSocket socketserver;
	        Socket clientSocket = null;
	        Scanner userInput = new Scanner(System.in);

	        // Demande du port d'Ècoute ‡ l'utilisateur.
	        int port = 0;
	        System.out.println("Entrer le port d'Ècoute: ");
	        port = userInput.nextInt();
	        while (port < 5000 || port > 5050) {
	            System.out.println("Mauvais port d'Ècoute!!");
	            System.out.println("Entrer un port d'Ècoute: ");
	            if (userInput.hasNextInt()) {
	                port = userInput.nextInt();
	            }
	            else {
	                userInput.nextLine(); // Nettoyer la ligne si l'usager a rentrÈ un string
	            }
	        }

	        // Ouverture du serveur pour connexion sur le port spÈcifiÈ
	        socketserver = new ServerSocket(port);
	        System.out.println("Le serveur est ‡ l'Ècoute du port " + socketserver.getLocalPort());

	        while (true) {

	            try {
	                // Connexion d'un client
	                clientSocket = socketserver.accept();

	                // Ouverture d'un thread de gestion de client ("gestionnaireClient")
	                Thread t = new Thread(new gestionnaireClient(clientSocket, socketserver));
	                t.start();

	            } catch (Exception e) {
	                clientSocket.close();
	                e.printStackTrace();
	            }
	        }
	    }
	}
