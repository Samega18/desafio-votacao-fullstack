import axios from 'axios';

// Utiliza a variável de ambiente VITE_API_URL se disponível, caso contrário usa o valor padrão
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  baseURL: apiUrl,
  headers: {
    'Content-Type': 'application/json',
  }
});

export default api;