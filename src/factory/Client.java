package factory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import model.Entity;
import service.ClientService;
import utils.InputValidator;

public class Client extends AbstractEntity {
    
    private static final AtomicInteger idCounter = new AtomicInteger(initIdCounter());

    private static int initIdCounter() {
        int maxId = 0;
            ArrayList<model.Client> clients = ClientService.getUniqueInstance().getClients();
            if (!clients.isEmpty()) {
                for (model.Client c : clients) {
                    if (c.getId() > maxId) {
                        maxId = c.getId();
                    }
                }
            }
        return maxId + 1;
    }

    @Override
    public Entity createEntity(Object... args){
        if (args.length < 5) {
            throw new IllegalArgumentException("Insufficient parameters to create a Client");
        }

        String firstName = ((String) args[0]).trim().toLowerCase().replaceAll("\\s+", " ");
        String lastName = ((String) args[1]).trim().toLowerCase().replaceAll("\\s+", " ");
        String email = ((String) args[2]).trim().toLowerCase().replaceAll("\\s+", " ");
        String phoneNumber = (String) args[3];
        String company = ((String) args[4]).trim().replaceAll("\\s+", " ");

        if (!(InputValidator.isValidName(firstName))) {
            throw new IllegalArgumentException ("Invalid firstname format");
        }
        if (!(InputValidator.isValidName(lastName))) {
            throw new IllegalArgumentException ("Invalid lastname format");
        }
        if (!(InputValidator.isValidEmail(email))) {
            throw new IllegalArgumentException ("Invalid email format");
        }
        if (!(InputValidator.isValidNumber(phoneNumber))) {
            throw new IllegalArgumentException ("Invalid phone number format");
        }

        return new model.Client(idCounter.getAndIncrement(), firstName, lastName, email, phoneNumber, (!company.isEmpty() ? company : "None"));

    }
}
