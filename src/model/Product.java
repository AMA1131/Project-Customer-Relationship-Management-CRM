package model;

import java.util.concurrent.atomic.AtomicInteger;

public class Product {

    /**
     * Counter to generate automatically a new product ID
     * AtomicInteger type used to avoid potential multithreading issues.
     * Garantee that two or more user cannot increment the counter at the same time
     */
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    /**
     * Name of the product
     */
    private String name;
    /** Category of the product */
    private String category;

    /** Description of the product */
    private String description;

    /** Price of the product */
    private double price;

    /**ID of the product */
    private int id;

    /** Available stock to buy of the product */
    private int stock;

//======================================================================================================================

    /**
     * Returns the name of the product.
     * @return name of the product
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the product.
     * @param name The name to set (Cannot be empty)
     * @throws IllegalArgumentException If the name is not recognized
     */
    public void setName( String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The product name cannot be empty.");
        }
        this.name = name;
    }

    /**
     * Returns the price of the product.
     * @return price of the product
     */
    public double getPrice() {
        return price;
    }

    /**
     * sets the price of the product.
     * @param price the price of the product (must be positive)
     * @throws IllegalArgumentException If the price is not > 0
     */
    public void setPrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("The price must be strictly positive.");
        }
        this.price = price;
    }

    /**
     * Returns the ID of the product.
     * @return The Id of the product
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the product.
     * @param id The id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the description of the product.
     * @return String description of the product
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the product.
     * @param description Text that describes the product
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the category of the product.
     * @return category of the product.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category of the product.
     * @param category The category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the available quantity of the product.
     * @return Available stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * Sets the available quantity of the product.
     * @param stock The quantity to set (must be >= 0)
     * @throws IllegalArgumentException If the quantity is < 0
     */
    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("The stock can't be negative.");
        }
        this.stock = stock;
    }

    /**
     * Generate a unique ID for each product.
     * 
     * @return An integer
     */
    private int generateId(){
        return idCounter.getAndIncrement();
    }

//======================================================================================================================

    /**
     * Constructs product with all its attribute.
     * Use setters to include the control of inputs.
     *
     * @param name Name of the product
     * @param price Price of the product
     * @param id Unique ID of the product
     * @param description Description of the product
     * @param category Category of the product
     * @param stock Available quantity in stock.
     */
    public Product(String name, double price, String description, String category, int stock) {
        this.setName(name);
        this.setPrice(price);
        this.setId(this.generateId());
        this.setDescription(description);
        this.setCategory(category);
        this.setStock(stock);
    }

//======================================================================================================================

    @Override
    public String toString() {
        return "Product {" + "id=" + id + ", name='" + name + "\'" + ", description= '" +  description + "\'" + ", price= '" +  price + "\'" + ", stockQuantity= '" +  stock + "\'" + "}";
    }
}
