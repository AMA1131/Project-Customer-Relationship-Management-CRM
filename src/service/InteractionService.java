package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import factory.InteractionFactory;
import factory.InteractionHistoryFactory;
import model.Client;
import utils.ClientFileHandler;
import utils.InteractionHistoryFileHandler;
import utils.Logger;
import model.GenerictIterator;
import model.composite_interaction.Interaction;
import model.composite_interaction.InteractionComponent;
import model.composite_interaction.InteractionHistory;
import model.composite_interaction.InteractionHistoryIterator;

public class InteractionService {
    private static InteractionService uniqueInstance = null;
    private final ArrayList<Interaction> interactions;
    private final InteractionHistoryService interactionHistoryService = InteractionHistoryService.getInstance();
    private final ClientService clientservice = ClientService.getUniqueInstance();

    private InteractionService() {
        ArrayList<InteractionHistory> roots = interactionHistoryService.getInteractionshistories();
        if (roots.isEmpty()) {
            interactions = new ArrayList<>();
            Logger.log("No interaction found");
        } else {
            interactions = new ArrayList<>();
            for (InteractionHistory root : roots) {
                InteractionHistoryIterator iterator = new InteractionHistoryIterator(root);
                while (iterator.hasNext()) {
                    try {
                        Interaction interaction = iterator.next();
                        interactions.add(interaction);
                    } catch (NoSuchElementException e) {
                        Logger.log("Alert InteractionHistoryIterator: " + e);
                    }
                    
                }
            }
        }
    }

    public static InteractionService getUniqueInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new InteractionService();
        }
        return uniqueInstance;
    }

    public ArrayList<Interaction> getInteractions() {
        return interactions;
    }

    public void clearInteractions() {
        interactions.clear();
    }

    public Interaction getInteractionById(int interactionId) {
        for (Interaction interaction : interactions) {
            if (interaction.getId() == interactionId) {
                return interaction;
            }
        }
        Logger.log("Alert InteractionService: Interaction not found in interactions list.");
        return null;
    }

    public int findClientFromInteraction(int interactionId) {
        for (Interaction interaction : interactions) {
            if (interaction.getId() == interactionId) {
                return interaction.getClientId();
            }
        }
        return -1;
    }
    /**
     * Returns the interactions list of a specific client if available or null 
     * @param clientId the id of the client
     * @return a list of interactions
     */
    public ArrayList<Interaction> getInteractionsByClientId(int clientId) {
        InteractionHistory clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);
        ArrayList<Interaction> clientInteractions = new ArrayList<>();
        if (clientRoot != null) {
            InteractionHistoryIterator iterator = new InteractionHistoryIterator(clientRoot);
            while (iterator.hasNext()) {
                try {
                    Interaction interaction = iterator.next();
                    clientInteractions.add(interaction);
                } catch (NoSuchElementException e) {
                    Logger.log("Info: " + e);
                }
            }
        }
        return clientInteractions;
    }

    public void addInteractionToSubHistory(String type /*meeting, call, email */, int userId,int clientId, String description) throws IOException {
        type = type.trim().replaceAll("\\s+", "").toLowerCase();
        if ( !clientservice.doesClientExist(clientId)) {
            throw new IllegalArgumentException("Client ID does not exist in database. Please create it and retry.");
        }
        //Create factory for interaction
        InteractionFactory factory = new InteractionFactory();
        InteractionHistoryFactory factory2 = new InteractionHistoryFactory();


        // Create the interaction
        Interaction newInteraction = (Interaction) factory.createEntity(type, userId, clientId, description);
        // Get the client history from DB
        InteractionHistory clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);

        // Create a new client history if client root is null
        if (clientRoot == null) {
            /*Logger.log("Client root NULL");*/
            interactionHistoryService.createInteractionHistory("History for client #" + clientId, clientId);
            clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);
        }
        /*Logger.log("not here");*/

        // Search history folder that match interaction type
        InteractionHistory subHistory = null;
        //iterate over the 3 interaction types folder to find the matching one with the new interaction to add
        ArrayList<InteractionComponent> subComponents = clientRoot.getInteractions();
        for (InteractionComponent subcomponent : subComponents){
            InteractionHistory history = (InteractionHistory) subcomponent;
            if (history.getTitle().equalsIgnoreCase(type)) {
                subHistory = history;
                /*Logger.log("I'm in");*/
                break;
            }
        }

        // in case subhistory was deleted by someone after the creation of a root, create a new subhistory and add it to root history
        if (subHistory == null) {
            subHistory = (InteractionHistory) factory2.createEntity(type, clientId);
            clientRoot.addInteraction(subHistory);
        }

        subHistory.addInteraction(newInteraction);
        Logger.log(subHistory.getTitle() + "subhistory updated with the new interaction" + newInteraction);

        new Thread(() -> {
            try{
                InteractionHistoryFileHandler.saveInteractionHistories(interactionHistoryService.getInteractionshistories());
                Logger.log("Saved in file");
            } catch (IOException e) {
                Logger.log("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        }).start();

        interactions.add(newInteraction);
        Logger.log("Interaction added in sub-history '" + type.trim().replaceAll("\\s+", "").toLowerCase() + "' of client #" + clientId);
    }

    public boolean removeInteraction(int interactionId) throws IOException{
         // get clientId from interaction ID
        int clientId = findClientFromInteraction(interactionId);

        if (clientId <= 0) {
            Logger.log("No such Interaction history for client #" + interactionId);
            return false;
        }
        Logger.log("CLientId found");
        // get the root history of the client from the the global list of the instance
        InteractionHistory clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);

        // iterate over the 3 subhistories (type meeting, call & email) to search the interaction to remove
        for (InteractionComponent component : clientRoot.getInteractions()) {
            // iterate over one of the subcategories
            InteractionHistory subHistory = (InteractionHistory) component;
            GenerictIterator<InteractionComponent> iterator = new GenerictIterator<>(subHistory.getInteractions());
            while (iterator.hasNext()) {
                InteractionComponent subComponent = iterator.next();
                Interaction interaction = (Interaction) subComponent;
                // delete the interaction from the sub history if ids match
                if (interactionId == interaction.getId()) {
                    iterator.remove();
                    interactions.removeIf(inter -> inter.getId() == interactionId);
                    Logger.log("Interctions list udpated and interaction removed");
                }
            }
        }

        new Thread(() -> {
            try{
                InteractionHistoryFileHandler.saveInteractionHistories(interactionHistoryService.getInteractionshistories());
                Logger.log("Saved in file");
            } catch (IOException e) {
                Logger.log("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        }).start();

        Logger.log("Interaction with ID #" + interactionId + " removed and history updated.");
        return true;
    }

    public void updateInteractionDescription(int interactionId, String description) throws IOException{
        // get clientId from interaction ID
        int clientId = findClientFromInteraction(interactionId);
        if (clientId <= 0) {
            Logger.log("No Interaction history for client #" + interactionId);
            return;
        } else {
            for (Interaction interaction : interactions) {
                if (interaction.getId() == interactionId){
                    interaction.setDescription(description);
                }
            }
        }

        // get the root history of the client from the the global list of the instance
        InteractionHistory clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);

        // iterate over the 3 subhistories (type meeting, call & email) to search the interaction to remove
        for (InteractionComponent component : clientRoot.getInteractions()) {
                // iterate over one of the subcategories
                InteractionHistory subHistory = (InteractionHistory) component;
                ArrayList<InteractionComponent> interactions = subHistory.getInteractions();
                for (InteractionComponent component2 : interactions) {
                    Interaction i = (Interaction) component2;
                    if (i.getId() == interactionId) {
                        i.setDescription(description);
                    }
                }
            }

        new Thread(() -> {
            try{
                InteractionHistoryFileHandler.saveInteractionHistories(interactionHistoryService.getInteractionshistories());
                Logger.log("Saved in file");
            } catch (IOException e) {
                Logger.log("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        }).start();

        Logger.log("Interaction with ID #" + interactionId + " updated and saved.");
    }

    public ArrayList<Interaction> searchInteraction(String keyword){
        ArrayList<Interaction> result = new ArrayList<>();
        String sanitizedKeyword = keyword.trim().replaceAll("\\s+", " ").toLowerCase();
        for (Interaction i : interactions) {
            if (i.getDescription().contains(sanitizedKeyword)) {
                result.add(i);
            }
        }
        return result;
    }
}
