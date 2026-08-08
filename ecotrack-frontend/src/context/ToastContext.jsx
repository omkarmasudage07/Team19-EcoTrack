import React, { createContext, useCallback, useContext, useState } from 'react';
import { FiCheckCircle, FiXCircle, FiInfo } from 'react-icons/fi';

const ToastContext = createContext(null);

const ICONS = {
  success: <FiCheckCircle className="text-success" />,
  error: <FiXCircle className="text-danger" />,
  info: <FiInfo className="text-primary" />,
};

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const showToast = useCallback((message, type = 'success') => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4000);
  }, []);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div
        className="position-fixed bottom-0 end-0 p-3"
        style={{ zIndex: 2000, maxWidth: 360 }}
      >
        {toasts.map((t) => (
          <div
            key={t.id}
            className="d-flex align-items-start gap-2 bg-white border rounded-3 shadow-sm p-3 mb-2"
          >
            <div className="mt-1">{ICONS[t.type]}</div>
            <div className="small flex-fill">{t.message}</div>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};
