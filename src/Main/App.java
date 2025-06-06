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
        System.out.printf("Nos Produits: !\n");
        Product p1 = new Product("Asus", 1199.99, "Perfect product", "Gamer laptop", 200);
        Product p2 = new Product("Lenovo", 800, "Almost the best one", "Gamer laptop", 50);
        System.out.println(p1);
        System.out.printf("%s\n\n", p2);

        // creating a user and displaying its info
        System.out.printf("Utilisateur connecté: !\n");
        User u = new User("user", "Ado", "canarabi@gmail.com", "+221775754839");
        System.out.printf("%s\n\n", u);

        /**
         * 1. Initializing the cart
         * 2. Adding the previous product to the cart
         * 3. Displaying the products in the cart
         * */
        System.out.printf("Panier: !\n");
        Cart cart = new Cart();
        cart.addItem(p1, 1);
        cart.addItem(p2, 1);
        for (CartProduct cp : cart.getCartProductList()) {
            System.out.println(cp);
        }

        System.out.printf("\n Modification Panier: !\n");
        cart.addItem(p1, 4);
        cart.addItem(p2, 9);
        for (CartProduct cp : cart.getCartProductList()) {
            System.out.println(cp);
        }

        System.out.printf("Cart total: %.2f\n",cart.getTotal());

        System.out.printf("\n 2eme Modification Panier: !\n");
        cart.updateQuantity(p1.getId(), 15);
        cart.remove(p2.getId());
        for (CartProduct cp : cart.getCartProductList()) {
            System.out.println(cp);
        }
    }
}
