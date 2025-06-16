document.addEventListener('DOMContentLoaded', () => {
    const customersTableBody = document.querySelector('#customersTable tbody');
    const customerSearchInput = document.getElementById('customerSearch');
    const addCustomerModal = document.getElementById('addCustomerModal');
    const addCustomerForm = document.getElementById('addCustomerForm');
    const saveCustomerBtn = document.getElementById('saveCustomerBtn');
    const editCustomerModal = document.getElementById('editCustomerModal');
    const editCustomerForm = document.getElementById('editCustomerForm');
    const updateCustomerBtn = document.getElementById('updateCustomerBtn');
    const alertContainer = document.getElementById('alertContainer');

    // --- Common Modal Functions (if not already in main.js) ---
    document.querySelectorAll('[data-toggle="modal"]').forEach(button => {
        button.addEventListener('click', (event) => {
            const targetId = event.target.getAttribute('data-target');
            document.querySelector(targetId).style.display = 'block';
        });
    });

    document.querySelectorAll('.modal .close').forEach(span => {
        span.addEventListener('click', (event) => {
            event.target.closest('.modal').style.display = 'none';
        });
    });

    window.addEventListener('click', (event) => {
        document.querySelectorAll('.modal').forEach(modal => {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        });
    });

    let customersData = []; // Store fetched customers here

    // Function to fetch customers from the backend API
    async function fetchCustomers(searchTerm = '') {
        try {
            // Adjust the API endpoint based on your security setup.
            // If the user has to be an ADMIN, you'll need a JWT token here.
            // For now, assuming you've adjusted security for /by-role to permit all for simplicity.
            const url = `http://localhost:8080/api/users/by-role?role=CUSTOMER`;
            const response = await fetch(url);

            if (!response.ok) {
                if (response.status === 204) { // No Content
                    customersData = [];
                    renderCustomers([]);
                    showAlert('No customers found.', 'info');
                    return;
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            customersData = await response.json();
            renderCustomers(customersData, searchTerm); // Pass searchTerm for client-side filtering
        } catch (error) {
            console.error('Error fetching customers:', error);
            showAlert('Error loading customers. Please try again later.', 'danger');
            customersData = []; // Clear data on error
            renderCustomers([]);
        }
    }

    // Function to render customers in the table
    function renderCustomers(customers, searchTerm = '') {
        customersTableBody.innerHTML = ''; // Clear existing rows

        const filteredCustomers = customers.filter(customer => {
            const username = customer.username ? customer.username.toLowerCase() : '';
            const email = customer.email ? customer.email.toLowerCase() : '';
            return searchTerm === '' ||
                username.includes(searchTerm.toLowerCase()) ||
                email.includes(searchTerm.toLowerCase());
        });

        if (filteredCustomers.length === 0) {
            customersTableBody.innerHTML = '<tr><td colspan="4">No customers found.</td></tr>'; // Adjusted colspan from 5 to 4
            return;
        }

        filteredCustomers.forEach(customer => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${customer.id}</td>
                <td>${customer.username}</td>
                <td>${customer.email}</td>
                <td>${customer.company || 'N/A'}</td> <td>
                    <button class="btn btn-sm btn-info" onclick="editCustomer(${customer.id})">Edit</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteCustomer(${customer.id})">Delete</button>
                </td>
            `;
            customersTableBody.appendChild(row);
        });
    }

    // Function to populate the edit modal
    window.editCustomer = (id) => {
        const customer = customersData.find(c => c.id === id);
        if (customer) {
            document.getElementById('editCustomerId').value = customer.id;
            document.getElementById('editCustomerName').value = customer.username;
            document.getElementById('editCustomerEmail').value = customer.email;
            // Removed: document.getElementById('editCustomerPhone').value = customer.phone || '';
            editCustomerModal.style.display = 'block';
        } else {
            showAlert('Customer not found for editing.', 'danger');
        }
    };

    // Function to handle updating a customer
    updateCustomerBtn.addEventListener('click', async() => {
        const id = document.getElementById('editCustomerId').value;
        const username = document.getElementById('editCustomerName').value.trim();
        const email = document.getElementById('editCustomerEmail').value.trim();
        // Removed: const phone = document.getElementById('editCustomerPhone').value.trim();

        if (!username || !email) {
            showAlert('Full Name and Email are required!', 'danger');
            return;
        }

        // Prepare the data to send to the backend
        const updatedData = {
            username: username,
            email: email,
            // phone: phone // Removed phone from the payload
        };

        try {
            const response = await fetch(`http://localhost:8080/api/users/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                        // Add Authorization header if your API requires JWT token
                        // 'Authorization': 'Bearer YOUR_JWT_TOKEN_HERE'
                },
                body: JSON.stringify(updatedData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to update customer: ${response.status} ${errorText}`);
            }

            showAlert('Customer updated successfully!', 'success');
            editCustomerModal.style.display = 'none';
            fetchCustomers(); // Re-fetch all customers to update the table
        } catch (error) {
            console.error('Error updating customer:', error);
            showAlert('Error updating customer. Please try again.', 'danger');
        }
    });

    // Function to handle deleting a customer
    window.deleteCustomer = async(id) => {
        if (!confirm('Are you sure you want to delete this customer? This action cannot be undone.')) {
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/api/users/${id}`, {
                method: 'DELETE',
                headers: {
                    // Add Authorization header if your API requires JWT token
                    // 'Authorization': 'Bearer YOUR_JWT_TOKEN_HERE'
                }
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to delete customer: ${response.status} ${errorText}`);
            }

            showAlert('Customer deleted successfully!', 'success');
            fetchCustomers(); // Re-fetch all customers to update the table
        } catch (error) {
            console.error('Error deleting customer:', error);
            showAlert('Error deleting customer. Please try again.', 'danger');
        }
    };

    // Function to show alerts (keep existing if already working)
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

    // Initial fetch of customers when the page loads
    fetchCustomers();

    // Search functionality
    document.getElementById('customerSearch').addEventListener('input', function() {
        renderCustomers(customersData, this.value); // Re-render with current data and search term
    });

    // NOTE: The `saveCustomer` function is for adding new customers.
    // Ensure you have a backend endpoint for this (e.g., POST /api/auth/signup or POST /api/users)
    // and modify saveCustomer accordingly if you want to enable adding customers.
    // For now, if saveCustomer is linked to a button, it's likely still using local storage.
    // If you need a backend save, let me know!
    // document.getElementById('saveCustomerBtn').addEventListener('click', function() {
    //     saveCustomer();
    // });

    // Function to handle adding a new customer (assuming a /api/auth/register endpoint for new user creation)
    // This function is provided as a starting point. You'll need to implement the backend /api/auth/register
    // or a similar endpoint to create new users.
    saveCustomerBtn.addEventListener('click', async() => {
        const username = document.getElementById('customerName').value.trim();
        const email = document.getElementById('customerEmail').value.trim();
        const company = document.getElementById('customerCompany').value.trim();
        const password = document.getElementById('customerPassword').value.trim();

        if (!username || !email || !password) {
            showAlert('Full Name, Email, and Password are required for new customer!', 'danger');
            return;
        }

        const newCustomerData = {
            username: username,
            email: email,
            password: password,
            company: company,
            role: ["customer"] // Assuming "customer" role for new signups
        };

        try {
            // Replace with your actual registration/signup endpoint
            const response = await fetch('http://localhost:8080/api/auth/register', { // Or /api/users if admin can add users directly
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newCustomerData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to add new customer: ${response.status} ${errorText}`);
            }

            showAlert('New customer added successfully!', 'success');
            addCustomerModal.style.display = 'none';
            addCustomerForm.reset(); // Clear the form
            fetchCustomers(); // Re-fetch and update table
        } catch (error) {
            console.error('Error adding new customer:', error);
            showAlert(`Error adding new customer: ${error.message}`, 'danger');
        }
    });

});