import { useEffect, useState } from 'react';
import { getSpecialites, getMedecins } from '../../api/appointmentApi';
import { getErrorMessage } from '../../api/authApi';
import './appointments.css';

export default function AppointmentForm({ initial, submitLabel, onSubmit, saving, error }) {
  const [specialites, setSpecialites] = useState([]);
  const [medecins, setMedecins] = useState([]);
  const [localError, setLocalError] = useState('');
  const [form, setForm] = useState({
    specialiteId: initial?.specialiteId ?? '',
    medecinId: initial?.medecinId ?? '',
    dateHeure: initial?.dateHeure ? initial.dateHeure.slice(0, 16) : '',
    type: initial?.type ?? 'PRESENTIEL',
    motif: initial?.motif ?? '',
  });

  useEffect(() => {
    getSpecialites()
      .then(setSpecialites)
      .catch((err) => setLocalError(getErrorMessage(err)));
  }, []);

  useEffect(() => {
    if (!form.specialiteId) return;
    getMedecins(form.specialiteId)
      .then(setMedecins)
      .catch((err) => setLocalError(getErrorMessage(err)));
  }, [form.specialiteId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'specialiteId') {
      setMedecins([]);
      setForm({ ...form, specialiteId: value, medecinId: '' });
    } else {
      setForm({ ...form, [name]: value });
    }
  };

  // Local date-time in the format of the datetime-local input
  const minDate = new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
    .toISOString()
    .slice(0, 16);

  const handleSubmit = (e) => {
    e.preventDefault();
    setLocalError('');

    if (!form.specialiteId || !form.medecinId || !form.dateHeure || !form.motif.trim()) {
      setLocalError('Please fill in all the fields.');
      return;
    }
    if (new Date(form.dateHeure) <= new Date()) {
      setLocalError('Please choose a date and time in the future.');
      return;
    }

    onSubmit({
      medecinId: Number(form.medecinId),
      dateHeure: form.dateHeure.length === 16 ? `${form.dateHeure}:00` : form.dateHeure,
      type: form.type,
      motif: form.motif.trim(),
    });
  };

  const shownError = localError || error;

  return (
    <form className="appointment-form" onSubmit={handleSubmit}>
      <label>
        Specialty
        <select name="specialiteId" value={form.specialiteId} onChange={handleChange}>
          <option value="">-- Select --</option>
          {specialites.map((s) => (
            <option key={s.id} value={s.id}>
              {s.nom}
            </option>
          ))}
        </select>
      </label>

      <label>
        Doctor
        <select
          name="medecinId"
          value={form.medecinId}
          onChange={handleChange}
          disabled={!form.specialiteId}
        >
          <option value="">-- Select --</option>
          {medecins.map((m) => (
            <option key={m.id} value={m.id}>
              Dr. {m.prenom} {m.nom}
            </option>
          ))}
        </select>
      </label>

      <label>
        Date and time
        <input
          type="datetime-local"
          name="dateHeure"
          value={form.dateHeure}
          min={minDate}
          onChange={handleChange}
        />
      </label>

      <label>
        Type
        <select name="type" value={form.type} onChange={handleChange}>
          <option value="PRESENTIEL">In person</option>
          <option value="EN_LIGNE">Online</option>
        </select>
      </label>

      <label>
        Reason
        <textarea name="motif" value={form.motif} onChange={handleChange} />
      </label>

      {shownError && <p className="appointments-error">{shownError}</p>}

      <button type="submit" disabled={saving}>
        {saving ? 'Saving...' : submitLabel}
      </button>
    </form>
  );
}