import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL as string || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

export interface UserDTO {
  username: string;
  email: string;
  role: string[];
  number: string;
  address: string;
  city: string;
  country: string;
  postalCode: string;
  profilePicture: string;
  firstName: string;
  lastName: string;
}

interface LoginCredentials {
  login: string;
  password: string;
}

export const account = {
  login: async (login: string, password: string) => {
    const credentials: LoginCredentials = { login, password };
    return api.post('/v1/auth/login', credentials);
  },
  googleLogin: (token: string) =>
    api.post('/v1/auth/google', { token }),
  register: async (userData: {
    email: string;
    username: string;
    password: string;
  }) => {
    return api.post('/v1/auth/register', userData);
  },
  logout: async () => {
    return api.post('/v1/auth/logout');
  },
  getMe: async () =>
    api.get<UserDTO>('/v1/users/me'),
  linkGoogle: (token: string) =>
  api.post<UserDTO>('/v1/users/me/link-google', { token }),
};