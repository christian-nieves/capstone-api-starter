package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;
import org.yearup.service.ShoppingCartService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController // makes this class a rest controller
@RequestMapping("cart") // base url is /cart
@CrossOrigin // allows the frontend website to call these endpoints
@PreAuthorize("isAuthenticated()") // every endpoint here requires the user to be logged in
public class ShoppingCartController
{
    private ShoppingCartService shoppingCartService; // handles cart business logic
    private UserService userService; // used to look up the logged-in user

    // Constructor
    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService)
    {
        this.shoppingCartService = shoppingCartService; // injects the cart service
        this.userService = userService; // injects the user service
    }

    // Get cart
    @GetMapping // get /cart
    public ShoppingCart getCart(Principal principal)
    {
        String userName = principal.getName(); // gets the logged-in user's username
        User user = userService.getByUserName(userName); // looks up the full user record
        int userId = user.getId(); // pulls their user id

        return shoppingCartService.getByUserId(userId); // returns that user's cart
    }

    // Add product
    @PostMapping("products/{productId}") // post /cart/products/15
    public ResponseEntity<ShoppingCart> addProduct(@PathVariable int productId, Principal principal)
    {
        String userName = principal.getName(); // gets the logged-in user's username
        User user = userService.getByUserName(userName); // looks up the full user record
        int userId = user.getId(); // pulls their user id

        ShoppingCart cart = shoppingCartService.addProduct(userId, productId); // adds the product to their cart
        return ResponseEntity.status(HttpStatus.CREATED).body(cart); // returns the updated cart with 201 created
    }

    // Update product
    @PutMapping("products/{productId}") // put /cart/products/15
    public ShoppingCart updateProduct(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal)
    {
        String userName = principal.getName(); // gets the logged-in user's username
        User user = userService.getByUserName(userName); // looks up the full user record
        int userId = user.getId(); // pulls their user id

        return shoppingCartService.updateProduct(userId, productId,