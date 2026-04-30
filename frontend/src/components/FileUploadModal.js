import React, { useState } from 'react';
import './FileUploadModal.css';

const FileUploadModal = ({ isOpen, onClose, onUpload }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');

  const handleFileSelect = (event) => {
    const file = event.target.files[0];
    if (file) {
      // Validate file type
      const allowedTypes = ['text/csv', 'application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'];
      if (!allowedTypes.includes(file.type) && !file.name.endsWith('.csv')) {
        setError('Please select a CSV file');
        return;
      }

      // Validate file size (max 10MB)
      if (file.size > 10 * 1024 * 1024) {
        setError('File size must be less than 10MB');
        return;
      }

      setSelectedFile(file);
      setError('');
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) {
      setError('Please select a file first');
      return;
    }

    setUploading(true);
    setError('');

    try {
      await onUpload(selectedFile);
      setSelectedFile(null);
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to upload file');
    } finally {
      setUploading(false);
    }
  };

  const handleClose = () => {
    setSelectedFile(null);
    setError('');
    onClose();
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>Import Transactions</h2>
          <button className="modal-close" onClick={handleClose}>&times;</button>
        </div>

        <div className="modal-body">
          <div className="file-upload-section">
            <div className="file-input-container">
              <input
                type="file"
                id="file-input"
                accept=".csv"
                onChange={handleFileSelect}
                className="file-input"
              />
              <label htmlFor="file-input" className="file-input-label">
                {selectedFile ? selectedFile.name : 'Choose CSV file...'}
              </label>
            </div>

            {selectedFile && (
              <div className="file-info">
                <p><strong>File:</strong> {selectedFile.name}</p>
                <p><strong>Size:</strong> {(selectedFile.size / 1024).toFixed(1)} KB</p>
                <p><strong>Type:</strong> {selectedFile.type || 'CSV'}</p>
              </div>
            )}

            <div className="upload-info">
              <h4>CSV Format Requirements:</h4>
              <ul>
                <li><strong>date:</strong> YYYY-MM-DD format (required)</li>
                <li><strong>amount:</strong> Numeric value (required)</li>
                <li><strong>category:</strong> Category name (required)</li>
                <li><strong>account:</strong> Account name (optional)</li>
                <li><strong>note:</strong> Description (optional)</li>
                <li><strong>expenseType:</strong> 0 for Expense, 1 for Income (optional, defaults to 0)</li>
              </ul>
              <p><em>Note: First row should be headers</em></p>
            </div>
          </div>

          {error && (
            <div className="alert alert-error">
              {error}
            </div>
          )}
        </div>

        <div className="modal-footer">
          <button
            className="btn btn-secondary"
            onClick={handleClose}
            disabled={uploading}
          >
            Cancel
          </button>
          <button
            className="btn btn-primary"
            onClick={handleUpload}
            disabled={!selectedFile || uploading}
          >
            {uploading ? (
              <>
                <span className="loading"></span>
                Uploading...
              </>
            ) : (
              'Upload & Import'
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default FileUploadModal;
