package test;


import model.User;
import org.junit.Before;
import org.junit.Test;
import service.UserService;
import utils.LogHandler;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserServiceTest {
    private final Path userPath = Paths.get("data/users.json");

    @Before
    public void setup() {
        try {
            Files.deleteIfExists(userPath);

            Field instance = UserService.class.getDeclaredField("uniqueInstance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
            LogHandler.logError("Error during test setup: " + e);
        }

        UserService.isTesting = true;
    }

    @Test
    public void testAddUser() {
        UserService service = UserService.getUniqueInstance();
        LogHandler.logInfo("istesting: " +  service.getIsTesting());
        LogHandler.logInfo("Start");
        try{
            service.addUser("giorGia", "raviolli", "Gv@example.com", "admin", "test");
            ArrayList<User> users = service.getusers();
            assertEquals(1, users.size());
            assertEquals("giorgia", users.get(0).getFirstName());
        } catch (IOException e) {
            LogHandler.logError("IOException thrown in testAddClient: " + e.getMessage());
        }

    }

    @Test
    public void testDoesClientExist() {
        UserService service = UserService.getUniqueInstance();
        LogHandler.logInfo("istesting: " +  service.getIsTesting());
        try {
            service.addUser("balu", "raviolli", "Gv@example.com", "admin", "OpenAI");
            int id = service.getusers().get(0).getId();
            assertTrue(service.doesUserExist(id));
            assertFalse(service.doesUserExist(9999));
        } catch (IOException e) {
            LogHandler.logError("IOException thrown in testDoesClientExist: " + e.getMessage());
        }
    }

    @Test
    public void testFindClientByName() {
        UserService service = UserService.getUniqueInstance();
        LogHandler.logInfo("istesting: " +  service.getIsTesting());
        try{
            service.addUser("tadA", "raviolli", "Gv@example.com", "admin", "OpenAI");
            ArrayList<User> found = service.findUserByName("TadA");
            assertEquals(1, found.size());
            assertEquals("tada", found.get(0).getFirstName());
        } catch (IOException e) {
            LogHandler.logError("IOException thrown in testFindClientByName: " + e.getMessage());
        }
    }

    @Test
    public void testRemoveUser() {
        UserService service = UserService.getUniqueInstance();
        LogHandler.logInfo("istesting: " +  service.getIsTesting());
        try{
            service.addUser("ecco", "raviolli", "Gv@example.com", "admin", "OpenAI");
            int id = service.getusers().get(0).getId();
            LogHandler.logInfo("ID: " + id);
            ArrayList<User> users = service.getusers();
            LogHandler.logInfo("List size: " + users.size());
            boolean removed = service.removeUser(id);
            assertTrue(removed);
            assertFalse(service.doesUserExist(id));
        } catch (IOException e) {
            LogHandler.logError("IOException thrown in testFindClientByName: " + e.getMessage());
        } catch (Exception e) {
            LogHandler.logError("error" + e.getMessage());
        }
    }
}
