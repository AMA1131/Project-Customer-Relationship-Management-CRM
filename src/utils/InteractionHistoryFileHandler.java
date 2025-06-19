package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import model.composite_interaction.Interaction;
import model.composite_interaction.InteractionComponent;
import model.composite_interaction.InteractionHistory;

public class InteractionHistoryFileHandler {
    private static final String fileName = "data/interactionsHistory.json";
    private static final Gson gson = buildGson();

    //private static Gson
    private static Gson buildGson() {
        // 1) Fabrique polymorphe pour InteractionComponent
        RuntimeTypeAdapterFactory<InteractionComponent> compFactory =
                RuntimeTypeAdapterFactory
                        .of(InteractionComponent.class, "kind")      // champ “kind” dans le JSON
                        .registerSubtype(Interaction.class,        "Interaction")
                        .registerSubtype(InteractionHistory.class, "InteractionHistory");

        // 3) on assemble tout dans le builder
        return new GsonBuilder()
                .registerTypeAdapterFactory(compFactory)
                .setPrettyPrinting()
                .create();
    }

    public static ArrayList<InteractionHistory> loadInteractionHistories() throws IOException{
        ArrayList<InteractionHistory>  interactionHistories = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // gson does't recognize ArrayList<InteractionHistory> so i tell him the type it musts read in the file
            Type interactionHistoriesListType = new TypeToken<ArrayList<InteractionHistory>>() {}.getType();
            interactionHistories = gson.fromJson(reader, interactionHistoriesListType);

            if (interactionHistories == null) interactionHistories = new ArrayList<>();
        } catch (FileNotFoundException e) {
            Logger.log("File not found : " + e.getMessage());
            /*return  interactionHistories;*/
        }

        return interactionHistories;
    }

    public static void saveInteractionHistories(ArrayList<InteractionHistory> interactionHistories) throws IOException{
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            gson.toJson(interactionHistories, writer);
            
        }
    }

    public static void addToDb(InteractionHistory newInteractionHistory) throws  IOException{
        ArrayList<InteractionHistory> interactionHistories = loadInteractionHistories();
        interactionHistories.add(newInteractionHistory);
        saveInteractionHistories(interactionHistories);
    }


}
