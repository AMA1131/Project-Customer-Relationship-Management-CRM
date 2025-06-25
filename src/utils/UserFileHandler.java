package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.User;

public class UserFileHandler {
    private static final String fileName = "data/users.json";
    private static final Gson gson = new Gson();

    public static ArrayList<User> loadUsers() throws IOException{
        ArrayList<User> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
            if (users == null) users = new ArrayList<>();

        } catch (FileNotFoundException e) {
            LogHandler.logWarning("FIle not found." + e);
            /*return users*/;
        }

        return users;
    }

    public static void saveUsers(ArrayList<User> users) throws  IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            gson.toJson(users, writer);
        }
    }

    public static void addToDb(User newUser) throws  IOException{
        ArrayList<User> users = loadUsers();
        users.add(newUser);
        saveUsers(users);
    }

}
