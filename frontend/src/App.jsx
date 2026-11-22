import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './auth/ProtectedRoute';

// Temporary pages: each one is replaced by the real page on its own branch
function Placeholder({ title }) {
  return <h2>{title}</h2>;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Placeholder title="Login" />} />
      <Route path="/register" element={<Placeholder title="Register" />} />

      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<Placeholder title="Dashboard" />} />
        <Route path="/profile" element={<Placeholder title="Profile" />} />
        <Route path="/appointments" element={<Placeholder title="My appointments" />} />
        <Route path="/appointments/new" element={<Placeholder title="Book an appointment" />} />
        <Route path="/appointments/:id/edit" element={<Placeholder title="Edit appointment" />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}