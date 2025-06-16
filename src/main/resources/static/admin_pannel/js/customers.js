document.addEventListener('DOMContentLoaded', function() {
    // Load customers table
    loadCustomers();

    // Search functionality
    document.getElementById('customerSearch').addEventListener('input', function() {
        loadCustomers(this.value);
    });

    // Save customer button
    document.getElementById('saveCustomerBtn').addEventListener('click', function() {
        saveCustomer();
    });

    // Update customer button
    document.getElementById('updateCustomerBtn').addEventListener('click', function() {
        updateCustomer();
    });
});

function loadCustomers(searchTerm = '') {
    const customers = JSON.parse(localStorage.getItem('ecommerceCustomers')) || [];
    const tbody = document.querySelector('#customersTable tbody');

    // Clear existing rows
    tbody.innerHTML = '';

    // Filter customers based on search term
    const filteredCustomers = customers.filter(customer => {
        return searchTerm === '' ||
            customer.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            customer.email.toLowerCase().includes(searchTerm.toLowerCase());
    });

    // Add customers to table
    filteredCustomers.forEach(customer => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${customer.id}</td>
            <td>${customer.name}</td>
            <td>${customer.email}</td>
            <td>${customer.phone || 'N/A'}</td>
            <td>${customer.orders}</td>
            <td>${formatDate(customer.registered)}</td>
            <td>
                <button class="btn btn-primary btn-sm edit-btn" data-id="${customer.id}">Edit</button>
                <button class="btn btn-danger btn-sm delete-btn" data-id="${customer.id}">Delete</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    // Add event listeners to edit buttons
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            editCustomer(this.getAttribute('data-id'));
        });
    });

    // Add event listeners to delete buttons
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            deleteCustomer(this.getAttribute('data-id'));
        });
    });
}

function saveCustomer() {
    const name = document.getElementById('customerName').value;
    const email = document.getElementById('customerEmail').value;
    const phone = document.getElementById('customerPhone').value;

    if (!name || !email) {
        showAlert('Please fill in all required fields', 'danger');
        return;
    }

    const customers = JSON.parse(localStorage.getItem('ecommerceCustomers')) || [];
    const newId = customers.length > 0 ? Math.max(...customers.map(c => c.id)) + 1 : 1;

    const newCustomer = {
        id: newId,
        name,
        email,
        phone,
        orders: 0,
        registered: new Date().toISOString().split('T')[0]
    };

    customers.push(newCustomer);
    localStorage.setItem('ecommerceCustomers', JSON.stringify(customers));

    // Close modal and reset form
    document.getElementById('addCustomerModal').style.display = 'none';
    document.getElementById('addCustomerForm').reset();

    // Reload customers
    loadCustomers();

    showAlert('Customer added successfully', 'success');
}

function editCustomer(customerId) {
    const customers = JSON.parse(localStorage.getItem('ecommerceCustomers')) || [];
    const customer = customers.find(c => c.id === parseInt(customerId));

    if (!customer) {
        showAlert('Customer not found', 'danger');
        return;
    }

    // Fill the edit form with customer data
    document.getElementById('editCustomerId').value = customer.id;
    document.getElementById('editCustomerName').value = customer.name;
    document.getElementById('editCustomerEmail').value = customer.email;
    document.getElementById('editCustomerPhone').value = customer.phone || '';

    // Show the edit modal
    document.getElementById('editCustomerModal').style.display = 'flex';
}

function updateCustomer() {
    const id = parseInt(document.getElementById('editCustomerId').value);
    const name = document.getElementById('editCustomerName').value;
    const email = document.getElementById('editCustomerEmail').value;
    const phone = document.getElementById('editCustomerPhone').value;

    if (!name || !email) {
        showAlert('Please fill in all required fields', 'danger');
        return;
    }

    const customers = JSON.parse(localStorage.getItem('ecommerceCustomers')) || [];
    const customerIndex = customers.findIndex(c => c.id === id);

    if (customerIndex === -1) {
        showAlert('Customer not found', 'danger');
        return;
    }

    // Update the customer (preserve orders and registration date)
    customers[customerIndex] = {
        ...customers[customerIndex],
        name,
        email,
        phone
    };

    localStorage.setItem('ecommerceCustomers', JSON.stringify(customers));

    // Close modal
    document.getElementById('editCustomerModal').style.display = 'none';

    // Reload customers
    loadCustomers();

    showAlert('Customer updated successfully', 'success');
}

function deleteCustomer(customerId) {
    if (!confirm('Are you sure you want to delete this customer? This action cannot be undone.')) {
        return;
    }

    const customers = JSON.parse(localStorage.getItem('ecommerceCustomers')) || [];
    const updatedCustomers = customers.filter(c => c.id !== parseInt(customerId));

    localStorage.setItem('ecommerceCustomers', JSON.stringify(updatedCustomers));

    // Reload customers
    loadCustomers();

    showAlert('Customer deleted successfully', 'success');
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