package test;

import model.composite_interaction.InteractionComponent;
import model.composite_interaction.InteractionHistory;
import service.ClientService;
import service.InteractionHistoryService;
import service.InteractionService;

import org.junit.Before;
import org.junit.Test;
import model.composite_interaction.Interaction;
import utils.LogHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class InteractionServiceTest {
    private final Path historyPath = Paths.get("data/interactionsHistory.json");
    private final Path loggerPath = Paths.get("data/crm.log");

    @Before
    public void setup() {
        try {
            Files.deleteIfExists(historyPath);

            Field instance = InteractionHistoryService.class.getDeclaredField("instance");
            Field instance2 = InteractionService.class.getDeclaredField("uniqueInstance");
            Field instance3 = ClientService.class.getDeclaredField("uniqueInstance");

            instance.setAccessible(true);
            instance2.setAccessible(true);
            instance3.setAccessible(true);

            instance.set(null, null);
            instance2.set(null, null);
            instance3.set(null, null);
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            LogHandler.logError("Error during test setup: " + e);
        }

        InteractionService.isTesting = true;
        InteractionHistoryService.isTesting = true;
        ClientService.isTesting = true;
    }

    @Test
    public void testCreateInteraction() {
        try {
            InteractionHistoryService interactionHistoryService = InteractionHistoryService.getInstance();
            LogHandler.logInfo("First");
            InteractionService interactionService = InteractionService.getUniqueInstance();

            interactionService.addInteractionToSubHistory("EMAIL", 1, 1, "Robot    Failing");
            interactionService.addInteractionToSubHistory("EM   AIL", 1, 1, "Robot    Failing");
            interactionService.addInteractionToSubHistory("Call", 1, 1, "Robot    Failing");
            interactionService.addInteractionToSubHistory("Cal  l", 1, 1, "Robot    Failing");
            interactionService.addInteractionToSubHistory("Mee Ting", 1, 1, "Robot    Failing");
            interactionService.addInteractionToSubHistory("meeting", 1, 2, "Robot    Failing");
            interactionService.addInteractionToSubHistory("Meet ing", 1, 2, "Robot    Failing");
            interactionService.addInteractionToSubHistory("Ca l  l", 1, 2, "Robot    Failing");
            interactionService.addInteractionToSubHistory("E mail", 1, 2, "Robot    Failing");

            // GET ALL INTERACTIONS ADDED
            ArrayList<Interaction> interactions = interactionService.getInteractions();

            // Get the list of subhistories of the client 1. the size must be 3 (meeting call & email
            ArrayList<InteractionComponent> components1 = interactionHistoryService
                    .getInteractionshistoryById(1)
                    .getInteractions();
            ArrayList<InteractionHistory> subHistories1 = new ArrayList<>();

            for (InteractionComponent component : components1) {
                InteractionHistory subHistory = (InteractionHistory) component;
                subHistories1.add(subHistory);
            }
            InteractionHistory subHistoryCall1 = subHistories1.get(0);
            InteractionHistory subHistoryMeeting1 = subHistories1.get(1);
            InteractionHistory subHistoryEmail1 = subHistories1.get(2);

            // Get the list of subhistories of the client  3. the size must be 3 (meeting call & email
            ArrayList<InteractionComponent> components2 = interactionHistoryService
                    .getInteractionshistoryById(2)
                    .getInteractions();

            ArrayList<InteractionHistory> subHistories2 = new ArrayList<>();

            for (InteractionComponent component : components2) {
                InteractionHistory subHistory = (InteractionHistory) component;
                subHistories2.add(subHistory);
            }
            InteractionHistory subHistoryCall2 = subHistories2.get(0);
            InteractionHistory subHistoryMeeting2 = subHistories2.get(1);
            InteractionHistory subHistoryEmail2 = subHistories2.get(2);


        /* log file does not exist initially
           We instantiated interactionHistoryService while history file does not exist
                → InteractionHistoryFileHandler create logfile with not found message
                → We must ensure that it has been created correctly
         */
            assertTrue(Files.exists(loggerPath));
            /*history file should be created since we added  9 interactions*/
            assertTrue(Files.exists(historyPath));
            // 2 histories must be in the json file 1 for each client 1 & 3
            assertTrue(interactionHistoryService.isexisting(1));
            assertTrue(interactionHistoryService.isexisting(2));

            // the size of history client 1 & 3 must be 3 for the three types of interactions
            assertEquals(3, subHistories1.size());
            assertEquals(3, subHistories2.size());

            // we create 2 email, 2 call and 1 meeting interactions for the client 1, so subhistories must contains equivalent number of elements
            assertEquals(2, subHistoryEmail1.getInteractions().size());
            assertEquals(2, subHistoryCall1.getInteractions().size());
            assertEquals(1, subHistoryMeeting1.getInteractions().size());

            // we create 1 email, 1 call and 2 meeting interactions for the client 1, so subhistories must contains equivalent number of elements
            assertEquals(1, subHistoryEmail2.getInteractions().size());
            assertEquals(1, subHistoryCall2.getInteractions().size());
            assertEquals(2, subHistoryMeeting2.getInteractions().size());

            // we have created 9 interactions in total
            assertEquals(9, interactions.size());
            return;
        } catch (IOException e){
            LogHandler.logError("IO Error while saving interaction history file: " + e.getMessage());
        }


    }

    @Test
    public void testRemoveInteraction() {
        try{
            LogHandler.logInfo("STARTS REMOVE PROCESS");
            InteractionService instance1 = InteractionService.getUniqueInstance();

            instance1.addInteractionToSubHistory("EMAIL", 1, 1, "Robot    Failing");
            instance1.addInteractionToSubHistory("EM   AIL", 1, 1, "Robot    Failing");
            instance1.addInteractionToSubHistory("Cal  l", 1, 1, "Robot    Failing");
            instance1.addInteractionToSubHistory("Mee Ting", 1, 1, "Robot    Failing");
            instance1.addInteractionToSubHistory("Call", 1, 1, "Robot    Failing");
            instance1.addInteractionToSubHistory("meeting", 1, 2, "Robot    Failing");
            instance1.addInteractionToSubHistory("Meet ing", 1, 2, "Robot    Failing");
            instance1.addInteractionToSubHistory("Ca l  l", 1, 2, "Robot    Failing");
            instance1.addInteractionToSubHistory("E mail", 1, 2, "Robot    Failing");

            int clientId = instance1.findClientFromInteraction(12);
            String interactionType = instance1.getInteractionById(12).getType();

            instance1.removeInteraction(12);

            InteractionHistoryService instance2 = InteractionHistoryService.getInstance();
            ArrayList<InteractionComponent> subcomponents = instance2.getInteractionshistoryById(clientId).getInteractions();
            ArrayList<InteractionHistory> subhistories = new ArrayList<>();

            for (InteractionComponent subcomponent : subcomponents) {
                InteractionHistory history = (InteractionHistory) subcomponent;
                subhistories.add(history);
            }

            InteractionHistory matchingsubhistory = null;
            for (InteractionHistory subhistory : subhistories) {
                if (subhistory.getTitle().equals(interactionType)) {
                    matchingsubhistory = subhistory;
                    break;
                }
            }

            assertEquals(1, matchingsubhistory.getInteractions().size());
            assertNull(instance1.getInteractionById(7));
        } catch (IOException e){
            LogHandler.logError("IO Error while saving interaction history file: " + e.getMessage());
        }
    }
}
