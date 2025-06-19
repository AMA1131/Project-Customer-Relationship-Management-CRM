package model.composite_interaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.Entity;

public class Interaction implements Entity, InteractionComponent {
    private int id;
    private String type;
    private int userId;
    private String description;
    private String interactionDate;
    private int clientId;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

//==============================================================================================================

    public Interaction(int id, String type, int userId, String description, int clientId) {
        this.id = id;
        this.type = type;
        this.userId = userId;
        this.clientId = clientId;
        this.description = description;
        this.interactionDate = LocalDateTime.now().format(formatter);
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getInteractionDate() {
        return interactionDate;
    }

    public int getClientId() {
        return clientId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//==============================================================================================================

    @Override
    public String toString() {
        return "Interaction [id= " + id + ", type= '" + type + "', userId= " + userId + ", clientId= " + clientId + ", description= '" + description + "', timestamp= " + interactionDate + "]";
    }

    @Override
    public void display() {
        System.out.println(this.toString());
    }
}
