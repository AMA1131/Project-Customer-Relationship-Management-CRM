package service;

import java.io.IOException;
import java.util.ArrayList;
import factory.UserFactory;
import model.GenerictIterator;
import model.User;
import utils.ClientFileHandler;
import utils.PasswordHasher;
import utils.UserFileHandler;
import utils.Logger;

public class UserService {
    private static UserService uniqueInstance = null;
    private final ArrayList<User> users;

    private UserService() throws  IOException{
        this.users = UserFileHandler.loadUsers();
    }

    public static UserService getUniqueInstance(){
        if (uniqueInstance == null) {
            try{
                uniqueInstance = new UserService();
            } catch(IOException e) {
                Logger.log("From Userservice instanciation, an error occured during the reading the user file: " + e.getMessage());
                throw new RuntimeException("Critical IO error, please contact Admin", e);
            }
        }
        return uniqueInstance;
    }

    public void addUser(String firstName, String lastName, String email, String role, String password) throws IOException{
        // create the factory
        UserFactory userFactory = new UserFactory();
        // create the user
        User user = (User) userFactory.createEntity(firstName, lastName, email, role, password);
        // Add user to the local user storage
        users.add(user);
        // Add user to the database
        new Thread(() -> {
            try{
                UserFileHandler.addToDb(user);
            } catch (IOException e) {
                Logger.log("IO error during saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during saving");
            }
        }).start();

        // notify adding state in the log file
        Logger.log("User added" + user);
    }

    public boolean removeUser (int targetId) throws IOException {
        GenerictIterator<User> iterator = new GenerictIterator<>(users);
        while (iterator.hasNext()) {
            User user = iterator.next(); 
            int id = user.getId();
            if (id == targetId) {
                iterator.remove();
                new Thread(() -> {
                    try{
                        UserFileHandler.saveUsers(users);
                    } catch (IOException e) {
                        Logger.log("IO error during saving to database: " + e.getMessage());
                        System.err.println("❌ An error occured during saving");
                    }
                }).start();

                Logger.log("User deleted" + user);
                return true;
            }
        }
        Logger.log("Id to remove not found in database");
        return false;
    }

    public ArrayList<User> getusers() {
        return users;
    }

    public User aunticateUser(String email, String password) {
        String hashedpassword = PasswordHasher.hash(password);
        if (users.isEmpty()) return null;
        for(User u : users) {
            if ((u.getEmail().equals(email)) && (u.getHashedPassword().equals(hashedpassword))) return u;
        }

        return null;
    }

    public ArrayList<User> findUserByName(String firstName) {
        ArrayList<User> result = new ArrayList<>();
        for(User user : users) {
            if (user.getFirstName().equals(firstName.trim().toLowerCase().replaceAll("\\s+", " "))) {
                result.add(user);
            }
        }

        return result;
    }
}
