// layout-loader.js - Handles loading header and footer components
function loadLayout() {
  // Load header content
  fetch('header.html')
    .then(response => response.text())
    .then(data => {
      document.getElementById('header').innerHTML = data;
      
      // Update cart badge after header is loaded
      updateCartBadge();
    })
    .catch(error => console.error('Error loading header:', error));

  // Load footer content
  fetch('footer.html')
    .then(response => response.text())
    .then(data => {
      document.getElementById('footer').innerHTML = data;
    })
    .catch(error => console.error('Error loading footer:', error));
}

// Function to update cart badge count
function updateCartBadge() {
  // Get cart items from localStorage
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  
  // Find all cart badge elements
  const cartBadges = document.querySelectorAll('.fas.fa-shopping-cart.text-primary + .badge');
  
  // Update all badge elements with cart count
  cartBadges.forEach(badge => {
    badge.textContent = cart.length;
  });
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
  // If there's no header/footer elements to load into, this is a single-page setup
  if (!document.getElementById('header') && !document.getElementById('footer')) {
    // Just update the cart badge if found
    updateCartBadge();
  } else {
    // Otherwise load the full layout
    loadLayout();
  }
});