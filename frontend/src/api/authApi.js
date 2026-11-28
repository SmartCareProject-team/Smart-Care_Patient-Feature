import api from './axiosClient';
import { USE_MOCK } from './config';

const wait = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

export async function loginRequest({ email, password }) {
  if (USE_MOCK) {
    await wait(500);
    // Mock: the password "wrong" simulates a failed login
    if (password === 'wrong') {
      throw { response: { status: 401, data: { message: 'Invalid e-mail or password.' } } };
    }
    return { token: 'mock-token', role: 'PATIENT', patientId: 1 };
  }
  const { data } = await api.post('/auth/login', { email, password });
  return data; // { token, role, patientId }
}

export async function registerRequest(payload) {
  if (USE_MOCK) {
    await wait(500);
    return { message: 'Account created.' };
  }
  const { data } = await api.post('/auth/register', payload);
  return data;
}

export function getErrorMessage(error) {
  if (!error.response) return 'Server unreachable. Check your connection.';
  return error.response.data?.message || `Error ${error.response.status}`;
}