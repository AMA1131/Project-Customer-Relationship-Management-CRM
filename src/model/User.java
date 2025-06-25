package model;

public class User implements Entity{
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String role; //admin or user
    private final String hashedPassword;

//==============================================================================================

    public User (int id, String firstName, String lastName, String email, String role, String hashedPassword) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.hashedPassword = hashedPassword;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
    public String getHashedPassword() { return hashedPassword; }

//==============================================================================================

    @Override
    public String toString() {
        return "User [id= " + id + ", name= '" + firstName + " " + lastName + "', email= '" + email + "', role= '" + role + "']";
    }
}