package lab1;

	import javax.imageio.ImageIO;
	import java.awt.*;
	import java.awt.image.BufferedImage;
	import java.io.*;
	import java.net.Socket;
	import java.net.UnknownHostException;
	import java.nio.ByteBuffer;
	import java.nio.file.Path;
	import java.nio.file.Paths;
	import java.util.Scanner;


	public class Main {

	    public static void main(String[] args) {
	        Socket socket;
	        BufferedReader in;
	        BufferedWriter writer;

	        Scanner userInput = new Scanner(System.in);

	        // ADRESSE IP
	        String IP = "";
	        System.out.println("Entrer l'adresse IP du serveur: ");
	        do {
	            if (userInput.hasNextLine()) {
	                IP = userInput.nextLine();
	            }
	            if (!isIPAddressValid(IP)) {
	                System.out.println("Adresse IP invalide. Veuillez recommencer.");
	            }
	        } while (!isIPAddressValid(IP));

	        // PORT
	        int port = 0;
	        System.out.println("Entrer le port d'Ècoute entre 5000 et 5050: ");
	        do {
	            if (userInput.hasNextInt()) {
	                port = userInput.nextInt();
	            }
	            else {
	                userInput.nextLine(); // Nettoyer la ligne si l'usager a rentrÈ un string
	            }
	            if (port < 5000 || port > 5050) {
	                System.out.println("Port d'Ècoute invalide. Recommencez.");
	            }
	        } while(port < 5000 || port > 5050);
	        userInput.nextLine();
	        System.out.println(port);

	        // CONNEXION
	        try {

	            // Ouverture pour connexion et Èchange avec le serveur
	            socket = new Socket(IP, port);
	            System.out.println("Demande de connexion");
	            Scanner Q = new Scanner(socket.getInputStream());
	            PrintStream R = new PrintStream(socket.getOutputStream());

	            String demandeLogin = Q.nextLine();
	            System.out.println(demandeLogin);
	            String login = userInput.nextLine();
	            R.println(login);

	            Boolean vieux = Q.nextBoolean();
	            Q.nextLine(); // Nettoyer le '\n' engendrÈ par le nextBoolean

	            String demandePass = Q.nextLine();
	            System.out.println(demandePass);
	            String pass = userInput.nextLine();
	            R.println(pass);

	            // Si l'usager est dÈj‡ prÈsent dans la base de donnÈes
	            if (vieux){
	                // Le serveur lui demande son mot de passe jusqu'‡ ce qu'il soit bon
	                String correct = Q.nextLine();
	                while (correct.equals("non")){
	                    demandePass = Q.nextLine();
	                    System.out.println(demandePass);
	                    pass = userInput.nextLine();
	                    R.println(pass);
	                    correct = Q.nextLine();
	                }
	            }

	            String motAccueil = Q.nextLine();
	            System.out.println(motAccueil);

	            String demandeAction = Q.nextLine();
	            System.out.println(demandeAction);

	            String decision = "";
	            do {
	                if (userInput.hasNextLine()) {
	                    decision = userInput.nextLine();
	                }
	                if (!decision.equals("oui") && !decision.equals("non")){
	                    System.out.println("oui ou non ?");
	                }
	            } while(!decision.equals("oui") && !decision.equals("non"));
	            R.println(decision);

	            // Envoi d'une image si l'usager le dÈsire
	            if (decision.equals("oui")) {

	                // SÈlection directe de l'image ‡ l'aide d'une boÓte de recherche ("FileDialog")
	                System.out.println("Choisir l'image ‡ filtrer. Assurez-vous toutefois qu'elle soit en format JPEG!");
	                FileDialog imageDialog = new FileDialog((Frame)null, "SÈlectionner l'image");
	                imageDialog.setVisible(true);
	                File[] imageFile = imageDialog.getFiles();
	                String imagePath = "";
	                String fileName = "";

	                if(imageFile.length > 0) {
	                    imagePath = imageDialog.getFiles()[0].getAbsolutePath();
	                    Path p = Paths.get(imagePath);
	                    fileName = p.getFileName().toString();
	                }
	                else {
	                    System.out.println("Aucune image de sÈlectionnÈe!");
	                }

	                // Lecture de l'image
	                BufferedImage toFilterImage = null;
	                try {
	                    toFilterImage = ImageIO.read(new File(imagePath));
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }

	                // Envoi de l'image (convertie en type BufferedImage)
	                OutputStream outputStream = socket.getOutputStream();
	                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	                ImageIO.write(toFilterImage, "jpg", byteArrayOutputStream);
	                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
	                outputStream.write(size);
	                outputStream.write(byteArrayOutputStream.toByteArray());
	                outputStream.flush();

	                System.out.println("Votre image a ÈtÈ transmise!");

	                // Envoi du fileName pour LA ligne du serveur
	                R.println(fileName);

	                // RÈception de l'image filtrÈe
	                System.out.println("Entrer le nom que vous voulez donner ‡ l'image filtrÈe:");
	                String NomImageFiltree = "";
	                if (userInput.hasNextLine()) {
	                    NomImageFiltree = userInput.nextLine();
	                }

	                // Le "directory" pour mettre l'image est le dossier "src" du projet "Client"
	                String dir = System.getProperty("user.dir");
	                String imageDir = dir + "\\src\\";

	                // RÈception et enregistrement de l'image filtrÈe
	                InputStream inputStream = socket.getInputStream();
	                byte[] sizeAr = new byte[4];
	                inputStream.read(sizeAr);
	                int size2 = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
	                byte[] imageAr = new byte[size2];
	                inputStream.read(imageAr);
	                BufferedImage imageFiltre = ImageIO.read(new ByteArrayInputStream(imageAr));
	                ImageIO.write(imageFiltre, "jpg",new File(imageDir + NomImageFiltree +".jpg"));

	                System.out.println("Votre image a ÈtÈ filtrÈe! Vous pouvez la rÈcupÈrer ici : " +imageDir+NomImageFiltree+ ".jpg");
	                socket.close();

	            }
	            else {
	                socket.close();
	            }

	            System.out.println("DÈconnexion du client");
	            System.exit(0);

	        } catch (UnknownHostException e) {

	            e.printStackTrace();
	        } catch (IOException e) {

	            e.printStackTrace();
	        }
	    }


	    // V…RIFICATION DE LA VALIDIT… DE L'ADREESE IP
	    private static boolean isIPAddressValid(String ip) {
	        boolean result = true;
	        int i = 0;
	        int[] val = new int[4];

	        // DiffÈrents cas possibles de non-conformitÈ
	        if ((ip == null) || (ip.trim().length() == 0)) {
	            // IP nulle
	            result = false;
	        } else {
	            if (!(ip.contains("."))) {
	                // IP sans "."
	                result = false;
	            } else {
	                String[] parts = ip.split("\\.");
	                if (!(parts.length == 4)) {
	                    // IP ne possËde pas 4 parties
	                    result = false;
	                } else {
	                    for (String s : parts) {
	                        try {
	                            val[i] = Integer.parseInt(s);
	                            if ((val[i] < 0) || (val[i] > 255)) {
	                                // IP ne possËde pas 4 octects standards
	                                result = false;
	                            }
	                            i++;
	                        } catch (Exception e) {
	                            // …chec de conversion
	                            result = false;
	                        }
	                    }
	                }
	            }
	        }
	        return result;
	    }

	}

