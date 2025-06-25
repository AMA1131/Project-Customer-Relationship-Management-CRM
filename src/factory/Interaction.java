package factory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import model.Entity;
import service.InteractionService;

public class Interaction extends AbstractEntity {
    
    private static final AtomicInteger idcounter = new AtomicInteger(initIdCounter());

    private static int initIdCounter() {
        int maxId = 0;
        ArrayList<model.composite_interaction.Interaction> interactions = InteractionService.getUniqueInstance().getInteractions();
        if (!interactions.isEmpty()) {
            for (model.composite_interaction.Interaction i : interactions) {
                if (i.getId() > maxId) {
                    maxId = i.getId();
                }
            }
        }
        return maxId + 1;
    }

    @Override
    public Entity createEntity(Object... args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("Insufficient parameters to create an interaction");
        }

        String type = ((String) args[0]).trim().replaceAll("\\s+", "").toLowerCase();
        int userId = (int) args[1];
        int clientId = (int) args[2];
        String description = ((String) args[3]).trim().replaceAll("\\s+", " ").toLowerCase();

        if (type.isEmpty() || (!type.equals("email") && !type.equals("call") && !type.equals("meeting"))) {
            throw new IllegalArgumentException("Invalid interaction type. Use: email, call, or meeting");
        }
        if (userId < 1 || clientId < 1) {
            throw new IllegalArgumentException("User ID and Client ID must be positive integers");
        }

        return new model.composite_interaction.Interaction(idcounter.getAndIncrement(), type, userId, description, clientId);
    }
}
