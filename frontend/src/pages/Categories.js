import React, { useState, useEffect } from 'react';
import { categoryService } from '../services/api';
import './Categories.css';

const Categories = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    description: ''
  });

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      setLoading(true);
      const data = await categoryService.getAllCategories();
      setCategories(data);
    } catch (err) {
      setError('Failed to load categories');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCategory) {
        await categoryService.updateCategory(editingCategory.id, formData);
      } else {
        await categoryService.addCategory(formData);
      }
      await loadCategories();
      resetForm();
    } catch (err) {
      setError('Failed to save category');
    }
  };

  const handleEdit = (category) => {
    setEditingCategory(category);
    setFormData({
      name: category.name,
      description: category.description || ''
    });
    setShowAddForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this category?')) {
      try {
        await categoryService.deleteCategory(id);
        await loadCategories();
      } catch (err) {
        setError('Failed to delete category');
      }
    }
  };

  const resetForm = () => {
    setFormData({ name: '', description: '' });
    setEditingCategory(null);
    setShowAddForm(false);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <span className="loading"></span>
        <p>Loading categories...</p>
      </div>
    );
  }

  return (
    <div className="categories">
      <div className="categories-header">
        <h1>Categories</h1>
        <button
          className="btn btn-primary"
          onClick={() => setShowAddForm(!showAddForm)}
        >
          {showAddForm ? 'Cancel' : 'Add Category'}
        </button>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
          <button onClick={() => setError('')} className="alert-close">&times;</button>
        </div>
      )}

      {showAddForm && (
        <div className="category-form-container">
          <form onSubmit={handleSubmit} className="category-form">
            <h3>{editingCategory ? 'Edit Category' : 'Add New Category'}</h3>

            <div className="form-group">
              <label htmlFor="name">Category Name *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                required
                placeholder="Enter category name"
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">Description</label>
              <textarea
                id="description"
                name="description"
                value={formData.description}
                onChange={handleInputChange}
                placeholder="Enter category description (optional)"
                rows="3"
              />
            </div>

            <div className="form-actions">
              <button type="submit" className="btn btn-primary">
                {editingCategory ? 'Update Category' : 'Add Category'}
              </button>
              <button type="button" className="btn btn-secondary" onClick={resetForm}>
                Cancel
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="categories-list">
        {categories.length === 0 ? (
          <div className="empty-state">
            <p>No categories found. <button className="link-button" onClick={() => setShowAddForm(true)}>Add your first category</button></p>
          </div>
        ) : (
          <div className="categories-list">
            {categories.map(category => (
              <div key={category.id} className="category-card">
                <div className="category-info">
                  <h3>{category.name}</h3>
                </div>
                <div className="category-description-container">
                  {category.description && (
                    <p className="category-description">{category.description}</p>
                  )}
                </div>
                <div className="category-actions">
                  <button
                    className="btn btn-small btn-secondary"
                    onClick={() => handleEdit(category)}
                  >
                    Edit
                  </button>
                  <button
                    className="btn btn-small btn-danger"
                    onClick={() => handleDelete(category.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Categories;
