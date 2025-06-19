package utils;

public class InputValidator {

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        // Sanitization
        email = email.trim().replaceAll("\\s+", " ").toLowerCase();
        String emailRegex = "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$";
        // Validation
        return email.matches(emailRegex);
    }

    public static boolean isValidNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() == 0) return false;
        // Sanitization
        phoneNumber = phoneNumber.trim().replaceAll("\\s+", "");
        String phoneNumberRegex = "^\\+?[0-9]{7,15}$";
        // Validation
        return phoneNumber.matches(phoneNumberRegex);
    }

    public static boolean isValidName(String name) {
        if (name == null || name.length() == 0) return false;
        // Sanitization
        name = name.trim().replaceAll("\\s+", " ").toLowerCase();
        String nameRegex = "^(?!.*[-.'\\s]{2})[\\p{L}'.\\-\\s]{2,50}$";
        // Validation
        return name.matches(nameRegex);
    }

    // public static boolean isValidInteger(String input) {
    //     input = input.trim().replaceAll("\\s+", "");
    //     try{
    //         Integer.parseInt(input);
    //         return true;
    //     } catch (NumberFormatException e) {
    //         return false;
    //     }
    // }


}
