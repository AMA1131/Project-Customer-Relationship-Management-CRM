package service;

import java.io.IOException;
import java.util.ArrayList;
import factory.InteractionHistory;
import model.GenerictIterator;
import utils.InteractionHistoryFileHandler;
import utils.LogHandler;

/**
 * Singleton pattern
 */
public class InteractionHistoryService {
    //unique instance
    private static InteractionHistoryService instance = null;

    private final ArrayList<model.composite_interaction.InteractionHistory> interactionHistories;

    // private constructor
    private InteractionHistoryService() throws  IOException{
        interactionHistories = InteractionHistoryFileHandler.loadInteractionHistories();
    }

    public static InteractionHistoryService getInstance() {
        if (instance == null) {
            try{
                instance = new InteractionHistoryService();
            } catch (IOException e) {
                LogHandler.logError("From InteractionHistoryService instanciation, An error occured during the reading Interaction history file: " + e.getMessage());
                throw new RuntimeException("Critical IO error, please contact Admin", e);
            }
        }

        return instance;
    }

    public boolean isexisting(int clientId) {
        for (model.composite_interaction.InteractionHistory interactionHistory : interactionHistories) {
            if (interactionHistory.getClientId() == clientId) return true;
        }
        return false;
    }

    public ArrayList<model.composite_interaction.InteractionHistory> getInteractionshistories() {
        return interactionHistories;
    }

    public boolean isEmpty() {
        return interactionHistories.isEmpty();
    }

    public void createInteractionHistory(String title, int clientId) throws IOException{
        // Check if an interaction history already exists for this client
        if (isexisting(clientId)) {
            LogHandler.logDebug("An interaction History already exists for this client");
                return;
        }

        // At this point, it means interation history doesn't exists for this client so we create a new one.
        InteractionHistory interactionHistoryFactory = new InteractionHistory();
        model.composite_interaction.InteractionHistory rootInteractionHistory = (model.composite_interaction.InteractionHistory) interactionHistoryFactory.createEntity(title, clientId);

        // Create 3 sub-history by default for the 3 types of interactions: meeting, mail & phone
        model.composite_interaction.InteractionHistory callHistory = (model.composite_interaction.InteractionHistory) interactionHistoryFactory.createEntity("Call", clientId);
        model.composite_interaction.InteractionHistory meetingHistory = (model.composite_interaction.InteractionHistory) interactionHistoryFactory.createEntity("Meeting", clientId);
        model.composite_interaction.InteractionHistory emailHistory = (model.composite_interaction.InteractionHistory) interactionHistoryFactory.createEntity("Email", clientId);
        //Add subhistories to root
        rootInteractionHistory.addInteraction(callHistory);
        rootInteractionHistory.addInteraction(meetingHistory);
        rootInteractionHistory.addInteraction(emailHistory);
        // udapte the list of interaction histories
        interactionHistories.add(rootInteractionHistory);
        LogHandler.logDebug("number of elements in history" + interactionHistories.size());
        //Add the new root history to file
        new Thread(() -> {
            try{
                InteractionHistoryFileHandler.addToDb(rootInteractionHistory);
                LogHandler.logDebug(" InteractionHistory created with wub-histories for the client " + clientId);
            } catch (IOException e) {
                LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        }).start();
    }

    public void removeInteractionHistory(int targetId) throws IOException{
        GenerictIterator<model.composite_interaction.InteractionHistory> iterator = new GenerictIterator<>(interactionHistories);
        while (iterator.hasNext()) {
            model.composite_interaction.InteractionHistory history = iterator.next();
            int id = history.getClientId();
            if (id == targetId) {
                try {
                    iterator.remove();

                    new Thread(() -> {
                        try{
                            InteractionHistoryFileHandler.saveInteractionHistories(interactionHistories);
                        } catch (IOException e) {
                            LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                            System.err.println("❌ An error occured during client saving");
                        }
                    }).start();

                } catch (IllegalStateException e) {
                    LogHandler.logWarning("Error:" + e);
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
                LogHandler.logError("IO error during client saving to database: " + e.getMessage());
                System.err.println("❌ An error occured during client saving");
            }
        }).start();

    }

    public model.composite_interaction.InteractionHistory getInteractionshistoryById(int clientId) {
        for (model.composite_interaction.InteractionHistory interactionHistory : interactionHistories) {
            if (interactionHistory.getClientId() == clientId) {
                return interactionHistory;
            }
        }
        /*Logger.log("No interaction history found for client ID: " + clientId);*/
        return null;
    }

}
