package lab1;

	import java.io.*;
	import java.net.*;
	import java.net.Socket;
	import java.nio.ByteBuffer;
	import java.util.Scanner;
	import java.awt.image.BufferedImage;
	import javax.imageio.*;
	import java.net.InetAddress;
	import java.time.format.DateTimeFormatter;
	import java.time.LocalDateTime;

	public class gestionnaireClient implements Runnable {

	    // Variables d'instance
	    private final ServerSocket socketserver;
	    private Socket clientSocket;

	    // Constructeur
	    public gestionnaireClient(Socket p_clientSocket, ServerSocket p_socketserver){
	        this.socketserver = p_socketserver;
	        this.clientSocket = p_clientSocket;
	    }

	    public void run() {
	        try {
	            Scanner sc = new Scanner(clientSocket.getInputStream());
	            PrintStream p = new PrintStream(clientSocket.getOutputStream());

	            // Instanciation d'un nouvel objet "client" ‡ chaque connexion
	            // Processus d'identification du client contenu dans le constructeur Client
	            Client client = new Client(clientSocket);

	            String clientName = client.getLogin();
	            System.out.println("Le client "+clientName+" s'est connectÈ.");

	            // Path de l'image filtrÈe
	            String dir = System.getProperty("user.dir");
	            String fullPath = dir + "\\ImageFiltre.jpg";

	            p.println("DÈsirez-vous filtrer une image (oui ou non)? ");
	            String answer = sc.nextLine();

	            if (answer.equals("oui")) {
	                System.out.println("Le client "+clientName+" va envoyer une image ‡ filtrer.");
	                // RÈception de l'image ‡ filtrer
	                InputStream inputStream = this.clientSocket.getInputStream();
	                byte[] sizeAr = new byte[4];
	                inputStream.read(sizeAr);
	                int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
	                byte[] imageAr = new byte[size];
	                inputStream.read(imageAr);

	                // Lecture du nom du fichier ‡ filtrer pour affichage de la ligne
	                String fileName = sc.nextLine();

	                // Affichage de LA ligne demandÈe
	                InetAddress Adresse = InetAddress.getLocalHost();
	                DateTimeFormatter dataFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
	                LocalDateTime now = LocalDateTime.now();
	                System.out.println("["+clientName+" - "+Adresse.getLocalHost().getHostAddress()+":"+this.socketserver.getLocalPort()+" - "+dataFormat.format(now)+"] : "+fileName+" reÁue");

	                // RÈcupÈration de l'image en format BufferedImage
	                BufferedImage imageAfiltrer = ImageIO.read(new ByteArrayInputStream(imageAr));

	                // Application du filtre Sobel
	                BufferedImage imageFiltre = Sobel.process(imageAfiltrer);

	                // Enregistrement de l'image filtrÈe
	                try {
	                    File outputfile = new File(fullPath);
	                    ImageIO.write(imageFiltre, "jpg", outputfile);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }

	                // Envoi de l'image filtrÈe
	                OutputStream outputStream = this.clientSocket.getOutputStream();
	                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	                ImageIO.write(imageFiltre, "jpg", byteArrayOutputStream);
	                byte[] size2 = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
	                outputStream.write(size2);
	                outputStream.write(byteArrayOutputStream.toByteArray());
	                outputStream.flush();

	                System.out.println("Le client "+clientName+" s'est dÈconnectÈ.");
	                this.clientSocket.close();
	            }
	            else {
	                System.out.println("Le client "+clientName+" s'est dÈconnectÈ.");
	                this.clientSocket.close();
	            }

	        }catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

