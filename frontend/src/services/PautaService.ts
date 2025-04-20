import api from './api';
import { PautaDTO, PautaRequest, PageResponse } from '../types';

export const PautaService = {
  listarPautas: async (page = 0, size = 10): Promise<PageResponse<PautaDTO>> => {
    const response = await api.get('/pautas', {
      params: { page, size }
    });
    return response.data;
  },

  obterPauta: async (id: number): Promise<PautaDTO> => {
    const response = await api.get(`/pautas/${id}`);
    return response.data;
  },

  criarPauta: async (pauta: PautaRequest): Promise<PautaDTO> => {
    const response = await api.post('/pautas', pauta);
    return response.data;
  }
}; 