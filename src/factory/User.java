package factory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import model.Entity;
import service.UserService;
import utils.InputValidator;
import utils.PasswordHasher;

public class User extends AbstractEntity {
    private static final AtomicInteger idCounter = new AtomicInteger(initIdCounter());

    private static int initIdCounter() {
        int maxId = 0;
        ArrayList<model.User> users = UserService.getUniqueInstance().getusers();
        if (!users.isEmpty()) {
            for (model.User u : users) {
                if (u.getId() > maxId) {
                    maxId = u.getId();
                }
            }
        }
        return maxId + 1;
    }

    @Override
    public Entity createEntity(Object... args){
        if (args.length < 5) {
            throw new IllegalArgumentException("Insufficient parameters to create a user");
        }

        String firstName = ((String) args[0]).trim().toLowerCase().replaceAll("\\s+", " ");
        String lastName = ((String) args[1]).trim().toLowerCase().replaceAll("\\s+", " ");
        String email = ((String) args[2]).trim().toLowerCase().replaceAll("\\s+", " ");
        String role = ((String) args[3]).trim().replaceAll("\\s+", "").toLowerCase();
        String password = ((String) args[4]).replaceAll("\\s+", "");

        if(password.isEmpty()) {
            throw new IllegalArgumentException("Enter a password");
        }
        String hashedPassword = PasswordHasher.hash(password);

        if (!(InputValidator.isValidName(firstName))) {
            throw new IllegalArgumentException ("Invalid firstname format");
        }
        if (!(InputValidator.isValidName(lastName))) {
            throw new IllegalArgumentException ("Invalid lastname format");
        }
        if (!(InputValidator.isValidEmail(email))) {
            throw new IllegalArgumentException ("Invalid email format");
        }
        if (role.isEmpty() || (!role.equals("admin") && !role.equals("user"))) {
            throw new IllegalArgumentException ("Use 'admin' or 'user'");
        }

        return new model.User(idCounter.getAndIncrement(), firstName, lastName, email, role, hashedPassword);

    }
}
