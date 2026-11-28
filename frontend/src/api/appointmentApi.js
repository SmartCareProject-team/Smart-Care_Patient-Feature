import api from './axiosClient';
import { USE_MOCK } from './config';

const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms));
const MOCK_KEY = 'mockAppointments';

const MOCK_SPECIALITES = [
  { id: 1, nom: 'Cardiology' },
  { id: 2, nom: 'Dermatology' },
  { id: 3, nom: 'General medicine' },
];

const MOCK_MEDECINS = [
  { id: 1, nom: 'Ben Ali', prenom: 'Sami', specialiteId: 1 },
  { id: 2, nom: 'Trabelsi', prenom: 'Leila', specialiteId: 2 },
  { id: 3, nom: 'Gharbi', prenom: 'Karim', specialiteId: 3 },
  { id: 4, nom: 'Mansour', prenom: 'Nadia', specialiteId: 3 },
];

// One appointment per status, like the seed data planned by the team
function seed() {
  return [
    { id: 1, medecinId: 1, specialiteId: 1, dateHeure: '2026-12-01T10:00:00', type: 'EN_LIGNE', motif: 'Follow-up', status: 'EN_ATTENTE' },
    { id: 2, medecinId: 2, specialiteId: 2, dateHeure: '2026-12-03T14:30:00', type: 'PRESENTIEL', motif: 'Skin check', status: 'CONFIRME' },
    { id: 3, medecinId: 3, specialiteId: 3, dateHeure: '2026-11-10T09:00:00', type: 'PRESENTIEL', motif: 'General check-up', status: 'TERMINE' },
    { id: 4, medecinId: 3, specialiteId: 3, dateHeure: '2026-11-15T11:00:00', type: 'EN_LIGNE', motif: 'Headaches', status: 'ANNULE' },
  ];
}

function readMock() {
  const saved = localStorage.getItem(MOCK_KEY);
  if (saved) return JSON.parse(saved);
  const data = seed();
  localStorage.setItem(MOCK_KEY, JSON.stringify(data));
  return data;
}

function writeMock(list) {
  localStorage.setItem(MOCK_KEY, JSON.stringify(list));
}

function withNames(a) {
  const m = MOCK_MEDECINS.find((x) => x.id === a.medecinId);
  const s = MOCK_SPECIALITES.find((x) => x.id === a.specialiteId);
  return {
    ...a,
    medecinNom: m ? `Dr. ${m.prenom} ${m.nom}` : '',
    specialiteNom: s ? s.nom : '',
  };
}

function conflict() {
  return {
    response: {
      status: 409,
      data: { message: 'Only pending appointments can be modified or cancelled.' },
    },
  };
}

// The contract says "status"; accept "statut" too until Dev 2 confirms
function normalize(a) {
  return { ...a, status: a.status ?? a.statut };
}

export async function getSpecialites() {
  if (USE_MOCK) {
    await wait(200);
    return MOCK_SPECIALITES;
  }
  const { data } = await api.get('/specialites');
  return data;
}

export async function getMedecins(specialiteId) {
  if (USE_MOCK) {
    await wait(200);
    return MOCK_MEDECINS.filter((m) => String(m.specialiteId) === String(specialiteId));
  }
  const { data } = await api.get('/medecins', { params: { specialiteId } });
  return data;
}

export async function getAppointments() {
  if (USE_MOCK) {
    await wait(300);
    return readMock()
      .map(withNames)
      .sort((a, b) => b.dateHeure.localeCompare(a.dateHeure));
  }
  const { data } = await api.get('/patients/me/appointments');
  return data.map(normalize);
}

export async function createAppointment(payload) {
  if (USE_MOCK) {
    await wait(400);
    const list = readMock();
    const medecin = MOCK_MEDECINS.find((m) => m.id === payload.medecinId);
    const created = {
      id: Math.max(0, ...list.map((a) => a.id)) + 1,
      medecinId: payload.medecinId,
      specialiteId: medecin ? medecin.specialiteId : null,
      dateHeure: payload.dateHeure,
      type: payload.type,
      motif: payload.motif,
      status: 'EN_ATTENTE',
    };
    writeMock([...list, created]);
    return withNames(created);
  }
  const { data } = await api.post('/patients/me/appointments', payload);
  return data;
}

export async function updateAppointment(id, payload) {
  if (USE_MOCK) {
    await wait(400);
    const list = readMock();
    const index = list.findIndex((a) => String(a.id) === String(id));
    if (index === -1 || list[index].status !== 'EN_ATTENTE') throw conflict();
    const medecin = MOCK_MEDECINS.find((m) => m.id === payload.medecinId);
    list[index] = {
      ...list[index],
      ...payload,
      specialiteId: medecin ? medecin.specialiteId : list[index].specialiteId,
    };
    writeMock(list);
    return withNames(list[index]);
  }
  const { data } = await api.put(`/patients/me/appointments/${id}`, payload);
  return data;
}

export async function cancelAppointment(id) {
  if (USE_MOCK) {
    await wait(300);
    const list = readMock();
    const index = list.findIndex((a) => String(a.id) === String(id));
    if (index === -1 || list[index].status !== 'EN_ATTENTE') throw conflict();
    list[index] = { ...list[index], status: 'ANNULE' };
    writeMock(list);
    return withNames(list[index]);
  }
  const { data } = await api.patch(`/patients/me/appointments/${id}/cancel`);
  return data;
}