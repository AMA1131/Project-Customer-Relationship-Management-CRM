package test;


import model.Client;
import model.User;
import org.junit.Before;
import org.junit.Test;
import service.ClientService;
import utils.LogHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ClientServiceTest {
    private final Path clientPath = Paths.get("data/clients.json");

    @Before
    public void setup() {
        ClientService.isTesting = true;
        try {
            Files.deleteIfExists(clientPath);
            LogHandler.logInfo("Client file deleted");

            Field instance = ClientService.class.getDeclaredField("uniqueInstance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            LogHandler.logError("Error during test setup: " + e);
        }
    }

    @Test
    public void testAddClient() {
        ClientService service = ClientService.getUniqueInstance();
        try{
            service.addClient("giorGia", "raviolli", "Gv@example.com", "1234567885", "OpenAI");
            ArrayList<Client> clients = service.getClients();
            assertEquals(1, clients.size());
            assertEquals("giorgia", clients.get(0).getFirstName());
        } catch (IOException e) {
            LogHandler.logError("IOException thrown in testAddClient: " + e.getMessage());
        }

    }

    @Test
    public void testDoesClientExist() {
        ClientService service = ClientService.getUniqueInstance();
        try {
            service.addClient("balu", "raviolli", "Gv@example.com", "1234567885", "OpenAI");
            int id = service.getClients().get(0).getId();
            assertTrue(service.doesClientExist(id));
            assertFalse(service.doesClientExist(9999));
        } catch (IOException e) {
            LogHandler.logError("IOException thrown in testDoesClientExist: " + e.getMessage());
        }
    }

    @Test
    public void testFindClientByName() {
        ClientService service = ClientService.getUniqueInstance();
        try{
            service.addClient("tadA", "raviolli", "Gv@example.com", "1234567885", "OpenAI");
            ArrayList<Client> found = service.findClientByName("TadA");
            assertEquals(1, found.size());
            assertEquals("tada", found.get(0).getFirstName());
        } catch (IOException e) {
            LogHandler.logError("IOException thrown in testFindClientByName: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveClient() {
        ClientService service = ClientService.getUniqueInstance();
        try{
            service.addClient("ecco", "raviolli", "Gv@example.com", "1234567885", "OpenAI");
            int id = service.getClients().get(0).getId();
            LogHandler.logInfo("ID: " + id);
            ArrayList<Client> clients = service.getClients();
            LogHandler.logInfo("List size: " + clients.size());
            boolean removed = service.removeClient(id);
            assertTrue(removed);
            assertFalse(service.doesClientExist(id));
        } catch (IOException e) {
            LogHandler.logError("IOException thrown in testFindClientByName: " + e.getMessage());
        }
    }
}
