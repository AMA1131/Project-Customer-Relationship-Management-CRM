package app;

import model.Client;
import model.User;
import model.composite_interaction.Interaction;
import service.ClientService;
import service.InteractionService;
import service.UserService;
import utils.LogHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ApplicationMenu {
    private static final Scanner scan = new Scanner(System.in);
    private static UserService userService;
    private static ClientService clientService;
    private static InteractionService interactionService;



    public static void start() {
        try{
            userService = UserService.getUniqueInstance();
            clientService = ClientService.getUniqueInstance();
            interactionService = InteractionService.getUniqueInstance();
        } catch (RuntimeException e) {
            System.err.println("❌ Application Starting failed: Please contact Support.");
            LogHandler.logError("App Stop : " + e.getMessage());
            System.exit(1);
        }
        while (true) {
            System.out.println("=== Welcome ===");
            System.out.println("1. Login");
            /*System.out.println("2. Create an account");*/
            System.out.println("2. Quit");
            System.out.print("Your choice: ");
            String choice = scan.nextLine();

            switch(choice) {
                case "1" -> loginMenu();
                /*case "2" -> createUsermenu();*/
                case "2" -> {
                    System.out.println("👋 GoodBye!");
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice");
            }
        }
    }

    public static void loginMenu() {

        System.out.print("Email : ");
        String email = scan.nextLine();
        System.out.print("Password : ");
        String password = scan.nextLine();

        User user = userService.aunticateUser(email, password);
        if (user != null) {
            SessionManager.login(user);
            System.out.println("✅ Login Successful");

            if (user.getRole().equals("admin")) {
                adminMenu();
            } else{
                userMenu();
            }
        } else{
            System.err.println("❌ Incorrect identifiers. Please retry");
        }
    }

    public static void createUserInterface() {
        Path filePath = Paths.get("data/users.json");
        String role;
        try {
            System.out.print("Firstname : ");
            String firstName = scan.nextLine();
            System.out.print("Lastname : ");
            String lastName = scan.nextLine();
            System.out.print("Email : ");
            String email = scan.nextLine();
            if (!Files.exists(filePath)) {
                role = "admin";
            } else {
                System.out.print("Role (admin/user) : ");
                role = scan.nextLine();
            }
            System.out.print("Password : ");
            String password = scan.nextLine();

            userService.addUser(firstName, lastName, email, role, password);
            System.out.println("✅ User created");
        } catch (IllegalArgumentException e) {
            LogHandler.logWarning("Illegal argument: " + e.getMessage());
            System.err.println("Illegal argument: " + e.getMessage());
        } catch(IOException e) {
            LogHandler.logError("From addToDB, an error occured during the reading process: " + e.getMessage());
        } catch (Exception e) {
            LogHandler.logError("An error occured creation: " + e.getMessage());
            System.out.println("❌ Error : " + e.getMessage());
        }
    }
    public static void removeUserInterface() {
        System.out.print("Enter the user ID : ");
        int id = scan.nextInt();
        scan.nextLine();
        try{
            boolean result = userService.removeUser(id);
            if (result) {
                System.out.println("✅ user deleted from database");
            } else {
                System.out.println("❌ Id not found in database");
            }
        } catch (IOException e) {
            LogHandler.logError("An error occured while saving." + e.getMessage());
            System.err.println("❌ Operation Failed: IO Error");
        } catch (Exception e) {
            LogHandler.logError("An error occured during the interaction removal: " + e);
            System.err.println("❌ An Error occured: Operation Failed");
        }
    }
    public static void searchUserInterface() {
        System.out.print("Enter the user firstname : ");
        String firstname = scan.nextLine();

        ArrayList<User> matches = userService.findUserByName(firstname);
        if (!matches.isEmpty()) {
            for (User u : matches) {
                System.out.println(u);
            }
        } else {
            System.err.println("❌ No match found");
        }
    }

    public static void addClientInterface() {
        try {
            System.out.print("Firstname : ");
            String firstName = scan.nextLine();
            System.out.print("Lastname : ");
            String lastName = scan.nextLine();
            System.out.print("Email : ");
            String email = scan.nextLine();
            System.out.print("Telephone : ");
            String phone = scan.nextLine();
            System.out.print("Company (Press entry if your are an individual) : ");
            String company = scan.nextLine();

            clientService.addClient(firstName, lastName, email, phone, company);
            System.out.println("✅ Client created.");
        } catch (IllegalArgumentException e) {
            LogHandler.logWarning("Arguments Error" + e);
            System.err.println("❌" + e.getMessage() +" Please retry");
        } catch (RuntimeException e) {
            LogHandler.logError("From addToDB, an error occured during the saving process: " + e.getMessage());
            System.out.println("Operation failed: I/O issues Contact Support.");
        } catch (Exception e) {
            LogHandler.logError("An issue occured during the process" + e.getMessage());
            System.out.println("Operation failed: An error occured during process");
        }
    }
    public static void removeClientInterface(){
        System.out.print("Enter the Client ID : ");
        int id = scan.nextInt();
        scan.nextLine();
        try{
            boolean result = clientService.removeClient(id);
            if (result) {
                System.out.println("✅ client deleted from database");
            } else {
                System.out.println("❌ Id not found in database");
            }
        } catch (IOException e) {
            LogHandler.logError("An error occured while saving." + e.getMessage());
            System.err.println("❌ Operation Failed: IO Error");
        } catch (Exception e) {
            LogHandler.logError("An error occured during the interaction removal: " + e.getMessage());
            System.err.println("❌ An Error occured: Operation Failed");
        }
    }
    public static void searchClientInterface() {
        System.out.print("Enter the client firstname : ");
        String firstname = scan.nextLine();

        ArrayList<Client> matches = clientService.findClientByName(firstname);
        if (!matches.isEmpty()) {
            for (Client c : matches) {
                System.out.println(c);
            }
        } else {
            System.err.println("❌ No match found");
        }
    }

    public static void addInteractionInterface()  {
        try {
            System.out.print("Type (email/call/meeting) : ");
            String type = scan.nextLine();
            int userId = SessionManager.getCurrentUser().getId();
            System.out.print("Client ID : ");
            int clientId = Integer.parseInt(scan.nextLine());
            System.out.print("Description : ");
            String desc = scan.nextLine();


            interactionService.addInteractionToSubHistory(type, userId, clientId, desc);
            System.out.println("✅ Interaction created.");
        } catch (IOException e) {
            LogHandler.logError("From addToDB, an error occured during the reading process: " + e.getMessage());
            System.err.println("❌ Operation Failed: IO issue, please contact support");
        } catch (IllegalArgumentException e) {
            LogHandler.logWarning("Arguments Error" + e.getMessage());
            System.err.println("❌ Operation failed: " + e.getMessage());
        } catch (Exception e) {
            LogHandler.logError("An error occured during the interaction add process: " + e.getMessage());
            System.err.println("Operation failed:  please contact support");
        }
    }
    public static void removeInteractionInterface() {
            System.out.print("Enter the interaction ID: ");
            int id = scan.nextInt();
            scan.nextLine();
            try{
                boolean result = interactionService.removeInteraction(id);
                if (result) {
                    System.out.println("✅ Interaction deleted from database");
                } else {
                    System.out.println("❌ Id not found in database");
                }
            } catch (IOException e) {
                LogHandler.logError("An error occured while saving." + e.getMessage());
                System.err.println("❌ Operation Failed: IO Error");
            } catch (Exception e) {
                LogHandler.logError("An error occured during the interaction removal: " + e.getMessage());
                System.err.println("❌ An Error occured: Operation Failed");
            }
    }
    public static void searchInteractionInterface() {
        System.out.print("Enter a keyword of the interaction description : ");
        String keyword = scan.nextLine();

        ArrayList<Interaction> matches = interactionService.searchInteraction(keyword);
        if (!matches.isEmpty()) {
            for (Interaction i : matches) {
                    System.out.println(i);
            }
        } else {
            System.err.println("❌ No match found");
        }
    }
    public static void searchInteractionByClientIdInterface() {
        System.out.print("Enter the client ID  : ");
        int id = scan.nextInt();
        scan.nextLine();

        ArrayList<Interaction> matches = interactionService.getInteractionsByClientId(id);
        if (!matches.isEmpty()) {
            for (Interaction i : matches) {
                    System.out.println(i);
            }
        } else {
            System.err.println("❌ No interactions found");
        }
    }
    public static void updateInteractionDescription() {
        System.out.print("Enter the Interaction ID : ");
        int id = scan.nextInt();
        scan.nextLine();
        System.out.print("Type the description to update : ");
        String newDescription = scan.nextLine();
        try{
            interactionService.updateInteractionDescription(id, newDescription);
            System.out.println("✅ Interaction updated");
        } catch (IOException e) {
            LogHandler.logError("An error occured during the interaction update: " + e.getMessage());
            System.err.println("❌ IO Issue, an Error occured during the saving: Please contact support");
        }
    }

    public static void adminMenu() {
        while (true) {
            System.out.println("\n=== ADMINISTRATOR MENU ===");
            System.out.println("1. Add a new user");
            System.out.println("2. Remove a user");
            System.out.println("3. Search a user");
            System.out.println("4. Add a client");
            System.out.println("5. Remove a client");
            System.out.println("6. Search a client");
            System.out.println("7. Add interaction");
            System.out.println("8. remove interaction");
            System.out.println("9. Search interaction by description");
            System.out.println("10. Search interaction by client ID");
            System.out.println("11. Logout");
            System.out.print("Your choice : ");
            String choice = scan.nextLine();

            switch (choice) {
                case "1" -> createUserInterface();
                case "2" -> removeUserInterface();
                case "3" -> searchUserInterface();
                case "4" -> addClientInterface();
                case "5" -> removeClientInterface();
                case "6" -> searchClientInterface();
                case "7" -> addInteractionInterface();
                case "8" -> removeInteractionInterface();
                case "9" -> searchInteractionInterface();
                case "10" -> searchInteractionByClientIdInterface();
                case "11" -> {
                    SessionManager.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid choice");
            }
        }
    }

    public static void userMenu() {
        while (true) {
            System.out.println("\n=== USER MENU ===");
            System.out.println("1. Search a client");
            System.out.println("2. Add interaction");
            System.out.println("3. Search interaction by description");
            System.out.println("4. Search interaction by client ID");
            System.out.println("5. Update interaction");
            System.out.println("6. Logout");
            System.out.print("Your choice : ");
            String choice = scan.nextLine();

            switch (choice) {
                case "1" -> searchClientInterface();
                case "2" -> addInteractionInterface();
                case "3" -> searchInteractionInterface();
                case "4" -> searchInteractionByClientIdInterface();
                case "5" -> updateInteractionDescription();
                case "6" -> {
                    SessionManager.logout();
                    return;
                }
                default -> System.out.println("❌ Invalid choice");
            }
        }
    }
}
