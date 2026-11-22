import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerRequest, getErrorMessage } from '../../api/authApi';
import './auth.css';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    nom: '',
    prenom: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!form.nom.trim() || !form.prenom.trim() || !form.email.trim() || !form.password) {
      setError('Please fill in all the fields.');
      return;
    }
    if (form.password.length < 6) {
      setError('The password must have at least 6 characters.');
      return;
    }
    if (form.password !== form.confirmPassword) {
      setError('The passwords do not match.');
      return;
    }

    setLoading(true);
    try {
      await registerRequest({
        nom: form.nom.trim(),
        prenom: form.prenom.trim(),
        email: form.email.trim(),
        password: form.password,
      });
      navigate('/login', { state: { registered: true } });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <h2>Register</h2>
      <form className="auth-form" onSubmit={handleSubmit}>
        <label>
          Last name
          <input name="nom" value={form.nom} onChange={handleChange} />
        </label>
        <label>
          First name
          <input name="prenom" value={form.prenom} onChange={handleChange} />
        </label>
        <label>
          E-mail
          <input type="email" name="email" value={form.email} onChange={handleChange} />
        </label>
        <label>
          Password
          <input type="password" name="password" value={form.password} onChange={handleChange} />
        </label>
        <label>
          Confirm password
          <input
            type="password"
            name="confirmPassword"
            value={form.confirmPassword}
            onChange={handleChange}
          />
        </label>
        {error && <p className="auth-error">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Creating account...' : 'Create account'}
        </button>
      </form>
      <p className="auth-link">
        Already registered? <Link to="/login">Log in</Link>
      </p>
    </div>
  );
}