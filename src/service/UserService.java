package service;

import java.io.IOException;
import java.util.ArrayList;
import factory.User;
import model.GenerictIterator;
import utils.ClientFileHandler;
import utils.PasswordHasher;
import utils.UserFileHandler;
import utils.LogHandler;

public class UserService {
    private static UserService uniqueInstance = null;
    public static boolean isTesting = false;
    private final ArrayList<model.User> users;

    private UserService() throws  IOException{
        this.users = UserFileHandler.loadUsers();
    }

    public static UserService getUniqueInstance(){
        if (uniqueInstance == null) {
            try{
                uniqueInstance = new UserService();
            } catch(IOException e) {
                LogHandler.logError("From Userservice instanciation, an error occured during the reading the user file: " + e.getMessage());
                throw new RuntimeException("Critical IO error, please contact Admin", e);
            }
        }
        return uniqueInstance;
    }

    public boolean doesUserExist(int  userId) {
        for (model.User u : users) {
            if (u.getId() == userId) {
                return true;
            }
        }
        return false;
    }

    public void addUser(String firstName, String lastName, String email, String role, String password) throws IOException{
        // create the factory
        User userFactory = new User();
        // create the user
        model.User user = (model.User) userFactory.createEntity(firstName, lastName, email, role, password);
        // Add user to the local user storage
        users.add(user);
        // Add user to the database
        if(isTesting) {
                UserFileHandler.addToDb(user);
        } else{
            new Thread(() -> {
                try{
                    UserFileHandler.addToDb(user);
                } catch (IOException e) {
                    LogHandler.logError("IO error during saving to database: " + e.getMessage());
                    System.err.println("❌ An error occured during saving");
                }
            }).start();
        }

        // notify adding state in the log file
        LogHandler.logInfo("User added" + user);
    }

    public boolean removeUser (int targetId) throws IOException {
        GenerictIterator<model.User> iterator = new GenerictIterator<>(users);
        while (iterator.hasNext()) {
            model.User user = iterator.next();
            int id = user.getId();
            if (id == targetId) {
                iterator.remove();
                if (isTesting) {
                    UserFileHandler.saveUsers(users);
                } else {
                    new Thread(() -> {
                        try{
                            UserFileHandler.saveUsers(users);
                        } catch (IOException e) {
                            LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                            System.err.println("❌ An error occured during client saving");
                        }
                    }).start();
                }

                LogHandler.logInfo("Client deleted" + user);
                return true;
            }
        }
        LogHandler.logInfo("Id not found in database");
        return false;
    }

    public ArrayList<model.User> getusers() {
        return users;
    }

    public boolean getIsTesting() {
        return isTesting;
    }


    public model.User aunticateUser(String email, String password) {
        String hashedpassword = PasswordHasher.hash(password);
        if (users.isEmpty()) return null;
        for(model.User u : users) {
            if ((u.getEmail().equals(email)) && (u.getHashedPassword().equals(hashedpassword))) return u;
        }

        return null;
    }

    public ArrayList<model.User> findUserByName(String firstName) {
        ArrayList<model.User> result = new ArrayList<>();
        for(model.User user : users) {
            if (user.getFirstName().equals(firstName.trim().toLowerCase().replaceAll("\\s+", " "))) {
                result.add(user);
            }
        }

        return result;
    }
}
