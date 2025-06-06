package model;

import java.util.ArrayList;
import java.util.Iterator;

public class Cart {
    /**
     * The list of cartProducts currently in the cart.
     */
    private ArrayList<CartProduct> cartProductList; 

    /**
     * The total price of all cartProducts in the cart.
     */
    private double total;

// ==========================================================================================

    /**
     * Constructs an empty cart with zero total.
     */
    public Cart(){
        this.cartProductList = new ArrayList<>();
        this.total = 0;
    }
    
// ==========================================================================================

    /**
     * Returns the  copy list of cartProducts in the cart for safety.
     * 
     * @return The list of cartProduct
     */
    public ArrayList<CartProduct> getCartProductList() {
        return new ArrayList<>(cartProductList);
    }

    /**
    * Recalculate the total of the Cart.
    * Useful after a product removal or quantity edit
    * 
    */
    private void calculateTotal() {
        this.total = 0;
        for (CartProduct cp : cartProductList) {
            double quantity = cp.getQuantity();
            double price = cp.getproduct().getPrice();
            this.total += (quantity * price);
        }
    }

    /**
    * Update the cart total. Faster than recalculating the total.
    * Useful when adding a product
    *
    * @param quantity la quantité ajoutée
    * @param price le prix unitaire du produit ajouté
    */
    private void updateTotal(int quantity, double price) {
        this.total += (quantity * price);
    }

    /**
     * Returns the current total price of the cart.
     * 
     * @return The total price
     */
    public double getTotal() {

        return total;
    }

    /**
     * Check the presence of a product in the cart.
     * Returns it if so, else return null
     * 
     * @param productId The ID of the product to search in the cart list
     * @return the found product or null
     */
    public CartProduct findProductInCart(int productId) {
        for (CartProduct cp : cartProductList) {
            int id = cp.getproduct().getId();
            if (id == productId) {
                return cp;
            }
        }
        return null;
    }

// ==========================================================================================

    /**
     * Adds a Product and its desired quantity to the cart.
     * 
     * @param product The product to add to the cart
     * @param quantity the desired quantity of the product
     */
    public void addItem(Product product, int quantity ) {
        // throw an exception if the quantity entered by the user is invalid (<=0)
        if (quantity <=  0) {
            throw new IllegalArgumentException("The quantity must be strictly positive");
        }

        // if the the cart already contained the product the user wants to to add to it, we just increase the quantity
            CartProduct cartProduct = this.findProductInCart(product.getId());
            if (cartProduct != null) {
                cartProduct.setQuantity(cartProduct.getQuantity() + quantity);
                this.updateTotal(quantity, product.getPrice());
                return;
            }
        
        // Else we create a new CartProduct and add it to the cart
        CartProduct productToAdd = new CartProduct(product, quantity);
        cartProductList.add(productToAdd);
        this.updateTotal(quantity, product.getPrice());
    }

// ==========================================================================================

    /**
     * Removes a product from the cart based on its ID.
     * If a product in the cart has the same ID as the one provided,
     * it will be removed from the cart
     * 
     * @param id The ID of the product we want to delete
     */
    public void remove(int id) {
        Iterator<CartProduct> iterator = cartProductList.iterator();
        while (iterator.hasNext()) {
            CartProduct cartProduct = iterator.next();
            if (cartProduct.getproduct().getId() == id){
                iterator.remove();
                this.calculateTotal();
                return;
            }
        }
    }

// ==========================================================================================

    /**
     * Update the quantity of a product in the cart.
     * 
     * @param targetId The ID of the product to update
     * @param quantity The new quantity to set
     */
    public void updateQuantity(int targetId, int quantity) {
        if (quantity <=  0) {
            throw new IllegalArgumentException("The quantity must be strictly positive");
        }
        
        CartProduct cartProduct = this.findProductInCart(targetId);
            if (cartProduct != null) {
                cartProduct.setQuantity(quantity);
                this.calculateTotal();
            }
    }


}
