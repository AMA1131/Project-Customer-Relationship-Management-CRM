package service;

import java.io.IOException;
import java.util.ArrayList;
import factory.InteractionHistoryFactory;
import model.GenerictIterator;
import model.composite_interaction.InteractionHistory;
import utils.InteractionHistoryFileHandler;
import utils.Logger;

/**
 * Singleton pattern
 */
public class InteractionHistoryService {
    //unique instance
    private static InteractionHistoryService instance = null;

    private final ArrayList<InteractionHistory> interactionHistories;

    // private constructor
    private InteractionHistoryService() throws  IOException{
        interactionHistories = InteractionHistoryFileHandler.loadInteractionHistories();
    }

    public static InteractionHistoryService getInstance() {
        if (instance == null) {
            try{
                instance = new InteractionHistoryService();
            } catch (IOException e) {
                Logger.log("From InteractionHistoryService instanciation, An error occured during the reading Interaction history file: " + e.getMessage());
                throw new RuntimeException("Critical IO error, please contact Admin", e);
            }
        }

        return instance;
    }

    public boolean isexisting(int clientId) {
        for (InteractionHistory interactionHistory : interactionHistories) {
            if (interactionHistory.getClientId() == clientId) return true;
        }
        return false;
    }

    public ArrayList<InteractionHistory> getInteractionshistories() {
        return interactionHistories;
    }

    public boolean isEmpty() {
        return interactionHistories.isEmpty();
    }

    public void createInteractionHistory(String title, int clientId) throws IOException{
        // Check if an interaction history already exists for this client
        if (isexisting(clientId)) {
            Logger.log("An interaction History already exists for this client");
                return;
        }

        // At this point, it means interation history doesn't exists for this client so we create a new one.
        InteractionHistoryFactory interactionHistoryFactory = new InteractionHistoryFactory();
        InteractionHistory rootInteractionHistory = (InteractionHistory) interactionHistoryFactory.createEntity(title, clientId);

        // Create 3 sub-history by default for the 3 types of interactions: meeting, mail & phone
        InteractionHistory callHistory = (InteractionHistory) interactionHistoryFactory.createEntity("Call", clientId);
        InteractionHistory meetingHistory = (InteractionHistory) interactionHistoryFactory.createEntity("Meeting", clientId);
        InteractionHistory emailHistory = (InteractionHistory) interactionHistoryFactory.createEntity("Email", clientId);
        //Add subhistories to root
        rootInteractionHistory.addInteraction(callHistory);
        rootInteractionHistory.addInteraction(meetingHistory);
        rootInteractionHistory.addInteraction(emailHistory);
        // udapte the list of interaction histories
        interactionHistories.add(rootInteractionHistory);
        Logger.log("nombre d'element dans l'historique" + interactionHistories.size());
        //Add the new root history to file
        new Thread(() -> {
            try{
                InteractionHistoryFileHandler.addToDb(rootInteractionHistory);
                Logger.log(" InteractionHistory created with wub-histories for the client " + clientId);
            } catch (IOException e) {
                Logger.log("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        }).start();
    }

    public void removeInteractionHistory(int targetId) throws IOException{
        GenerictIterator<InteractionHistory> iterator = new GenerictIterator<>(interactionHistories);
        while (iterator.hasNext()) {
            InteractionHistory history = iterator.next();
            int id = history.getClientId();
            if (id == targetId) {
                try {
                    iterator.remove();

                    new Thread(() -> {
                        try{
                            InteractionHistoryFileHandler.saveInteractionHistories(interactionHistories);
                        } catch (IOException e) {
                            Logger.log("IO error during client saving to database: " + e.getMessage());
                            System.err.println("❌ An error occured during client saving");
                        }
                    }).start();

                } catch (IllegalStateException e) {
                    Logger.log("Error:" + e);
                }
            }
        }
    }

    public void clearHistory() throws IOException {
        interactionHistories.clear();
        new Thread(() -> {
            try{
                InteractionHistoryFileHandler.saveInteractionHistories(interactionHistories);
            } catch (IOException e) {
                Logger.log("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        }).start();

    }

    public InteractionHistory getInteractionshistoryById(int clientId) {
        for (InteractionHistory interactionHistory : interactionHistories) {
            if (interactionHistory.getClientId() == clientId) {
                return interactionHistory;
            }
        }
        /*Logger.log("No interaction history found for client ID: " + clientId);*/
        return null;
    }

}
