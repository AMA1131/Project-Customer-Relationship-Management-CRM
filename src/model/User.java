package model;

public class User {
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
    private int number;

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
        this.email = email;
    }

    /** 
     *  Return user number
     * @return User number
     */
    public int getNumber() {
        return number;
    }
    /** 
     *  Set the user number
     * @param number The number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Constructs a new User with the specified ID, name, email, and number.
     * 
     * @param id the user's ID
     * @param name the user's full name
     * @param email the user's email address
     * @param number the user's phone number
     */
    public User(int id, String name, String email, int number) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
    }

    /**
    * Returns a string representation of the user.
    * @return a formatted string containing the user's information.
    */
    @Override
    public String toString() {
        return "User #" + id + " - " + name + " - " + email + " - " + number;
    }
}
