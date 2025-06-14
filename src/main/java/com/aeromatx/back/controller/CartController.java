package com.aeromatx.back.controller;

import com.aeromatx.back.entity.Cart;
// import com.aeromatx.back.entity.CartItem;
import com.aeromatx.back.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get user's cart
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Add item to cart
    @PostMapping("/{userId}/add")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Cart> addItemToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        try {
            Cart updatedCart = cartService.addItemToCart(userId, productId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Update item quantity in cart
    @PutMapping("/{userId}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        try {
            Cart updatedCart = cartService.updateCartItemQuantity(userId, productId, quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Remove item from cart
    @DeleteMapping("/{userId}/remove")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Cart> removeItemFromCart(
            @PathVariable Long userId,
            @RequestParam Long productId) {
        try {
            Cart updatedCart = cartService.removeItemFromCart(userId, productId);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Clear cart
    @DeleteMapping("/{userId}/clear")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<HttpStatus> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
