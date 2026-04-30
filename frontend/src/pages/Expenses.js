import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { expenseService } from '../services/api';
import './Expenses.css';

const Expenses = () => {
  const [expenses, setExpenses] = useState([]);
  const [filteredExpenses, setFilteredExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({
    category: '',
    date: '',
    month: '',
    expenseType: ''
  });

  useEffect(() => {
    loadExpenses();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [expenses, filters]);

  const loadExpenses = async () => {
    try {
      setLoading(true);
      const [expensesData, categoriesData] = await Promise.all([
        expenseService.getAllExpenses(),
        expenseService.getExpenseCategories()
      ]);

      setExpenses(expensesData);
      setCategories(categoriesData);
    } catch (err) {
      setError('Failed to load expenses');
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = [...expenses];

    if (filters.category) {
      filtered = filtered.filter(expense => expense.category.name === filters.category);
    }

    if (filters.date) {
      filtered = filtered.filter(expense => expense.date === filters.date);
    }

    if (filters.month) {
      filtered = filtered.filter(expense => expense.date.startsWith(filters.month));
    }

    if (filters.expenseType !== '') {
      filtered = filtered.filter(expense => expense.expenseType === parseInt(filters.expenseType));
    }

    setFilteredExpenses(filtered);
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const clearFilters = () => {
    setFilters({
      category: '',
      date: '',
      month: '',
      expenseType: ''
    });
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this expense?')) {
      try {
        await expenseService.deleteExpense(id);
        setExpenses(expenses.filter(expense => expense.id !== id));
      } catch (err) {
        setError('Failed to delete expense');
      }
    }
  };

  const handleExportPDF = async () => {
    try {
      const response = await fetch('/expenses/users/export/pdf', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
      });

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `expenses_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.pdf`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
      } else {
        setError('Failed to export PDF');
      }
    } catch (err) {
      setError('Failed to export PDF');
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString();
  };

  const getExpenseTypeLabel = (type) => {
    return type === 0 ? 'Expense' : 'Income';
  };

  const getRowClassName = (expenseType) => {
    return expenseType === 0 ? 'expense-row' : 'income-row';
  };

  const calculateSummary = () => {
    const expenses = filteredExpenses.filter(e => e.expenseType === 0);
    const income = filteredExpenses.filter(e => e.expenseType === 1);

    const totalExpenses = expenses.reduce((sum, e) => sum + parseFloat(e.amount || 0), 0);
    const totalIncome = income.reduce((sum, e) => sum + parseFloat(e.amount || 0), 0);

    return {
      totalExpenses,
      totalIncome,
      net: totalIncome - totalExpenses,
      expenseCount: expenses.length,
      incomeCount: income.length
    };
  };

  const summary = calculateSummary();

  if (loading) {
    return (
      <div className="loading-container">
        <span className="loading"></span>
        <p>Loading expenses...</p>
      </div>
    );
  }

  return (
    <div className="expenses">
      <div className="expenses-header">
        <h1>All Transactions</h1>
        <div className="expenses-actions">
          <button onClick={handleExportPDF} className="btn btn-secondary">
            Export PDF
          </button>
          <Link to="/expenses/add" className="btn btn-primary">
            Add New Transaction
          </Link>
        </div>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      {/* Filters */}
      <div className="filters">
        <h3>Filter Transactions</h3>
        <div className="filter-controls">
          <div className="form-group">
             <label htmlFor="expenseType">Type</label>
                <select
                      id="expenseType"
                      name="expenseType"
                      value={filters.expenseType}
                      onChange={handleFilterChange}
                >
                  <option value="">All Types</option>
                  <option value="0">Expense</option>
                  <option value="1">Income</option>
                </select>
          </div>

          <div className="form-group">
            <label htmlFor="category">Category</label>
            <select
              id="category"
              name="category"
              value={filters.category}
              onChange={handleFilterChange}
            >
              <option value="">All Categories</option>
              {categories.map(category => (
                <option key={category} value={category}>{category}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="date">Date</label>
            <input
              type="date"
              id="date"
              name="date"
              value={filters.date}
              onChange={handleFilterChange}
            />
          </div>

          <div className="form-group">
            <label htmlFor="month">Month</label>
            <input
              type="month"
              id="month"
              name="month"
              value={filters.month}
              onChange={handleFilterChange}
            />
          </div>

        <div className="form-group">
            <label>&nbsp;</label>
          <button onClick={clearFilters} className="btn btn-secondary">
            Clear Filters
          </button>
        </div>
        </div>
      </div>

      {/* Expenses Table */}
      <div className="expenses-table-container">
        {filteredExpenses.length === 0 ? (
          <div className="empty-state">
            <p>No expenses found. <Link to="/expenses/add">Add your first expense</Link></p>
          </div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Category</th>
                <th>Type</th>
                <th>Amount</th>
                <th>Account</th>
                <th>Note</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredExpenses.map(expense => (
                <tr key={expense.id} className={getRowClassName(expense.expenseType)}>
                  <td>{formatDate(expense.date)}</td>
                  <td>{expense.category.name}</td>
                  <td>
                    <span className="type-badge" data-type={expense.expenseType}>
                      {getExpenseTypeLabel(expense.expenseType)}
                    </span>
                  </td>
                  <td>${parseFloat(expense.amount).toFixed(2)}</td>
                  <td>{expense.account || '-'}</td>
                  <td>{expense.note || '-'}</td>
                  <td>
                    <div className="action-buttons">
                      <Link
                        to={`/expenses/edit/${expense.id}`}
                        className="btn btn-secondary btn-small"
                      >
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(expense.id)}
                        className="btn btn-danger btn-small"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Summary */}
      {filteredExpenses.length > 0 && (
        <div className="expenses-summary">
          <h3>Summary</h3>
          <div className="summary-grid">
            <div className="summary-card">
              <h4>Expenses</h4>
              <p className="summary-amount">${summary.totalExpenses.toFixed(2)}</p>
              <p className="summary-count">{summary.expenseCount} transaction{summary.expenseCount !== 1 ? 's' : ''}</p>
            </div>
            <div className="summary-card">
              <h4>Income</h4>
              <p className="summary-amount income">${summary.totalIncome.toFixed(2)}</p>
              <p className="summary-count">{summary.incomeCount} transaction{summary.incomeCount !== 1 ? 's' : ''}</p>
            </div>
            <div className="summary-card">
              <h4>Net</h4>
              <p className={`summary-amount ${summary.net >= 0 ? 'income' : 'expense'}`}>
                ${summary.net.toFixed(2)}
              </p>
              <p className="summary-label">Income - Expenses</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Expenses;
