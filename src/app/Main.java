package app;

import service.UserService;
import utils.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Path filePath = Paths.get("data/users.json");
        String role;
        Scanner scan = new Scanner(System.in);
        while (!Files.exists(filePath)) {
            UserService userService = UserService.getUniqueInstance();
            System.out.println("First connexion: Admin user Creation");
            try {
                System.out.print("Prénom : ");
                String firstName = scan.nextLine();
                System.out.print("Nom : ");
                String lastName = scan.nextLine();
                System.out.print("Email : ");
                String email = scan.nextLine();
                if (!Files.exists(filePath)) {
                    role = "admin";
                } else {
                    System.out.print("Rôle (admin/user) : ");
                    role = scan.nextLine();
                }
                System.out.print("Password : ");
                String password = scan.nextLine();

                userService.addUser(firstName, lastName, email, role, password);
                System.out.println("✅ Utilisateur créé");
            } catch(IOException e) {
                Logger.log("From addToDB, an error occured during the reading process: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("❌ Erreur : " + e.getMessage());
            }
        }

        ApplicationMenu.start();
    }
}
