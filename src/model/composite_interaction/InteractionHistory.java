package model.composite_interaction;

import java.util.ArrayList;

import model.Entity;
import utils.Logger;

public class InteractionHistory implements Entity, InteractionComponent { 
    private int id;
    private String title;
    private ArrayList<InteractionComponent> interactions = new ArrayList<>();
    private int clientId;

//==========================================================================================================

    public InteractionHistory(int id, String title, int clientId) {
        this.title = title;
        this.clientId = clientId;
        this.id = id;
    }

//==========================================================================================================

    public void addInteraction(InteractionComponent interaction) {
        if (interaction == null){
            Logger.log("Attempted to add null interaction to history.");
            return;
        }
        interactions.add(interaction);
    }

    @Override
    public int getClientId() {
        return clientId;
    }

    @Override
    public void display(){
        System.out.println("Interaction with client #" + clientId + ":");
        for (InteractionComponent interaction : interactions) {
            interaction.display();
        }
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public ArrayList<InteractionComponent> getInteractions() {
        return interactions;
    }
}
