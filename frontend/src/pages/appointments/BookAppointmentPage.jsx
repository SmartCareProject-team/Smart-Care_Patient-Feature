import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { createAppointment } from '../../api/appointmentApi';
import { getErrorMessage } from '../../api/authApi';
import AppointmentForm from './AppointmentForm';
import './appointments.css';

export default function BookAppointmentPage() {
  const navigate = useNavigate();
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (payload) => {
    setSaving(true);
    setError('');
    try {
      await createAppointment(payload);
      navigate('/appointments', {
        state: { message: 'Appointment booked. Its status is pending.' },
      });
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="appointments-page">
      <h2>Book an appointment</h2>
      <AppointmentForm
        submitLabel="Book"
        onSubmit={handleSubmit}
        saving={saving}
        error={error}
      />
      <p style={{ textAlign: 'center' }}>
        <Link to="/appointments">Back to my appointments</Link>
      </p>
    </div>
  );
}