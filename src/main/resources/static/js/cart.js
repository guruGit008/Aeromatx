let eventListenersInitialized = false; // Flag to prevent duplicate initializations

// Cart badge update function
function updateCartBadge() {
  // Get cart items from localStorage
  const cart = JSON.parse(localStorage.getItem('cart')) || [];
  
  // Find all cart badge elements (there may be multiple in responsive layouts)
  const cartBadges = document.querySelectorAll('.fas.fa-shopping-cart.text-primary + .badge');
  
  // Update all badge elements with cart count
  cartBadges.forEach(badge => {
    badge.textContent = cart.length;
  });
}

function initializeCartEventListeners() {
  // If event listeners are already initialized, don't do it again
  if (eventListenersInitialized) {
    return;
  }
  
  // Direct "Add to Cart" button functionality
  document.querySelectorAll('.add-to-cart').forEach(button => {
    button.addEventListener('click', function() {
      // Get the product container
      const productItem = this.closest('.product-item');
      
      // Get product details
      const productName = productItem.dataset.productName;
      const grade = productItem.querySelector('.grade-selector').value;
      const shape = productItem.querySelector('.shape-selector').value;
      
      // Default values for additional fields
      const quantity = 1;
      const thickness = "";
      const length = "";
      const width = "";
      const specialInstructions = "Standard " + productName + " " + shape;
      const imageSrc = "assets/" + productName.toLowerCase() + "-image.jpg";
      
      // Create cart item object matching the structure used by customize function
      const item = {
        title: productName,
        imageSrc: imageSrc,
        grade: grade,
        shape: shape,
        length: length,
        width: width,
        thickness: thickness,
        quantity: quantity,
        specialInstructions: specialInstructions
      };
      
      // Add to cart array
      let cart = JSON.parse(localStorage.getItem('cart')) || [];
      cart.push(item);
      
      // Save cart to local storage
      localStorage.setItem('cart', JSON.stringify(cart));
      
      // Update cart badge count
      updateCartBadge();
      
      // Show success popup
      showAddToCartPopup(productName);
    });
  });

  // Customize product modal functionality
  document.querySelectorAll('.customize-product').forEach(button => {
    button.addEventListener('click', function() {
      const productName = this.getAttribute('data-product-name');
      const modal = document.getElementById('customizeModal');
      modal.setAttribute('data-product-name', productName); // store in modal
      
      // Reset form fields
      document.getElementById('customizationForm').reset();
      
      // Pre-populate the grade and shape based on what was selected in the product card
      try {
        const productItem = this.closest('.product-item');
        const selectedGrade = productItem.querySelector('.grade-selector').value;
        const selectedShape = productItem.querySelector('.shape-selector').value;
        
        document.getElementById('productGrade').value = selectedGrade;
        document.getElementById('productShape').value = selectedShape;
      } catch (e) {
        console.log('Could not pre-populate form:', e);
      }
    });
  });

  // Customized Product: Add to Cart Button Logic - FIXED VERSION
  const addCustomizedBtn = document.getElementById("addCustomizedToCart");
  if (addCustomizedBtn) {
    addCustomizedBtn.addEventListener("click", function() {
      const modal = document.getElementById("customizeModal");
      const title = modal.getAttribute("data-product-name");
      const grade = document.getElementById("productGrade").value;
      const shape = document.getElementById("productShape").value;
      const quantity = parseInt(document.getElementById("productQuantity").value);
      const length = document.getElementById("productLength").value;
      const width = document.getElementById("productWidth").value;
      const thickness = document.getElementById("productThickness").value;
      const specialInstructions = document.getElementById("specialInstructions").value;
      const imageSrc = "assets/" + title.toLowerCase() + "-image.jpg";

      if (!grade || !shape || !quantity || quantity < 1) {
        alert("Grade, Shape, and Quantity are required.");
        return;
      }

      const item = {
        title,
        imageSrc,
        grade,
        shape,
        length,
        width,
        thickness,
        quantity,
        specialInstructions
      };

      let cart = JSON.parse(localStorage.getItem("cart")) || [];
      cart.push(item);
      localStorage.setItem("cart", JSON.stringify(cart));
      
      // Update cart badge count
      updateCartBadge();

      // Show success popup
      showAddToCartPopup(title);
      
      // Close the modal and remove the backdrop properly
      closeCustomizeModal();
    });
  }
  
  // Set flag to indicate that event listeners have been initialized
  eventListenersInitialized = true;
}

// Function to properly close the modal and clean up the backdrop
function closeCustomizeModal() {
  // First try with jQuery if available
  if (typeof $ !== 'undefined' && typeof $.fn.modal !== 'undefined') {
    $('#customizeModal').modal('hide');
    return;
  }
  
  // If jQuery is not available or failed, try with Bootstrap 5 approach
  if (typeof bootstrap !== 'undefined') {
    const modalElement = document.getElementById('customizeModal');
    const bootstrapModal = bootstrap.Modal.getInstance(modalElement);
    if (bootstrapModal) {
      bootstrapModal.hide();
      return;
    }
  }
  
  // Last resort manual approach (will work in most cases)
  const modalElement = document.getElementById('customizeModal');
  modalElement.classList.remove('show');
  modalElement.style.display = 'none';
  
  // Remove modal-open class from body
  document.body.classList.remove('modal-open');
  
  // Remove backdrop
  const backdrops = document.getElementsByClassName('modal-backdrop');
  while (backdrops.length > 0) {
    backdrops[0].parentNode.removeChild(backdrops[0]);
  }
}

// Initialize event listeners when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
  // Update cart badge when page loads
  updateCartBadge();
  
  initializeCartEventListeners();
  
  // Load cart items if we're on the cart page
  const cartItemsContainer = document.getElementById("cart-items");
  if (cartItemsContainer) {
    loadCartItems();
  }
});

// Shared function to show popup
function showAddToCartPopup(productName) {
  const popup = document.createElement("div");
  popup.innerHTML = `<strong>${productName}</strong> added to cart!`;
  popup.style.position = "fixed";
  popup.style.top = "20px";
  popup.style.right = "20px";
  popup.style.backgroundColor = "#28a745";
  popup.style.color = "#fff";
  popup.style.padding = "10px 20px";
  popup.style.borderRadius = "5px";
  popup.style.boxShadow = "0 0 10px rgba(0,0,0,0.2)";
  popup.style.zIndex = 1000;
  document.body.appendChild(popup);

  setTimeout(() => {
    popup.remove();
  }, 2000);
}

// Function to load cart items
function loadCartItems() {
  const cart = JSON.parse(localStorage.getItem("cart")) || [];
  const container = document.getElementById("cart-items");
  container.innerHTML = "";

  if (cart.length === 0) {
    container.innerHTML = '<tr><td colspan="11" class="text-muted text-center">Your cart is empty.</td></tr>';
    return;
  }

  cart.forEach((item, index) => {
    const total = item.price ? item.price * item.quantity : "-";
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${item.title || "-"}</td>
      <td>${item.grade || "-"}</td>
      <td>${item.shape || "-"}</td>
      <td>${item.length || "-"}</td>
      <td>${item.width || "-"}</td>
      <td>${item.thickness || "-"}</td>
      <td>${item.quantity}</td>
      <td>${item.price ? "$" + item.price : "-"}</td>
      <td>${total !== "-" ? "$" + total : "-"}</td>
      <td>${item.specialInstructions || "-"}</td>
      <td><button class="btn btn-sm btn-danger" onclick="removeItem(${index})">Remove</button></td>
    `;
    container.appendChild(row);
  });
}

// Remove item function
function removeItem(index) {
  let cart = JSON.parse(localStorage.getItem("cart")) || [];
  cart.splice(index, 1);
  localStorage.setItem("cart", JSON.stringify(cart));
  
  // Update cart badge count
  updateCartBadge();
  
  // Reload only if we're on the cart page
  location.reload();
}