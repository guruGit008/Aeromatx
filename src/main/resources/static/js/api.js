// API Configuration
const API_CONFIG = {
    BASE_URL: 'http://localhost:8080/api',
    TIMEOUT: 10000
};

// API Utility Functions
class APIService {
    constructor() {
        this.baseURL = API_CONFIG.BASE_URL;
    }

    async makeRequest(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const token = localStorage.getItem('authToken');
        
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        };

        const finalOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers
            }
        };

        try {
            const response = await fetch(url, finalOptions);
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    // Authentication
    async login(credentials) {
        return this.makeRequest('/auth/login', {
            method: 'POST',
            body: JSON.stringify(credentials)
        });
    }

    async register(userData) {
        return this.makeRequest('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    // Products
    async getProducts() {
        return this.makeRequest('/products');
    }

    async getProduct(id) {
        return this.makeRequest(`/products/${id}`);
    }

    async searchProducts(keyword, page = 0, size = 10) {
        return this.makeRequest(`/products/search?keyword=${keyword}&page=${page}&size=${size}`);
    }

    async getProductsByCategory(categoryId) {
        return this.makeRequest(`/products/category/${categoryId}`);
    }

    // Cart
    async getCart() {
        return this.makeRequest('/cart');
    }

    async addToCart(productId, quantity) {
        return this.makeRequest('/cart/add', {
            method: 'POST',
            body: JSON.stringify({ productId, quantity })
        });
    }

    async updateCartItem(itemId, quantity) {
        return this.makeRequest(`/cart/items/${itemId}`, {
            method: 'PUT',
            body: JSON.stringify({ quantity })
        });
    }

    async removeFromCart(itemId) {
        return this.makeRequest(`/cart/items/${itemId}`, {
            method: 'DELETE'
        });
    }
}