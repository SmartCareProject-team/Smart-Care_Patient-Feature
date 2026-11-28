import { useEffect, useState } from 'react';
import { getProfile, updateProfile } from '../../api/patientApi';
import { getErrorMessage } from '../../api/authApi';
import './profile.css';

const BLOOD_GROUPS = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

export default function ProfilePage() {
  const [form, setForm] = useState(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    let cancelled = false;
    getProfile()
      .then((data) => {
        if (!cancelled) setForm(data);
      })
      .catch((err) => {
        if (!cancelled) setError(getErrorMessage(err));
      });
    return () => {
      cancelled = true;
    };
  }, []);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setMessage('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setMessage('');

    if (!form.nom.trim() || !form.prenom.trim()) {
      setError('Last name and first name are required.');
      return;
    }
    if (form.dateNaissance && new Date(form.dateNaissance) > new Date()) {
      setError('The date of birth cannot be in the future.');
      return;
    }

    setSaving(true);
    try {
      const saved = await updateProfile(form);
      setForm(saved);
      setMessage('Profile saved.');
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  if (!form) {
    return (
      <div className="profile-page">
        {error ? <p className="profile-error">{error}</p> : <p>Loading...</p>}
      </div>
    );
  }

  const today = new Date().toISOString().split('T')[0];

  return (
    <div className="profile-page">
      <h2>My profile</h2>
      <form className="profile-form" onSubmit={handleSubmit}>
        <label>
          Last name
          <input name="nom" value={form.nom} onChange={handleChange} />
        </label>
        <label>
          First name
          <input name="prenom" value={form.prenom} onChange={handleChange} />
        </label>
        <label>
          National ID (CIN)
          <input name="cin" value={form.cin} onChange={handleChange} />
        </label>
        <label>
          Date of birth
          <input
            type="date"
            name="dateNaissance"
            value={form.dateNaissance}
            max={today}
            onChange={handleChange}
          />
        </label>
        <label>
          Phone
          <input name="telephone" value={form.telephone} onChange={handleChange} />
        </label>
        <label>
          Address
          <input name="adresse" value={form.adresse} onChange={handleChange} />
        </label>
        <label>
          Blood group
          <select name="groupeSanguin" value={form.groupeSanguin} onChange={handleChange}>
            <option value="">-- Select --</option>
            {BLOOD_GROUPS.map((g) => (
              <option key={g} value={g}>
                {g}
              </option>
            ))}
          </select>
        </label>
        <label>
          Medical history
          <textarea name="antecedents" value={form.antecedents} onChange={handleChange} />
        </label>
        <label>
          Allergies
          <textarea name="allergies" value={form.allergies} onChange={handleChange} />
        </label>

        {error && <p className="profile-error">{error}</p>}
        {message && <p className="profile-success">{message}</p>}

        <button type="submit" disabled={saving}>
          {saving ? 'Saving...' : 'Save'}
        </button>
      </form>
    </div>
  );
}