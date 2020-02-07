package lab1;

	import java.io.*;
	import java.net.Socket;
	import java.util.Scanner;

	public class Client {

	    // Variables private pour log-in
	    private String login = null, pass = null;

	    // Variable d'instance
	    private Socket ss;

	    // Constructeur et mÈthode d'authentification qui roule par dÈfaut lors de l'instanciation
	    public Client(Socket p_ss){
	        ss = p_ss;

	        // Authentification
	        boolean vieux = false;
	        try {

	            // Demande du nom d'usager ("login")
	            Scanner sc = new Scanner(ss.getInputStream());
	            PrintStream p = new PrintStream(ss.getOutputStream());
	            p.println("Entrez le nom d'usager:");
	            if (sc.hasNextLine()) {
	                login = sc.nextLine();
	            }

	            File file = new File("Base.txt");
	            file.createNewFile(); // CrÈation d'une nouvelle base de donnÈes SSI elle n'existe pas!

	            // VÈrification de l'existence de l'usager dans la base de donnÈes
	            FileReader fr = new FileReader(file);
	            BufferedReader br = new BufferedReader(fr);
	            String str;
	            String[] splitStr;
	            String reelPass = null;
	            while ((str = br.readLine()) != null) {
	                splitStr = str.split("\\s+");
	                if (login.equals(splitStr[0])) {
	                    vieux = true;
	                    reelPass = splitStr[1];
	                }
	            }
	            br.close();

	            p.println(vieux);

	            // Si l'usager est dÈj‡ prÈsent dans la base de donnÈes
	            if (vieux) {
	                // Le serveur lui demande son mot de passe jusqu'‡ ce qu'il soit correct
	                String correct = "non";
	                p.println("Entrez le mot de passe:");
	                do {
	                    if (sc.hasNextLine()) {
	                        pass = sc.nextLine();
	                    }
	                    if (!pass.equals(reelPass)){
	                        p.println(correct);
	                        p.println("Erreur dans la saisie du mot de passe, rÈessayez:");
	                    }
	                } while(!pass.equals(reelPass));
	                correct = "oui";
	                p.println(correct);
	                p.println("Re-bonjour " + login + "!");
	            }
	            // Si l'usager est nouveau
	            else{
	                // Le serveur lui demande un nouveau mot de passe et rendre ces informations (login + pass) dans la base de donnÈes
	                FileWriter fw = new FileWriter(file, true); // Le append true permet d'ajouter le texte ‡ la suite du fichier
	                PrintWriter pw = new PrintWriter(fw);
	                p.println("Il n'existe aucun utilisateur ‡ ce nom. Choississez votre mot de passe:");
	                pass = sc.nextLine();
	                pw.println(login + " " + pass);
	                pw.close();
	                p.println("Bienvenue dans le systËme de traitement d'image " + login + "!");
	            }

	        }
	        catch (IOException e) {
	            System.err.println(login+" ne rÈpond pas !");
	        }
	    }

	    public String getLogin() {
	        return login;
	    }

	}
