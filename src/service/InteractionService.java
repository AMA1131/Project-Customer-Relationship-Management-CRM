package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import factory.Interaction;
import factory.InteractionHistory;
import utils.InteractionHistoryFileHandler;
import utils.LogHandler;
import model.GenerictIterator;
import model.composite_interaction.InteractionComponent;
import model.composite_interaction.InteractionHistoryIterator;

public class InteractionService {
    private static InteractionService uniqueInstance = null;
    public static boolean isTesting = false;
    private final ArrayList<model.composite_interaction.Interaction> interactions;
    private final InteractionHistoryService interactionHistoryService = InteractionHistoryService.getInstance();
    private final ClientService clientservice = ClientService.getUniqueInstance();

    private InteractionService() {
        ArrayList<model.composite_interaction.InteractionHistory> roots = interactionHistoryService.getInteractionshistories();
        if (roots.isEmpty()) {
            interactions = new ArrayList<>();
            LogHandler.logWarning("No interaction found");
        } else {
            interactions = new ArrayList<>();
            for (model.composite_interaction.InteractionHistory root : roots) {
                InteractionHistoryIterator iterator = new InteractionHistoryIterator(root);
                while (iterator.hasNext()) {
                    try {
                        model.composite_interaction.Interaction interaction = iterator.next();
                        interactions.add(interaction);
                    } catch (NoSuchElementException e) {
                        LogHandler.logDebug("Alert: InteractionHistoryIterator: " + e);
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

    public ArrayList<model.composite_interaction.Interaction> getInteractions() {
        return interactions;
    }

    public void clearInteractions() {
        interactions.clear();
    }

    public model.composite_interaction.Interaction getInteractionById(int interactionId) {
        for (model.composite_interaction.Interaction interaction : interactions) {
            if (interaction.getId() == interactionId) {
                return interaction;
            }
        }
        LogHandler.logWarning("Alert InteractionService: Interaction not found in interactions list.");
        return null;
    }

    public int findClientFromInteraction(int interactionId) {
        for (model.composite_interaction.Interaction interaction : interactions) {
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
    public ArrayList<model.composite_interaction.Interaction> getInteractionsByClientId(int clientId) {
        model.composite_interaction.InteractionHistory clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);
        ArrayList<model.composite_interaction.Interaction> clientInteractions = new ArrayList<>();
        if (clientRoot != null) {
            InteractionHistoryIterator iterator = new InteractionHistoryIterator(clientRoot);
            while (iterator.hasNext()) {
                try {
                    model.composite_interaction.Interaction interaction = iterator.next();
                    clientInteractions.add(interaction);
                } catch (NoSuchElementException e) {
                    LogHandler.logDebug("Info: " + e);
                }
            }
        }
        return clientInteractions;
    }

    public void addInteractionToSubHistory(String type /*meeting, call, email */, int userId,int clientId, String description) throws IOException {
        type = type.trim().replaceAll("\\s+", "").toLowerCase();
        if ( !isTesting && !clientservice.doesClientExist(clientId)) {
            throw new IllegalArgumentException("Client ID does not exist in database. Please create it and retry.");
        }
        //Create factory for interaction
        Interaction factory = new Interaction();
        InteractionHistory factory2 = new InteractionHistory();


        // Create the interaction
        model.composite_interaction.Interaction newInteraction = (model.composite_interaction.Interaction) factory.createEntity(type, userId, clientId, description);
        // Get the client history from DB
        model.composite_interaction.InteractionHistory clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);

        // Create a new client history if client root is null
        if (clientRoot == null) {
            LogHandler.logWarning("Client root NULL");
            interactionHistoryService.createInteractionHistory("History for client #" + clientId, clientId);
            clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);
        }
        /*Logger.log("not here");*/

        // Search history folder that match interaction type
        model.composite_interaction.InteractionHistory subHistory = null;
        //iterate over the 3 interaction types folder to find the matching one with the new interaction to add
        ArrayList<InteractionComponent> subComponents = clientRoot.getInteractions();
        for (InteractionComponent subcomponent : subComponents){
            model.composite_interaction.InteractionHistory history = (model.composite_interaction.InteractionHistory) subcomponent;
            if (history.getTitle().equalsIgnoreCase(type)) {
                subHistory = history;
                /*Logger.log("I'm in");*/
                break;
            }
        }

        // in case subhistory was deleted by someone after the creation of a root, create a new subhistory and add it to root history
        if (subHistory == null) {
            subHistory = (model.composite_interaction.InteractionHistory) factory2.createEntity(type, clientId);
            clientRoot.addInteraction(subHistory);
        }

        subHistory.addInteraction(newInteraction);
        LogHandler.logDebug(subHistory.getTitle() + "subhistory updated with the new interaction" + newInteraction);

        if (isTesting) {
            try{
                InteractionHistoryFileHandler.saveInteractionHistories(interactionHistoryService.getInteractionshistories());
                LogHandler.logDebug("Saved in file");
            } catch (IOException e) {
                LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        } else{
            new Thread(() -> {
                try{
                    InteractionHistoryFileHandler.saveInteractionHistories(interactionHistoryService.getInteractionshistories());
                    LogHandler.logDebug("Saved in file");
                } catch (IOException e) {
                    LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                    System.err.println("❌ An error occured during client saving");
                }
            }).start();
        }


        interactions.add(newInteraction);
        LogHandler.logDebug("Interaction added in sub-history '" + type.trim().replaceAll("\\s+", "").toLowerCase() + "' of client #" + clientId);
    }

    public boolean removeInteraction(int interactionId) throws IOException{
         // get clientId from interaction ID
        int clientId = findClientFromInteraction(interactionId);

        if (clientId <= 0) {
            LogHandler.logWarning("No such Interaction history for client #" + interactionId);
            return false;
        }
        LogHandler.logDebug("CLientId found");
        // get the root history of the client from the the global list of the instance
        model.composite_interaction.InteractionHistory clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);

        // iterate over the 3 subhistories (type meeting, call & email) to search the interaction to remove
        for (InteractionComponent component : clientRoot.getInteractions()) {
            // iterate over one of the subcategories
            model.composite_interaction.InteractionHistory subHistory = (model.composite_interaction.InteractionHistory) component;
            GenerictIterator<InteractionComponent> iterator = new GenerictIterator<>(subHistory.getInteractions());
            while (iterator.hasNext()) {
                InteractionComponent subComponent = iterator.next();
                model.composite_interaction.Interaction interaction = (model.composite_interaction.Interaction) subComponent;
                // delete the interaction from the sub history if ids match
                if (interactionId == interaction.getId()) {
                    iterator.remove();
                    interactions.removeIf(inter -> inter.getId() == interactionId);
                    LogHandler.logInfo("Interactions list udpated and interaction removed");
                }
            }
        }

        if (isTesting) {
            try{
                InteractionHistoryFileHandler.saveInteractionHistories(interactionHistoryService.getInteractionshistories());
                LogHandler.logDebug("Saved in file");
            } catch (IOException e) {
                LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        } else{
            new Thread(() -> {
                try{
                    InteractionHistoryFileHandler.saveInteractionHistories(interactionHistoryService.getInteractionshistories());
                    LogHandler.logDebug("Saved in file");
                } catch (IOException e) {
                    LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                    System.err.println("❌ An error occured during client saving");
                }
            }).start();
        }

        LogHandler.logInfo("Interaction with ID #" + interactionId + " removed and history updated.");
        return true;
    }

    public void updateInteractionDescription(int interactionId, String description) throws IOException{
        // get clientId from interaction ID
        int clientId = findClientFromInteraction(interactionId);
        if (clientId <= 0) {
            LogHandler.logWarning("No Interaction history for client #" + interactionId);
            return;
        } else {
            for (model.composite_interaction.Interaction interaction : interactions) {
                if (interaction.getId() == interactionId){
                    interaction.setDescription(description);
                }
            }
        }

        // get the root history of the client from the the global list of the instance
        model.composite_interaction.InteractionHistory clientRoot = interactionHistoryService.getInteractionshistoryById(clientId);

        // iterate over the 3 subhistories (type meeting, call & email) to search the interaction to remove
        for (InteractionComponent component : clientRoot.getInteractions()) {
                // iterate over one of the subcategories
                model.composite_interaction.InteractionHistory subHistory = (model.composite_interaction.InteractionHistory) component;
                ArrayList<InteractionComponent> interactions = subHistory.getInteractions();
                for (InteractionComponent component2 : interactions) {
                    model.composite_interaction.Interaction i = (model.composite_interaction.Interaction) component2;
                    if (i.getId() == interactionId) {
                        i.setDescription(description);
                    }
                }
            }

        new Thread(() -> {
            try{
                InteractionHistoryFileHandler.saveInteractionHistories(interactionHistoryService.getInteractionshistories());
                LogHandler.logDebug("Saved in file");
            } catch (IOException e) {
                LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        }).start();

        LogHandler.logInfo("Interaction with ID #" + interactionId + " updated and saved.");
    }

    public ArrayList<model.composite_interaction.Interaction> searchInteraction(String keyword){
        ArrayList<model.composite_interaction.Interaction> result = new ArrayList<>();
        String sanitizedKeyword = keyword.trim().replaceAll("\\s+", " ").toLowerCase();
        for (model.composite_interaction.Interaction i : interactions) {
            if (i.getDescription().contains(sanitizedKeyword)) {
                result.add(i);
            }
        }
        return result;
    }
}
