import { Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './auth/ProtectedRoute';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import ProfilePage from './pages/profile/ProfilePage';
import AppointmentsPage from './pages/appointments/AppointmentsPage';
import BookAppointmentPage from './pages/appointments/BookAppointmentPage';
import EditAppointmentPage from './pages/appointments/EditAppointmentPage';
import DashboardPage from './pages/dashboard/DashboardPage.jsx';


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
       <Route path="/" element={<DashboardPage />} />
<Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/appointments" element={<AppointmentsPage />} />
        <Route path="/appointments/new" element={<BookAppointmentPage />} />
        <Route path="/appointments/:id/edit" element={<EditAppointmentPage />} />
      </Route>
      <Route path="/profile" element={<ProfilePage />} />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}