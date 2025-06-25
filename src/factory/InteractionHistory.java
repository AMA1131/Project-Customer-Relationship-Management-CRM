package factory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import model.Entity;
import service.InteractionHistoryService;
import utils.LogHandler;

public class InteractionHistory extends AbstractEntity {
    private static final AtomicInteger idCounter = new AtomicInteger(initIdCounter());

    private static int initIdCounter() {
        int maxId = 0;
        ArrayList<model.composite_interaction.InteractionHistory> interactionHistories = InteractionHistoryService.getInstance().getInteractionshistories();
        if (!interactionHistories.isEmpty()) {
            for (model.composite_interaction.InteractionHistory ih : interactionHistories) {
                if (ih.getId() > maxId) {
                    maxId = ih.getId();
                }
            }
        }
        return maxId + 1;
    }

    @Override
    public Entity createEntity(Object... args){
        if (args.length < 2) {
            throw new IllegalArgumentException("Insufficient parameters to create a Client");
        }
        String title = ((String) args[0]).trim().replaceAll("\\s+", " ").toLowerCase();
        int clientId = (int) args[1];

        if (title.isEmpty()) {
            throw new IllegalArgumentException ("Invalid name format");
        }
        if (clientId < 1) {
            throw new IllegalArgumentException("Client ID must be positive integers");
        }
        LogHandler.logInfo("Created Interaction history " + title);
        return new model.composite_interaction.InteractionHistory(idCounter.getAndIncrement(), title, clientId);
    }
}
