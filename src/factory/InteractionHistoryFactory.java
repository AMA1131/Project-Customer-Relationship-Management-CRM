package factory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import model.Entity;
import model.composite_interaction.InteractionHistory;
import service.InteractionHistoryService;
import utils.Logger;

public class InteractionHistoryFactory extends AbstractEntityFactory{
    private static final AtomicInteger idCounter = new AtomicInteger(initIdCounter());

    private static int initIdCounter() {
        int maxId = 0;
        ArrayList<InteractionHistory> interactionHistories = InteractionHistoryService.getInstance().getInteractionshistories();
        if (!interactionHistories.isEmpty()) {
            for (InteractionHistory ih : interactionHistories) {
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
        Logger.log("Created Interaction history " + title);
        return new InteractionHistory(idCounter.getAndIncrement(), title, clientId);
    }
}
