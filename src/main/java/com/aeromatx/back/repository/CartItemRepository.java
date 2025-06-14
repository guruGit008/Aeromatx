package com.aeromatx.back.repository;

import com.aeromatx.back.entity.Cart;
import com.aeromatx.back.entity.CartItem;
import com.aeromatx.back.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}