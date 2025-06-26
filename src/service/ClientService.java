package service;

import java.io.IOException;
import java.util.ArrayList;
import factory.Client;
import model.GenerictIterator;
import utils.ClientFileHandler;
import utils.LogHandler;

public class ClientService {
    private static ClientService uniqueInstance = null;
    public static boolean isTesting = false;
    private final ArrayList<model.Client> clients;

    private ClientService()  throws  IOException{
        this.clients = ClientFileHandler.loadClients();

    }

    public static ClientService getUniqueInstance(){
        if (uniqueInstance == null) {
            try {
                uniqueInstance = new ClientService();
            }catch(IOException e) {
                LogHandler.logError("From ClientService instanciation, An error occured during the reading client file: " + e.getMessage());
                throw new RuntimeException("Critical IO error, please contact Admin", e);
            }
        }
        return uniqueInstance;
    }

    public boolean doesClientExist(int  clientId) {
        for (model.Client c : clients) {
            if (c.getId() == clientId) {
                return true;
            }
        }
        return false;
    }

    public void addClient(String firstName, String lastName, String email, String phoneNumber, String companyName) throws IOException{
            // create the factory
            Client clientFactory = new Client();
            // create the client
            model.Client client = (model.Client) clientFactory.createEntity(firstName, lastName, email, phoneNumber, companyName);
            // Add client to the local clients storage
            clients.add(client);
            // Add client to the database
            if (isTesting) {
                try{
                    ClientFileHandler.addToDb(client);
                } catch (IOException e) {
                    LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                    System.err.println("❌ An error occured during client saving");
                }
            } else {
                new Thread(() -> {
                    try{
                        ClientFileHandler.addToDb(client);
                    } catch (IOException e) {
                        LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                        System.err.println("❌ An error occured during client saving");
                    }
                }).start();
                // notify adding state in the log file
            }
            LogHandler.logInfo("Client added" + client);
    }

    public boolean removeClient (int targetId) throws IOException{
        GenerictIterator<model.Client> iterator = new GenerictIterator<>(clients);
        while (iterator.hasNext()) {
            model.Client client = iterator.next();
            int id = client.getId();
            if (id == targetId) {
                iterator.remove();
                if (isTesting) {
                        ClientFileHandler.saveClients(clients);
                } else {
                    new Thread(() -> {
                        try{
                            ClientFileHandler.saveClients(clients);
                        } catch (IOException e) {
                            LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                            System.err.println("❌ An error occured during client saving");
                        }
                    }).start();
                }


                LogHandler.logInfo("Client deleted" + client);
                return true;
            }
        }
        LogHandler.logInfo("Id not found in database");
        return false;
    }

    public ArrayList<model.Client> getClients() {
        return clients;
    }

    public ArrayList<model.Client> findClientByName(String firstName) {
        ArrayList<model.Client> result = new ArrayList<>();
        for(model.Client client : clients) {
            if (client.getFirstName().equals(firstName.trim().toLowerCase())) {
                result.add(client);
            }
        }

        return result;
    }
}
