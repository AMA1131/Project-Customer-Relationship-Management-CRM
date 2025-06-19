package app;

import model.User;

public class SessionManager {
    private static User currentUser;

    public static void login(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return isUserLoggedIn() && currentUser.getRole().equals("admin");
    }
}
