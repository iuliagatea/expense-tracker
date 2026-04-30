import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import FileUploadModal from '../components/FileUploadModal';
import { expenseService } from '../services/api';
import './ImportExpenses.css';

const ImportExpenses = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [importResults, setImportResults] = useState(null);
  const [error, setError] = useState('');

  const handleFileUpload = async (file) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await expenseService.importExpenses(formData);
      setImportResults(response);
      setError('');
    } catch (err) {
      setError(err.message || 'Failed to import expenses');
      throw err;
    }
  };

  const openModal = () => {
    setIsModalOpen(true);
    setImportResults(null);
    setError('');
  };

  const closeModal = () => {
    setIsModalOpen(false);
  };

  return (
    <div className="import-expenses">
      <div className="expenses-header">
        <h1>Import Transactions</h1>
        <Link to="/expenses" className="btn btn-secondary">
          Back to Transactions
        </Link>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      <div className="import-section">
        <div className="import-card">
          <h2>Import from CSV File</h2>
          <p>Upload a CSV file containing your transaction data. The file should follow the specified format with headers.</p>

          <button
            className="btn btn-primary btn-large"
            onClick={openModal}
          >
            Choose File & Import
          </button>

          <div className="import-info">
            <h3>Supported Format:</h3>
            <div className="format-example">
              <code>
                date,amount,category,account,note,expenseType<br/>
                2024-01-15,25.50,Food,Cash,Lunch with friends,0<br/>
                2024-01-16,1500.00,Salary,Bank,Monthly salary,1<br/>
                2024-01-17,-45.00,Transport,Credit Card,Bus fare,0
              </code>
            </div>

            <div className="field-descriptions">
              <h4>Field Descriptions:</h4>
              <ul>
                <li><strong>date:</strong> Transaction date (YYYY-MM-DD)</li>
                <li><strong>amount:</strong> Transaction amount (positive for income, negative for expenses)</li>
                <li><strong>category:</strong> Category name (will be created if doesn't exist)</li>
                <li><strong>account:</strong> Account name (optional)</li>
                <li><strong>note:</strong> Transaction description (optional)</li>
                <li><strong>expenseType:</strong> 0 for Expense, 1 for Income (optional, auto-detected from amount)</li>
              </ul>
            </div>
          </div>
        </div>

        {importResults && (
          <div className="import-results">
            <h2>Import Results</h2>
            <div className="results-summary">
              <div className="result-item">
                <span className="result-label">Total Records:</span>
                <span className="result-value">{importResults.totalRecords}</span>
              </div>
              <div className="result-item">
                <span className="result-label">Successfully Imported:</span>
                <span className="result-value success">{importResults.successCount}</span>
              </div>
              <div className="result-item">
                <span className="result-label">Failed Records:</span>
                <span className="result-value error">{importResults.errorCount}</span>
              </div>
            </div>

            {importResults.errors && importResults.errors.length > 0 && (
              <div className="error-details">
                <h3>Import Errors:</h3>
                <ul>
                  {importResults.errors.map((error, index) => (
                    <li key={index}>{error}</li>
                  ))}
                </ul>
              </div>
            )}

            <div className="import-actions">
              <Link to="/expenses" className="btn btn-primary">
                View Imported Transactions
              </Link>
              <button
                className="btn btn-secondary"
                onClick={() => setImportResults(null)}
              >
                Import Another File
              </button>
            </div>
          </div>
        )}
      </div>

      <FileUploadModal
        isOpen={isModalOpen}
        onClose={closeModal}
        onUpload={handleFileUpload}
      />
    </div>
  );
};

export default ImportExpenses;
