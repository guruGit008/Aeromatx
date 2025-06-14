package com.aeromatx.back.service;

import com.aeromatx.back.entity.Cart;
import com.aeromatx.back.entity.CartItem;
import com.aeromatx.back.entity.Product;
import com.aeromatx.back.entity.User;
import com.aeromatx.back.repository.CartItemRepository;
import com.aeromatx.back.repository.CartRepository;
import com.aeromatx.back.repository.ProductRepository;
import com.aeromatx.back.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Optional<Cart> getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return cartRepository.findByUser(user);
    }

    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStockQuantity());
        }

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        });

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingCartItem.isPresent()) {
            CartItem item = existingCartItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            cartItemRepository.save(newCartItem);
            cart.getCartItems().add(newCartItem); // Ensure the collection is updated
        }

        return cart;
    }

    public Cart updateCartItemQuantity(Long userId, Long productId, int newQuantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Product not found in cart."));

        if (newQuantity <= 0) {
            // If quantity is 0 or less, remove the item
            cartItemRepository.delete(item);
            cart.getCartItems().remove(item);
        } else {
            if (product.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStockQuantity());
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        }
        return cart;
    }

    public Cart removeItemFromCart(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Product not found in cart."));

        cartItemRepository.delete(item);
        cart.getCartItems().remove(item); // Ensure the collection is updated
        return cart;
    }

    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
        
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear(); // Clear the collection
        cartRepository.save(cart); // Save the updated cart to persist the clear
    }
}