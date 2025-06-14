package com.aeromatx.back.service;

import com.aeromatx.back.entity.*;
import com.aeromatx.back.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductService productService; // To decrease stock

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository,
                        CartService cartService,
                        ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.productService = productService;
    }

    public Order createOrder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Cart cart = cartService.getCartByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot create order from an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PENDING"); // Initial status

        Set<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            // Check and decrease stock
            productService.decreaseStock(product.getId(), quantity);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(product.getPrice()); // Capture price at time of purchase
            return orderItem;
        }).collect(Collectors.toSet());

        order.setOrderItems(orderItems);

        // Calculate total amount
        double totalAmount = orderItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceAtPurchase())
                .sum();
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        orderItems.forEach(item -> item.setOrder(savedOrder)); // Link items to the saved order
        orderItemRepository.saveAll(orderItems); // Save order items

        // Clear the user's cart after order creation
        cartService.clearCart(userId);

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return orderRepository.findByUser(user);
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Basic status validation
        Set<String> validStatuses = Set.of("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
        if (!validStatuses.contains(newStatus.toUpperCase())) {
            throw new IllegalArgumentException("Invalid order status: " + newStatus);
        }
        order.setStatus(newStatus.toUpperCase());
        return orderRepository.save(order);
    }

    // Security helper method for @PreAuthorize
    public boolean isOrderOwnedByUser(Long orderId, Long userId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.isPresent() && order.get().getUser().getId().equals(userId);
    }
}