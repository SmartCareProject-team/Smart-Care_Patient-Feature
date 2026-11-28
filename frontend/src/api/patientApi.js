import api from './axiosClient';
import { USE_MOCK } from './config';

const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
const MOCK_KEY = 'mockProfile';

const emptyProfile = {
  nom: '',
  prenom: '',
  cin: '',
  dateNaissance: '',
  telephone: '',
  adresse: '',
  groupeSanguin: '',
  antecedents: '',
  allergies: '',
};

// Replace null/missing fields with '' so the form inputs stay controlled
function normalize(data) {
  const result = {};
  Object.keys(emptyProfile).forEach((key) => {
    result[key] = data?.[key] ?? '';
  });
  return result;
}

export async function getProfile() {
  if (USE_MOCK) {
    await wait(300);
    const saved = localStorage.getItem(MOCK_KEY);
    return normalize(saved ? JSON.parse(saved) : {});
  }
  const { data } = await api.get('/patients/me');
  return normalize(data);
}

export async function updateProfile(profile) {
  if (USE_MOCK) {
    await wait(400);
    localStorage.setItem(MOCK_KEY, JSON.stringify(profile));
    return normalize(profile);
  }
  const { data } = await api.put('/patients/me', profile);
  return normalize(data);
}