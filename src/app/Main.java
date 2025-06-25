package app;

import service.UserService;
import utils.LogHandler;

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
                System.out.println("✅ User Created");
            } catch(IOException e) {
                LogHandler.logError("From addToDB, an error occured during the reading process: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("❌ Error : " + e.getMessage());
            }
        }

        ApplicationMenu.start();
    }
}
