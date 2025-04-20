import api from './api';
import { VotoDTO, VotoRequest, PageResponse } from '../types';

export const VotoService = {
  registrarVoto: async (sessaoId: number, voto: VotoRequest): Promise<VotoDTO> => {
    const response = await api.post(`/sessoes/${sessaoId}/votos`, voto);
    return response.data;
  },
  
  listarVotosPorSessao: async (sessaoId: number, page = 0, size = 10): Promise<PageResponse<VotoDTO>> => {
    const response = await api.get(`/sessoes/${sessaoId}/votos`, {
      params: { page, size }
    });
    return response.data;
  },
  
  listarVotosPorAssociado: async (associadoId: string, page = 0, size = 10): Promise<PageResponse<VotoDTO>> => {
    const response = await api.get(`/associados/${associadoId}/votos`, {
      params: { page, size }
    });
    return response.data;
  }
}; 