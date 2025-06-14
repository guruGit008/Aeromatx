package com.aeromatx.back.controller;

import com.aeromatx.back.dto.product.ProductRequest;
import com.aeromatx.back.dto.product.ProductResponse;
import com.aeromatx.back.entity.Product; // Import the entity
import com.aeromatx.back.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.Optional; // Needed for Optional in getProductById
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET all products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        // productService.getAllProducts() should return List<Product>
        List<ProductResponse> products = productService.getAllProducts().stream()
                .map(this::convertToDto) // Now converts Product entity to ProductResponse DTO
                .collect(Collectors.toList());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // GET product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        // productService.getProductById(id) should return Optional<Product>
        return productService.getProductById(id)
                .map(this::convertToDto) // Now converts Product entity to ProductResponse DTO
                .map(productDto -> new ResponseEntity<>(productDto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST create a new product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        // Convert the incoming ProductRequest DTO to a Product entity
        Product productToCreate = convertToEntity(productRequest);
        // Pass the Product entity to the service
        Product createdProduct = productService.createProduct(productToCreate);
        // Convert the returned Product entity to a ProductResponse DTO
        return new ResponseEntity<>(convertToDto(createdProduct), HttpStatus.CREATED);
    }

    // PUT update an existing product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        try {
            // Convert the incoming ProductRequest DTO to a Product entity for update
            Product productDetails = convertToEntity(productRequest);
            // Pass the ID and the Product entity to the service
            Product updatedProduct = productService.updateProduct(id, productDetails);
            // Convert the returned Product entity to a ProductResponse DTO
            return new ResponseEntity<>(convertToDto(updatedProduct), HttpStatus.OK);
        } catch (RuntimeException e) {
            // More specific exception handling might be preferred here,
            // e.g., if ProductNotFoundException is thrown by service
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE a product
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- Corrected Helper methods for DTO conversion ---

    // Converts a Product entity to a ProductResponse DTO
    private ProductResponse convertToDto(Product product) {
        if (product == null) {
            return null;
        }
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl()
        );
    }

    // Converts a ProductRequest DTO to a Product entity
    private Product convertToEntity(ProductRequest productRequest) {
        if (productRequest == null) {
            return null;
        }
        Product product = new Product(); // Instantiate your Product entity
        // product.setId(productRequest.getId()); // Only if ProductRequest has an ID for update scenarios
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setImageUrl(productRequest.getImageUrl());
        return product;
    }
}