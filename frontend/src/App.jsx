import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './auth/ProtectedRoute';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import ProfilePage from './pages/profile/ProfilePage';

// Temporary pages: each one is replaced by the real page on its own branch
function Placeholder({ title }) {
  return <h2>{title}</h2>;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
<Route path="/register" element={<RegisterPage />} />

      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<Placeholder title="Dashboard" />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/appointments" element={<Placeholder title="My appointments" />} />
        <Route path="/appointments/new" element={<Placeholder title="Book an appointment" />} />
        <Route path="/appointments/:id/edit" element={<Placeholder title="Edit appointment" />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}