// layout-loader.js - Handles loading header and footer components and highlights current page
function loadLayout() {
  // Get current page filename
  const currentPath = window.location.pathname;
  const currentPage = currentPath.substring(currentPath.lastIndexOf('/') + 1);
  
  // Load header content
  fetch('header.html')
    .then(response => response.text())
    .then(data => {
      document.getElementById('header').innerHTML = data;
      
      // After header is loaded, highlight the current page in navigation
      highlightCurrentPage(currentPage);
      
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

// Function to highlight the current page in navigation
function highlightCurrentPage(currentPage) {
  // Default to index.html if on the root path
  if (currentPage === '' || currentPage === '/') {
    currentPage = 'index.html';
  }
  
  // Remove active class from all nav links
  const navLinks = document.querySelectorAll('.navbar-nav .nav-item.nav-link');
  navLinks.forEach(link => {
    link.classList.remove('active');
  });
  
  // Add active class to the current page's nav link
  navLinks.forEach(link => {
    const href = link.getAttribute('href');
    if (href === currentPage) {
      link.classList.add('active');
    }
  });
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
  if (!document.getElementById('header2.0') && !document.getElementById('footer')) {
    // Just update the cart badge if found
    updateCartBadge();
  } else {
    // Otherwise load the full layout
    loadLayout();
  }
});