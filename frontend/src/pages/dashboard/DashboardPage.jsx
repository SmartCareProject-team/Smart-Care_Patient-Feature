import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { getAppointments } from '../../api/appointmentApi';
import './dashboard.css';

const STATUS = {
  EN_ATTENTE: { label: 'Pending', cls: 'pending' },
  CONFIRME: { label: 'Confirmed', cls: 'confirmed' },
  TERMINE: { label: 'Completed', cls: 'completed' },
  ANNULE: { label: 'Cancelled', cls: 'cancelled' },
};

const dateOf = (a) => new Date(a.dateHeure);
const formatDate = (d) =>
  `${d.toLocaleDateString('en-GB')} ${d.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })}`;
const doctorOf = (a) => a.medecinNom || a.medecin?.nom || `Doctor #${a.medecinId}`;

export default function DashboardPage() {
  const { user } = useAuth();
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    getAppointments()
      .then((list) => setAppointments(list))
      .catch(() => setError('Could not load your appointments.'))
      .finally(() => setLoading(false));
  }, []);

  const counts = useMemo(() => {
    const c = { EN_ATTENTE: 0, CONFIRME: 0, TERMINE: 0, ANNULE: 0 };
    appointments.forEach((a) => {
      if (c[a.status] !== undefined) c[a.status] += 1;
    });
    return c;
  }, [appointments]);

  const next = useMemo(() => {
    const now = new Date();
    return appointments
      .filter((a) => (a.status === 'EN_ATTENTE' || a.status === 'CONFIRME') && dateOf(a) >= now)
      .sort((a, b) => dateOf(a) - dateOf(b))[0];
  }, [appointments]);

  const name = user?.prenom || user?.firstName || user?.name || '';

  return (
    <div className="dashboard">
      <h1>Welcome{name ? `, ${name}` : ''}</h1>

      {loading && <p>Loading...</p>}
      {error && <p className="dashboard-error">{error}</p>}

      {!loading && !error && (
        <>
          <section className="dashboard-card">
            <h2>Next appointment</h2>
            {next ? (
              <>
                <p className="dashboard-next-date">{formatDate(dateOf(next))}</p>
                <p>{doctorOf(next)}</p>
                <p>{next.motif}</p>
                <span className={`badge ${STATUS[next.status].cls}`}>{STATUS[next.status].label}</span>
              </>
            ) : (
              <p>No upcoming appointment.</p>
            )}
          </section>

          <section className="dashboard-stats">
            {Object.entries(STATUS).map(([key, s]) => (
              <div key={key} className="dashboard-card stat">
                <span className="stat-number">{counts[key]}</span>
                <span className={`badge ${s.cls}`}>{s.label}</span>
              </div>
            ))}
          </section>

          <section className="dashboard-links">
            <Link to="/appointments/new">Book an appointment</Link>
            <Link to="/appointments">My appointments</Link>
            <Link to="/profile">Profile</Link>
          </section>
        </>
      )}
    </div>
  );
}