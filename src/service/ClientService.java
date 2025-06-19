package service;

import java.io.IOException;
import java.util.ArrayList;
import factory.ClientFactory;
import model.Client;
import model.GenerictIterator;
import utils.ClientFileHandler;
import utils.Logger;

public class ClientService {
    public static ClientService uniqueInstance = null;
    private final ArrayList<Client> clients;

    private ClientService()  throws  IOException{
        this.clients = ClientFileHandler.loadClients();

    }

    public static ClientService getUniqueInstance(){
        if (uniqueInstance == null) {
            try {
                uniqueInstance = new ClientService();
            }catch(IOException e) {
                Logger.log("From ClientService instanciation, An error occured during the reading client file: " + e.getMessage());
                throw new RuntimeException("Critical IO error, please contact Admin", e);
            }
        }
        return uniqueInstance;
    }

    public boolean doesClientExist(int  clientId) {
        for (Client c : clients) {
            if (c.getId() == clientId) {
                return true;
            }
        }
        return false;
    }

    public void addClient(String firstName, String lastName, String email, String phoneNumber, String companyName) throws IOException{
            // create the factory
            ClientFactory clientFactory = new ClientFactory();
            // create the client
            Client client = (Client) clientFactory.createEntity(firstName, lastName, email, phoneNumber, companyName);
            // Add client to the local clients storage
            clients.add(client);
            // Add client to the database
            new Thread(() -> {
                try{
                    ClientFileHandler.addToDb(client);
                } catch (IOException e) {
                    Logger.log("IO error during client saving to database: " + e.getMessage());
                    System.err.println("❌ An error occured during client saving");
                }
            }).start();
            // notify adding state in the log file
            Logger.log("Client added" + client);
    }

    public boolean removeClient (int targetId) throws IOException{
        GenerictIterator<Client> iterator = new GenerictIterator<>(clients);
        while (iterator.hasNext()) {
            Client client = iterator.next(); 
            int id = client.getId();
            if (id == targetId) {
                iterator.remove();
                new Thread(() -> {
                    try{
                        ClientFileHandler.saveClients(clients);
                    } catch (IOException e) {
                        Logger.log("IO error during client saving to database: " + e.getMessage());
                        System.err.println("❌ An error occured during client saving");
                    }
                }).start();

                Logger.log("Client deleted" + client);
                return true;
            }
        }
        Logger.log("Id not found in database");
        return false;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public ArrayList<Client> findClientByName(String firstName) {
        ArrayList<Client> result = new ArrayList<>();
        for(Client client : clients) {
            if (client.getFirstName().equals(firstName.trim().toLowerCase())) {
                result.add(client);
            }
        }

        return result;
    }
}
