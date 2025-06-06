package model;

import java.util.concurrent.atomic.AtomicInteger;

public class User {

    /**
     * Counter to generate automatically a new user ID
     * AtomicInteger type used to avoid potential multithreading issues.
     * Garantee that two or more user cannot increment the counter at the same time
     */
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    /** 
     * Unique user identifier
     */
    private int id;

    /** 
     *  Fullname of the user
     */
    private String name;

    /** 
     *  User's email
     */
    private String email;

    /** 
     *  User's number
     */
    private String number;

    /**
     * User role: 'admin' or 'user'
     */
    private String role;

//============================================================================================================== 

    /** 
     *  Returns user's ID
     * @return User's ID
     */
    public int getId() {
        return id;
    }
    /** 
     *  Set the unique user identifier
     * @param id The identifier to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /** 
     *  Return user's fullname
     * @return User's fullname
     */
    public String getName() {
        return name;
    }

    /** 
     *  Set the name of the user
     * @param name The username to set
     */
    public void setName(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The user name cannot be empty.");
        }
        this.name = name;
    }

    /** 
     *  Return user email
     * @return User email
     */
    public String getEmail() {
        return email;
    }

    /** 
     *  Set the user email
     * @param email The mail to set
     */
    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email: please enter a valid email.");
        }
        this.email = email;
    }

    /** 
     *  Return user number
     * @return User number
     */
    public String getNumber() {
        return number;
    }

    /** 
     *  Set the user number
     * 
     * @param number The number to set
     * @throws IllegalArgumentException If the role is not recognized
     */
    public void setNumber(String number) {
        if (!number.matches("^\\+?[0-9]{7,15}$")) {
        throw new IllegalArgumentException("Numéro de téléphone invalide.");
}
        this.number = number;
    }

    /**
     * Returns user Role
     * @return The role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user role.
     * @param role The role to set ("admin" or "user")
     * @throws IllegalArgumentException If the role is not recognized
     */
    public void setRole(String role) {
        if (role == null || !(role.equals("admin") || role.equals("user"))) {
            throw new IllegalArgumentException("Invalid role. Use 'admin' or 'user'.");
        }
        this.role = role;
    }

    /**
     * Generate a unique ID for each user.
     * 
     * @return An integer
     */
    private int generateId(){
        return idCounter.getAndIncrement();
    }

// =============================================================================================

    /**
     * Constructs a new User with the specified ID, name, email, and number.
     * 
     * @param id The user's ID
     * @param name The user's full name
     * @param email The user's email address
     * @param number The user's phone number
     * @param role The role of the user
     */
    public User(String role, String name, String email, String number) {
        this.setId(this.generateId());
        this.setName(name);
        this.setEmail(email);
        this.setNumber(number);
        this.setRole(role);
    }

// =============================================================================================

    /**
    * Returns a string representation of the user.
    * @return a formatted string containing the user's information.
    */
    @Override
    public String toString() {
        return "User {" + "id= " + id + ", name= '" +  name + "\'" + ", email= '" + email + "\'" + ", phoneNumber= '" + number + "\'" + ", role= '" + role + "\'" + "}";
    }
}
