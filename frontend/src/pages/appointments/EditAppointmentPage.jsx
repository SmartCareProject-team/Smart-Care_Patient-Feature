import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { getAppointments, updateAppointment } from '../../api/appointmentApi';
import { getErrorMessage } from '../../api/authApi';
import AppointmentForm from './AppointmentForm';
import './appointments.css';

export default function EditAppointmentPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [appointment, setAppointment] = useState(null);
  const [loadError, setLoadError] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getAppointments()
      .then((list) => {
        const found = list.find((a) => String(a.id) === id);
        if (found) setAppointment(found);
        else setLoadError('Appointment not found.');
      })
      .catch((err) => setLoadError(getErrorMessage(err)));
  }, [id]);

  const handleSubmit = async (payload) => {
    setSaving(true);
    setError('');
    try {
      await updateAppointment(id, payload);
      navigate('/appointments', { state: { message: 'Appointment updated.' } });
    } catch (err) {
      setError(
        err.response?.status === 409
          ? 'This appointment can no longer be modified: its status is no longer pending.'
          : getErrorMessage(err)
      );
    } finally {
      setSaving(false);
    }
  };

  const back = (
    <p style={{ textAlign: 'center' }}>
      <Link to="/appointments">Back to my appointments</Link>
    </p>
  );

  if (loadError) {
    return (
      <div className="appointments-page">
        <p className="appointments-error">{loadError}</p>
        {back}
      </div>
    );
  }
  if (!appointment) {
    return <div className="appointments-page"><p>Loading...</p></div>;
  }
  if (appointment.status !== 'EN_ATTENTE') {
    return (
      <div className="appointments-page">
        <p className="appointments-error">Only pending appointments can be edited.</p>
        {back}
      </div>
    );
  }

  return (
    <div className="appointments-page">
      <h2>Edit appointment</h2>
      <AppointmentForm
        initial={appointment}
        submitLabel="Save changes"
        onSubmit={handleSubmit}
        saving={saving}
        error={error}
      />
      {back}
    </div>
  );
}