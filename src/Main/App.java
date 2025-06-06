package Main;
import model.Product;
import model.User;

import java.util.ArrayList;

import model.Cart;
import model.CartProduct;

public class App {
    public static void main(String[] args) {
        System.out.printf("Bienvenue dans mon projet Java !\n\n");

        // creating a product & displaying its info
        Product p = new Product("Asus", 1199.99, 644684);
        System.out.println(p);

        // creating a user and displaying its info
        User u = new User(41467, "Ado", "canarabi@gmail.com", 775754839);
        System.out.println(u);

        /**
         * 1. Initializing the cart
         * 2. Adding the previous product to the cart
         * 3. Displaying the products in the cart
         * */
        Cart cart = new Cart();
        cart.addItem(p, 1);
        ArrayList<CartProduct> cartProduct = cart.getCartProductList();
        for (CartProduct cp : cartProduct) {
            System.out.println(cp);
        }
    }
}
