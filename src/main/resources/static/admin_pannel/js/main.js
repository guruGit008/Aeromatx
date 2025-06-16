// Initialize local storage with sample data if empty
function initializeData() {
    if (!localStorage.getItem('ecommerceProducts')) {
        const products = [
            { id: 1, name: 'Premium Laptop X1', category: 'Laptops', price: 1299.99, stock: 45, sales: 128, description: 'High performance laptop with 16GB RAM and 1TB SSD', image: '' },
            { id: 2, name: 'Smartphone Pro 12', category: 'Smartphones', price: 899.99, stock: 32, sales: 215, description: 'Latest smartphone with 5G and 128GB storage', image: '' },
            { id: 3, name: '4K Camera D500', category: 'Cameras', price: 599.99, stock: 18, sales: 76, description: 'Professional 4K camera with 20MP sensor', image: '' },
            { id: 4, name: 'Wireless Earbuds', category: 'Accessories', price: 129.99, stock: 67, sales: 342, description: 'Noise cancelling wireless earbuds with 20h battery', image: '' },
            { id: 5, name: 'Smart Watch 3', category: 'Accessories', price: 249.99, stock: 29, sales: 183, description: 'Fitness tracker with heart rate monitor', image: '' }
        ];
        localStorage.setItem('ecommerceProducts', JSON.stringify(products));
    }

    if (!localStorage.getItem('ecommerceCategories')) {
        const categories = [
            { id: 1, name: 'Laptops', slug: 'laptops', description: 'High performance laptops', status: 'active' },
            { id: 2, name: 'Smartphones', slug: 'smartphones', description: 'Latest smartphones', status: 'active' },
            { id: 3, name: 'Cameras', slug: 'cameras', description: 'Professional cameras', status: 'active' },
            { id: 4, name: 'Accessories', slug: 'accessories', description: 'Tech accessories', status: 'active' }
        ];
        localStorage.setItem('ecommerceCategories', JSON.stringify(categories));
    }

    if (!localStorage.getItem('ecommerceOrders')) {
        const orders = [{
                id: 'ORD-2025-001',
                customer: 'John Smith',
                date: '2025-06-12',
                amount: 245.99,
                status: 'completed',
                items: [
                    { product: 'Smartphone Pro 12', quantity: 1, price: 899.99 },
                    { product: 'Wireless Earbuds', quantity: 1, price: 129.99 }
                ]
            },
            {
                id: 'ORD-2025-002',
                customer: 'Sarah Johnson',
                date: '2025-06-11',
                amount: 189.50,
                status: 'shipped',
                items: [
                    { product: '4K Camera D500', quantity: 1, price: 599.99 }
                ]
            },
            {
                id: 'ORD-2025-003',
                customer: 'Michael Brown',
                date: '2025-06-10',
                amount: 320.75,
                status: 'pending',
                items: [
                    { product: 'Premium Laptop X1', quantity: 1, price: 1299.99 }
                ]
            },
            {
                id: 'ORD-2025-004',
                customer: 'Emily Davis',
                date: '2025-06-09',
                amount: 145.20,
                status: 'cancelled',
                items: [
                    { product: 'Smart Watch 3', quantity: 1, price: 249.99 }
                ]
            },
            {
                id: 'ORD-2025-005',
                customer: 'Robert Wilson',
                date: '2025-06-08',
                amount: 275.30,
                status: 'completed',
                items: [
                    { product: 'Wireless Earbuds', quantity: 2, price: 129.99 }
                ]
            }
        ];
        localStorage.setItem('ecommerceOrders', JSON.stringify(orders));
    }

    if (!localStorage.getItem('ecommerceCustomers')) {
        const customers = [
            { id: 1, name: 'John Smith', email: 'john@example.com', phone: '1234567890', orders: 5, registered: '2025-01-15' },
            { id: 2, name: 'Sarah Johnson', email: 'sarah@example.com', phone: '2345678901', orders: 3, registered: '2025-02-20' },
            { id: 3, name: 'Michael Brown', email: 'michael@example.com', phone: '3456789012', orders: 2, registered: '2025-03-10' },
            { id: 4, name: 'Emily Davis', email: 'emily@example.com', phone: '4567890123', orders: 1, registered: '2025-04-05' },
            { id: 5, name: 'Robert Wilson', email: 'robert@example.com', phone: '5678901234', orders: 4, registered: '2025-05-12' }
        ];
        localStorage.setItem('ecommerceCustomers', JSON.stringify(customers));
    }
}

// Initialize data when the app loads
document.addEventListener('DOMContentLoaded', function() {
    initializeData();

    // Set active menu item based on current page
    const currentPage = window.location.pathname.split('/').pop().replace('.html', '');
    if (currentPage === 'index' || currentPage === '') {
        document.querySelector('.sidebar-menu a[href="index.html"]').classList.add('active');
    } else {
        const menuItem = document.querySelector(`.sidebar-menu a[href="${currentPage}.html"]`);
        if (menuItem) {
            menuItem.classList.add('active');
        }
    }

    // Initialize modals
    document.querySelectorAll('[data-toggle="modal"]').forEach(button => {
        button.addEventListener('click', function() {
            const target = this.getAttribute('data-target');
            document.querySelector(target).style.display = 'flex';
        });
    });

    // Close modals when clicking close button or outside
    document.querySelectorAll('.modal, .close').forEach(element => {
        element.addEventListener('click', function(e) {
            if (e.target === this || e.target.classList.contains('close')) {
                this.closest('.modal').style.display = 'none';
            }
        });
    });

    // Prevent modal content from closing when clicking inside
    document.querySelectorAll('.modal-content').forEach(content => {
        content.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    });
});

// Helper function to format date
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// Helper function to format currency
function formatCurrency(amount) {
    return '$' + amount.toFixed(2);
}