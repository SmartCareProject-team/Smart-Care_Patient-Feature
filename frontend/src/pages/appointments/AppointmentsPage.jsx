import { useCallback, useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { getAppointments, cancelAppointment } from '../../api/appointmentApi';
import { getErrorMessage } from '../../api/authApi';
import StatusBadge from '../../components/StatusBadge';
import './appointments.css';

export default function AppointmentsPage() {
  const location = useLocation();
  const [appointments, setAppointments] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState(location.state?.message || '');

  const load = useCallback(async () => {
    try {
      setAppointments(await getAppointments());
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleCancel = async (id) => {
    if (!window.confirm('Cancel this appointment?')) return;
    setError('');
    setMessage('');
    try {
      await cancelAppointment(id);
      setMessage('Appointment cancelled.');
    } catch (err) {
      setError(
        err.response?.status === 409
          ? 'This appointment can no longer be cancelled: its status is no longer pending.'
          : getErrorMessage(err)
      );
    }
    load();
  };

  return (
    <div className="appointments-page">
      <h2>My appointments</h2>
      <div className="appointments-top">
        <Link to="/appointments/new">Book an appointment</Link>
      </div>

      {error && <p className="appointments-error">{error}</p>}
      {message && <p className="appointments-success">{message}</p>}

      {!appointments ? (
        !error && <p>Loading...</p>
      ) : appointments.length === 0 ? (
        <p>No appointments yet.</p>
      ) : (
        <table className="appointments-table">
          <thead>
            <tr>
              <th>Date</th>
              <th>Doctor</th>
              <th>Type</th>
              <th>Reason</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {appointments.map((a) => (
              <tr key={a.id}>
                <td>{new Date(a.dateHeure).toLocaleString()}</td>
                <td>
                  {a.medecinNom || `Doctor #${a.medecinId}`}
                  {a.specialiteNom ? ` (${a.specialiteNom})` : ''}
                </td>
                <td>{a.type === 'EN_LIGNE' ? 'Online' : 'In person'}</td>
                <td>{a.motif}</td>
                <td>
                  <StatusBadge status={a.status} />
                </td>
                <td>
                  {/* Edit and Cancel exist only while the status is EN_ATTENTE */}
                  {a.status === 'EN_ATTENTE' && (
                    <div className="appointments-actions">
                      <Link to={`/appointments/${a.id}/edit`}>Edit</Link>
                      <button type="button" onClick={() => handleCancel(a.id)}>
                        Cancel
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}