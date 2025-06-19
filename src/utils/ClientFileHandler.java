package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson; // biblioteca google converts java objects in JSON and vice versa.
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import model.Client;

public class ClientFileHandler {
    private static final String fileName = "data/clients.json";
    private static final Gson gson = new Gson();

    public static ArrayList<Client> loadClients() throws IOException {
        ArrayList<Client> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // gson does't recognize ArrayList<Client> so i tell him the type it musts read in the file
            Type clientListType = new TypeToken<ArrayList<Client>>() {}.getType();
            clients = gson.fromJson(reader, clientListType);

            if (clients == null) clients = new ArrayList<>();
        } catch (FileNotFoundException e) {
            Logger.log("File not found : " + e.getMessage());
        }
        return  clients;
    }

    public static void saveClients(ArrayList<Client> clients) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            gson.toJson(clients, writer);
        } /*catch (IOException e) {
            Logger.log("Error while saving client file: " + e.getMessage());
        }*/
    }

    public static void addToDb(Client newClient) throws IOException{
        ArrayList<Client> clients = loadClients();
        clients.add(newClient);
        saveClients(clients);
    }
}
