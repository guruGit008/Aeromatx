document.addEventListener('DOMContentLoaded', function() {
    // Load categories for dropdowns
    loadCategories();

    // Load products table
    loadProducts();

    // Search functionality
    document.getElementById('productSearch').addEventListener('input', function() {
        loadProducts(this.value);
    });

    // Category filter
    document.getElementById('categoryFilter').addEventListener('change', function() {
        loadProducts('', this.value);
    });

    // Save product button
    document.getElementById('saveProductBtn').addEventListener('click', function() {
        saveProduct();
    });

    // Update product button
    document.getElementById('updateProductBtn').addEventListener('click', function() {
        updateProduct();
    });
});

function loadCategories() {
    const categories = JSON.parse(localStorage.getItem('ecommerceCategories')) || [];
    const categoryDropdown = document.getElementById('productCategory');
    const editCategoryDropdown = document.getElementById('editProductCategory');
    const categoryFilter = document.getElementById('categoryFilter');

    // Clear existing options
    categoryDropdown.innerHTML = '<option value="">Select Category</option>';
    editCategoryDropdown.innerHTML = '<option value="">Select Category</option>';
    categoryFilter.innerHTML = '<option value="">All Categories</option>';

    // Add categories to dropdowns
    categories.forEach(category => {
        const option = document.createElement('option');
        option.value = category.name;
        option.textContent = category.name;
        categoryDropdown.appendChild(option.cloneNode(true));
        editCategoryDropdown.appendChild(option.cloneNode(true));
        categoryFilter.appendChild(option.cloneNode(true));
    });
}

function loadProducts(searchTerm = '', category = '') {
    const products = JSON.parse(localStorage.getItem('ecommerceProducts')) || [];
    const tbody = document.querySelector('#productsTable tbody');

    // Clear existing rows
    tbody.innerHTML = '';

    // Filter products based on search term and category
    const filteredProducts = products.filter(product => {
        const matchesSearch = searchTerm === '' ||
            product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            product.description.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesCategory = category === '' || product.category === category;
        return matchesSearch && matchesCategory;
    });

    // Add products to table
    filteredProducts.forEach(product => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${product.id}</td>
            <td>${product.name}</td>
            <td>${product.category}</td>
            <td>${formatCurrency(product.price)}</td>
            <td>${product.stock}</td>
            <td>${product.sales}</td>
            <td>
                <button class="btn btn-primary btn-sm edit-btn" data-id="${product.id}">Edit</button>
                <button class="btn btn-danger btn-sm delete-btn" data-id="${product.id}">Delete</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    // Add event listeners to edit buttons
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            editProduct(this.getAttribute('data-id'));
        });
    });

    // Add event listeners to delete buttons
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            deleteProduct(this.getAttribute('data-id'));
        });
    });
}

function saveProduct() {
    const name = document.getElementById('productName').value;
    const category = document.getElementById('productCategory').value;
    const price = parseFloat(document.getElementById('productPrice').value);
    const stock = parseInt(document.getElementById('productStock').value);
    const description = document.getElementById('productDescription').value;

    if (!name || !category || isNaN(price) || isNaN(stock)) {
        showAlert('Please fill in all required fields', 'danger');
        return;
    }

    const products = JSON.parse(localStorage.getItem('ecommerceProducts')) || [];
    const newId = products.length > 0 ? Math.max(...products.map(p => p.id)) + 1 : 1;

    const newProduct = {
        id: newId,
        name,
        category,
        price,
        stock,
        sales: 0,
        description
    };

    products.push(newProduct);
    localStorage.setItem('ecommerceProducts', JSON.stringify(products));

    // Close modal and reset form
    document.getElementById('addProductModal').style.display = 'none';
    document.getElementById('addProductForm').reset();

    // Reload products
    loadProducts();

    showAlert('Product added successfully', 'success');
}

function editProduct(productId) {
    const products = JSON.parse(localStorage.getItem('ecommerceProducts')) || [];
    const product = products.find(p => p.id === parseInt(productId));

    if (!product) {
        showAlert('Product not found', 'danger');
        return;
    }

    // Fill the edit form with product data
    document.getElementById('editProductId').value = product.id;
    document.getElementById('editProductName').value = product.name;
    document.getElementById('editProductCategory').value = product.category;
    document.getElementById('editProductPrice').value = product.price;
    document.getElementById('editProductStock').value = product.stock;
    document.getElementById('editProductSales').value = product.sales;
    document.getElementById('editProductDescription').value = product.description || '';

    // Show the edit modal
    document.getElementById('editProductModal').style.display = 'flex';
}

function updateProduct() {
    const id = parseInt(document.getElementById('editProductId').value);
    const name = document.getElementById('editProductName').value;
    const category = document.getElementById('editProductCategory').value;
    const price = parseFloat(document.getElementById('editProductPrice').value);
    const stock = parseInt(document.getElementById('editProductStock').value);
    const sales = parseInt(document.getElementById('editProductSales').value);
    const description = document.getElementById('editProductDescription').value;

    if (!name || !category || isNaN(price) || isNaN(stock) || isNaN(sales)) {
        showAlert('Please fill in all required fields', 'danger');
        return;
    }

    const products = JSON.parse(localStorage.getItem('ecommerceProducts')) || [];
    const productIndex = products.findIndex(p => p.id === id);

    if (productIndex === -1) {
        showAlert('Product not found', 'danger');
        return;
    }

    // Update the product
    products[productIndex] = {
        id,
        name,
        category,
        price,
        stock,
        sales,
        description
    };

    localStorage.setItem('ecommerceProducts', JSON.stringify(products));

    // Close modal
    document.getElementById('editProductModal').style.display = 'none';

    // Reload products
    loadProducts();

    showAlert('Product updated successfully', 'success');
}

function deleteProduct(productId) {
    if (!confirm('Are you sure you want to delete this product?')) {
        return;
    }

    const products = JSON.parse(localStorage.getItem('ecommerceProducts')) || [];
    const updatedProducts = products.filter(p => p.id !== parseInt(productId));

    localStorage.setItem('ecommerceProducts', JSON.stringify(updatedProducts));

    // Reload products
    loadProducts();

    showAlert('Product deleted successfully', 'success');
}

function showAlert(message, type) {
    const alertContainer = document.getElementById('alertContainer');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;
    alertContainer.appendChild(alert);

    // Remove alert after 3 seconds
    setTimeout(() => {
        alert.remove();
    }, 3000);
}