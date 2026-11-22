import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { loginRequest, getErrorMessage } from '../../api/authApi';
import './auth.css';

export default function LoginPage() {
  const { isAuthenticated, login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (isAuthenticated) return <Navigate to="/" replace />;

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!form.email.trim() || !form.password) {
      setError('Please enter your e-mail and password.');
      return;
    }
    setLoading(true);
    try {
      const data = await loginRequest({ email: form.email.trim(), password: form.password });
      login(data);
      navigate('/', { replace: true });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <h2>Login</h2>
      {location.state?.registered && (
        <p className="auth-success">Account created. You can log in now.</p>
      )}
      <form className="auth-form" onSubmit={handleSubmit}>
        <label>
          E-mail
          <input type="email" name="email" value={form.email} onChange={handleChange} />
        </label>
        <label>
          Password
          <input type="password" name="password" value={form.password} onChange={handleChange} />
        </label>
        {error && <p className="auth-error">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Log in'}
        </button>
      </form>
      <p className="auth-link">
        No account yet? <Link to="/register">Create one</Link>
      </p>
    </div>
  );
}