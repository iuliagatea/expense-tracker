import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept, Authorization',
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authService = {
  login: async (credentials) => {
    const response = await api.post('/login', credentials);
    return response.data;
  },

  signup: async (userData) => {
    const response = await api.post('/signup', userData);
    return response.data;
  },
};

// Expense API
export const expenseService = {
  getAllExpenses: async () => {
    const response = await api.get('/expenses');
    return response.data;
  },

  getExpensesByType: async (type) => {
      const response = await api.get(`/expenses/expense_type/${type}`);
      return response.data;
    },

  getExpenseById: async (id) => {
    const response = await api.get(`/expenses/${id}`);
    return response.data;
  },

  getExpensesByDate: async (date) => {
    const response = await api.get(`/expenses/day/${date}`);
    return response.data;
  },

  getExpensesByCategoryAndMonth: async (category, month) => {
    const response = await api.get(`/expenses/category/${category}/month?month=${month}`);
    return response.data;
  },

  getExpenseCategories: async () => {
    const response = await api.get('/expenses/categories');
    return response.data;
  },

  createExpense: async (expense) => {
    const response = await api.post('/expenses', expense);
    return response.data;
  },

  updateExpense: async (id, expense) => {
    const response = await api.put(`/expenses/${id}`, expense);
    return response.data;
  },

  deleteExpense: async (id) => {
    const response = await api.delete(`/expenses/${id}`);
    return response.data;
  },

  importExpenses: async (formData) => {
    const response = await api.post('/expenses/import', formData, {
     method: 'POST',
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
      },
    });
    return response.data;
  },
};

// Category API
export const categoryService = {
  getAllCategories: async () => {
    const response = await api.get('/categories');
    return response.data;
  },

  addCategory: async (category) => {
    const response = await api.post('/categories', category);
    return response.data;
  },

  updateCategory: async (id, category) => {
    const response = await api.put(`/categories/${id}`, category);
    return response.data;
  },

  deleteCategory: async (id) => {
    const response = await api.delete(`/categories/${id}`);
    return response.data;
  },
};

export default api;
