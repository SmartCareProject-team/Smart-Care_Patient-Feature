import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(() => {
    const token = localStorage.getItem('token');
    return token
      ? {
          token,
          role: localStorage.getItem('role'),
          patientId: localStorage.getItem('patientId'),
        }
      : null;
  });

  // Called with the login response: { token, role, patientId }
  const login = ({ token, role, patientId }) => {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role ?? '');
    localStorage.setItem('patientId', patientId ?? '');
    setAuth({ token, role, patientId });
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('patientId');
    setAuth(null);
  };

  return (
    <AuthContext.Provider value={{ auth, isAuthenticated: !!auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}