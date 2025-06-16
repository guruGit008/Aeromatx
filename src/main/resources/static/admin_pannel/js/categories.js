document.addEventListener('DOMContentLoaded', function() {
    // Load categories table
    loadCategories();

    // Search functionality
    document.getElementById('categorySearch').addEventListener('input', function() {
        loadCategories(this.value);
    });

    // Save category button
    document.getElementById('saveCategoryBtn').addEventListener('click', function() {
        saveCategory();
    });

    // Update category button
    document.getElementById('updateCategoryBtn').addEventListener('click', function() {
        updateCategory();
    });

    // Generate slug from name
    document.getElementById('categoryName').addEventListener('input', function() {
        const slug = this.value.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
        document.getElementById('categorySlug').value = slug;
    });

    document.getElementById('editCategoryName').addEventListener('input', function() {
        const slug = this.value.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
        document.getElementById('editCategorySlug').value = slug;
    });
});

function loadCategories(searchTerm = '') {
    const categories = JSON.parse(localStorage.getItem('ecommerceCategories')) || [];
    const tbody = document.querySelector('#categoriesTable tbody');

    // Clear existing rows
    tbody.innerHTML = '';

    // Filter categories based on search term
    const filteredCategories = categories.filter(category => {
        return searchTerm === '' ||
            category.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            category.slug.toLowerCase().includes(searchTerm.toLowerCase());
    });

    // Add categories to table
    filteredCategories.forEach(category => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${category.id}</td>
            <td>${category.name}</td>
            <td>${category.slug}</td>
            <td><span class="status ${category.status}">${category.status.charAt(0).toUpperCase() + category.status.slice(1)}</span></td>
            <td>
                <button class="btn btn-primary btn-sm edit-btn" data-id="${category.id}">Edit</button>
                <button class="btn btn-danger btn-sm delete-btn" data-id="${category.id}">Delete</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    // Add event listeners to edit buttons
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            editCategory(this.getAttribute('data-id'));
        });
    });

    // Add event listeners to delete buttons
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            deleteCategory(this.getAttribute('data-id'));
        });
    });
}

function saveCategory() {
    const name = document.getElementById('categoryName').value;
    const slug = document.getElementById('categorySlug').value;
    const description = document.getElementById('categoryDescription').value;
    const status = document.getElementById('categoryStatus').value;

    if (!name || !slug) {
        showAlert('Please fill in all required fields', 'danger');
        return;
    }

    const categories = JSON.parse(localStorage.getItem('ecommerceCategories')) || [];
    const newId = categories.length > 0 ? Math.max(...categories.map(c => c.id)) + 1 : 1;

    const newCategory = {
        id: newId,
        name,
        slug,
        description,
        status
    };

    categories.push(newCategory);
    localStorage.setItem('ecommerceCategories', JSON.stringify(categories));

    // Close modal and reset form
    document.getElementById('addCategoryModal').style.display = 'none';
    document.getElementById('addCategoryForm').reset();

    // Reload categories
    loadCategories();

    showAlert('Category added successfully', 'success');
}

function editCategory(categoryId) {
    const categories = JSON.parse(localStorage.getItem('ecommerceCategories')) || [];
    const category = categories.find(c => c.id === parseInt(categoryId));

    if (!category) {
        showAlert('Category not found', 'danger');
        return;
    }

    // Fill the edit form with category data
    document.getElementById('editCategoryId').value = category.id;
    document.getElementById('editCategoryName').value = category.name;
    document.getElementById('editCategorySlug').value = category.slug;
    document.getElementById('editCategoryDescription').value = category.description || '';
    document.getElementById('editCategoryStatus').value = category.status;

    // Show the edit modal
    document.getElementById('editCategoryModal').style.display = 'flex';
}

function updateCategory() {
    const id = parseInt(document.getElementById('editCategoryId').value);
    const name = document.getElementById('editCategoryName').value;
    const slug = document.getElementById('editCategorySlug').value;
    const description = document.getElementById('editCategoryDescription').value;
    const status = document.getElementById('editCategoryStatus').value;

    if (!name || !slug) {
        showAlert('Please fill in all required fields', 'danger');
        return;
    }

    const categories = JSON.parse(localStorage.getItem('ecommerceCategories')) || [];
    const categoryIndex = categories.findIndex(c => c.id === id);

    if (categoryIndex === -1) {
        showAlert('Category not found', 'danger');
        return;
    }

    // Update the category
    categories[categoryIndex] = {
        id,
        name,
        slug,
        description,
        status
    };

    localStorage.setItem('ecommerceCategories', JSON.stringify(categories));

    // Close modal
    document.getElementById('editCategoryModal').style.display = 'none';

    // Reload categories
    loadCategories();

    showAlert('Category updated successfully', 'success');
}

function deleteCategory(categoryId) {
    if (!confirm('Are you sure you want to delete this category? Products in this category will not be deleted.')) {
        return;
    }

    const categories = JSON.parse(localStorage.getItem('ecommerceCategories')) || [];
    const updatedCategories = categories.filter(c => c.id !== parseInt(categoryId));

    localStorage.setItem('ecommerceCategories', JSON.stringify(updatedCategories));

    // Reload categories
    loadCategories();

    showAlert('Category deleted successfully', 'success');
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