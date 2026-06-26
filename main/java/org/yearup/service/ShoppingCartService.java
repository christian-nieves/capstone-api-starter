package org.yearup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.models.CartItem;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService
{
    private final ShoppingCartRepository shoppingCartRepository; // talks to the shopping_cart table via JPA
    private final ProductService productService; // used to look up full product details for each cart item

    // Constructor
    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository; // injects the cart repository
        this.productService = productService; // injects the product service
    }

    // Get cart by user id
    public ShoppingCart getByUserId(int userId)
    {
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId); // gets all rows in this user's cart

        ShoppingCart shoppingCart = new ShoppingCart(); // builds an empty cart to fill

        for (CartItem cartItem : cartItems) // loops through each saved cart row
        {
            ShoppingCartItem shoppingCartItem = new ShoppingCartItem(); // creates a cart item for the response
            shoppingCartItem.setProduct(productService.getById(cartItem.getProductId())); // attaches the full product details
            shoppingCartItem.setQuantity(cartItem.getQuantity()); // sets how many of that product are in the cart
            shoppingCart.add(shoppingCartItem); // adds it to the cart
        }

        return shoppingCart; // returns the fully built cart
    }

    // Add product
    public ShoppingCart addProduct(int userId, int productId)
    {
        CartItem existingItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId); // checks if the product is already in the cart

        if (existingItem == null) // if it is not in the cart yet
        {
            CartItem newItem = new CartItem(); // create a new cart row
            newItem.setUserId(userId); // set the user
            newItem.setProductId(productId); // set the product
            newItem.setQuantity(1); // start the quantity at 1
            shoppingCartRepository.save(newItem); // save the new row
        }
        else // if the product is already in the cart
        {
            existingItem.setQuantity(existingItem.getQuantity() + 1); // increase the quantity by 1
            shoppingCartRepository.save(existingItem); // save the updated row
        }

        return getByUserId(userId); // return the updated cart
    }

    // Update product
    public ShoppingCart updateProduct(int userId, int productId, int quantity)
    {
        CartItem existingItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId); // finds the product in the user's cart

        if (existingItem != null) // only update if the user already has it in their cart
        {
            existingItem.setQuantity(quantity); // set the new quantity from the request
            shoppingCartRepository.save(existingItem); // save the change
        }

        return getByUserId(userId); // return the updated cart
    }

    // Clear cart
    @Transactional // needed so the delete runs inside a transaction
    public ShoppingCart clearCart(int userId)
    {
        shoppingCartRepository.deleteByUserId(userId); // removes all of this user's cart rows
        return getByUserId(userId); // return the now empty cart
    }
}