document.addEventListener('DOMContentLoaded', function() {
    // Load orders table
    loadOrders();

    // Search functionality
    document.getElementById('orderSearch').addEventListener('input', function() {
        loadOrders(this.value);
    });

    // Status filter
    document.getElementById('statusFilter').addEventListener('change', function() {
        loadOrders('', this.value);
    });

    // Update order status button
    document.getElementById('updateOrderStatusBtn').addEventListener('click', function() {
        const orderId = document.getElementById('orderDetailsModal').getAttribute('data-order-id');
        document.getElementById('currentOrderId').value = orderId;
        document.getElementById('updateStatusModal').style.display = 'flex';
    });

    // Confirm status update button
    document.getElementById('confirmStatusUpdateBtn').addEventListener('click', function() {
        updateOrderStatus();
    });
});

function loadOrders(searchTerm = '', status = '') {
    const orders = JSON.parse(localStorage.getItem('ecommerceOrders')) || [];
    const customers = JSON.parse(localStorage.getItem('ecommerceCustomers')) || [];
    const tbody = document.querySelector('#ordersTable tbody');

    // Clear existing rows
    tbody.innerHTML = '';

    // Filter orders based on search term and status
    const filteredOrders = orders.filter(order => {
        const matchesSearch = searchTerm === '' ||
            order.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
            order.customer.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesStatus = status === '' || order.status === status;
        return matchesSearch && matchesStatus;
    });

    // Add orders to table
    filteredOrders.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${order.id}</td>
            <td>${order.customer}</td>
            <td>${formatDate(order.date)}</td>
            <td>${formatCurrency(order.amount)}</td>
            <td><span class="status ${order.status}">${order.status.charAt(0).toUpperCase() + order.status.slice(1)}</span></td>
            <td>
                <button class="btn btn-primary btn-sm view-btn" data-id="${order.id}">View</button>
            </td>
        `;
        tbody.appendChild(row);
    });

    // Add event listeners to view buttons
    document.querySelectorAll('.view-btn').forEach(button => {
        button.addEventListener('click', function() {
            viewOrderDetails(this.getAttribute('data-id'));
        });
    });
}

function viewOrderDetails(orderId) {
    const orders = JSON.parse(localStorage.getItem('ecommerceOrders')) || [];
    const order = orders.find(o => o.id === orderId);

    if (!order) {
        showAlert('Order not found', 'danger');
        return;
    }

    // Set the order ID on the modal for reference
    document.getElementById('orderDetailsModal').setAttribute('data-order-id', orderId);

    // Build order details content
    let content = `
        <div class="order-info">
            <div class="form-group">
                <label>Order ID</label>
                <p>${order.id}</p>
            </div>
            <div class="form-group">
                <label>Customer</label>
                <p>${order.customer}</p>
            </div>
            <div class="form-group">
                <label>Order Date</label>
                <p>${formatDate(order.date)}</p>
            </div>
            <div class="form-group">
                <label>Status</label>
                <p><span class="status ${order.status}">${order.status.charAt(0).toUpperCase() + order.status.slice(1)}</span></p>
            </div>
            <div class="form-group">
                <label>Total Amount</label>
                <p>${formatCurrency(order.amount)}</p>
            </div>
        </div>
        
        <h4>Order Items</h4>
        <table class="order-items">
            <thead>
                <tr>
                    <th>Product</th>
                    <th>Quantity</th>
                    <th>Price</th>
                    <th>Total</th>
                </tr>
            </thead>
            <tbody>
    `;

    order.items.forEach(item => {
        content += `
            <tr>
                <td>${item.product}</td>
                <td>${item.quantity}</td>
                <td>${formatCurrency(item.price)}</td>
                <td>${formatCurrency(item.price * item.quantity)}</td>
            </tr>
        `;
    });

    content += `
            </tbody>
        </table>
    `;

    document.getElementById('orderDetailsContent').innerHTML = content;
    document.getElementById('orderDetailsModal').style.display = 'flex';
}

function updateOrderStatus() {
    const orderId = document.getElementById('currentOrderId').value;
    const newStatus = document.getElementById('orderStatus').value;

    const orders = JSON.parse(localStorage.getItem('ecommerceOrders')) || [];
    const orderIndex = orders.findIndex(o => o.id === orderId);

    if (orderIndex === -1) {
        showAlert('Order not found', 'danger');
        return;
    }

    // Update the order status
    orders[orderIndex].status = newStatus;
    localStorage.setItem('ecommerceOrders', JSON.stringify(orders));

    // Close modals
    document.getElementById('updateStatusModal').style.display = 'none';
    document.getElementById('orderDetailsModal').style.display = 'none';

    // Reload orders
    loadOrders();

    showAlert('Order status updated successfully', 'success');
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