import axios from 'axios';

const api = axios.create({
  // Confirm the backend port with Dev 2 (Spring Boot default is 8080)
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
});

// Add the JWT to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Expired or invalid token: clear the session and go back to the login page
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const onLoginPage = window.location.pathname.startsWith('/login');
    if (error.response?.status === 401 && !onLoginPage) {
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      localStorage.removeItem('patientId');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;