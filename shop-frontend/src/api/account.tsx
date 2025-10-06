import axios from 'axios';

const API_BASE_URL = import.meta.env.API_BASE_URL as string||'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
});

interface LoginCredentials {
  login: string; // can be either email or username
  password: string;
}

export const account = {
  login: async (login: string, password: string) => {
    const credentials: LoginCredentials = {
      login,
      password
    };
    return api.post('/v1/auth/login', credentials);
  },
   googleLogin: (token: string) =>
    api.post("/v1/auth/google", { token }),
  register: async (userData: {
    email: string;
    username: string;
    password: string;
  }) => {
    return api.post('/v1/auth/register', userData);
  },
  logout: async () => {
    return api.post('/v1/auth/logout');
  }
};
