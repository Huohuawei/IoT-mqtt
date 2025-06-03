import axios from 'axios';

const BASE_URL = 'http://localhost:8080';

export interface LoginData {
  username: string;
  password: string;
}

export interface RegisterData extends LoginData {
  email: string;
}

export const userApi = {
  login: async (data: LoginData) => {
    const response = await axios.post(`${BASE_URL}/api/users/login`, data);
    return response.data;
  },

  register: async (data: RegisterData) => {
    const response = await axios.post(`${BASE_URL}/api/users/register`, data);
    return response.data;
  }
}; 