package model;

import java.util.Objects;

/**
 * Represents an product in the shopping cart.
 * It holds a product and the quantity of that product selected by the user.
 */
public class CartProduct {
    /**
     * The product added to the cart.
     */
    private Product product;

    /**
     * The quantity of the product in the cart.
     */
    private int quantity;

    // ==============================================================================================================

    /**
     * Constructs a cart product with the product and the quantity desired by the user.
     * Use setter to validate quantity input
     * 
     * @param product The product to add to the cart
     * @param quantity the wanted quantity of the product to add to the cart 
     */
    public CartProduct(Product product, int quantity) {
        this.product = product;
        this.setQuantity(quantity);
    }

    // ==============================================================================================================

    /**
     * Returns the product of the cart product.
     * 
     * @return The product
     */
    public Product getproduct() {
        return product;
    }

    /**
     * Sets a specific product in this CartProduct.
     * @param product the product to set
     */
    public void setproduct(Product product) {
        this.product = product;
    }

    /**
     * Returns the quantity of the product for this CartProduct.
     * 
     * @return The quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product in this CartProduct.
     * 
     * @param quantity The quantity to set
     */
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }

    // ==============================================================================================================
    /**
     * Returns a string representation of the CartProduct.
     */
    @Override
    public String toString() {
        return quantity + " x " + product.getName() + " (" + product.getPrice() + " euros per unit)";
    }

    // @Override
    // public boolean equals(Object object) {
    //     // if (this == object) return true;
    //     if (!(object instanceof CartProduct)) return false;

    //     CartProduct castedObject = (CartProduct) object;
    //     return Objects.equals(this.product.getId(), castedObject.product.getId());
    // }
}
