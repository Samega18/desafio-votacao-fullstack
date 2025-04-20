import api from './api';
import { ResultadoVotacaoDTO, SessaoRequest, SessaoVotacaoDTO, PageResponse } from '../types';

export const SessaoVotacaoService = {
  abrirSessao: async (pautaId: number, sessao: SessaoRequest): Promise<SessaoVotacaoDTO> => {
    const response = await api.post(`/pautas/${pautaId}/sessoes`, sessao);
    return response.data;
  },

  obterSessao: async (id: number): Promise<SessaoVotacaoDTO> => {
    const response = await api.get(`/sessoes/${id}`);
    return response.data;
  },

  listarSessoes: async (page = 0, size = 10): Promise<PageResponse<SessaoVotacaoDTO>> => {
    const response = await api.get('/sessoes', {
      params: { page, size }
    });
    
    // The API sometimes returns an array instead of a paginated object - need to handle both cases
    if (Array.isArray(response.data)) {
      // Transform the array into the PageResponse format our components expect
      return {
        content: response.data,
        totalElements: response.data.length,
        totalPages: 1,
        size: response.data.length,
        number: 0,
        first: true,
        last: true,
        empty: response.data.length === 0
      };
    }
    
    // If we already have a PageResponse object, just pass it through
    return response.data;
  },

  obterResultado: async (sessaoId: number): Promise<ResultadoVotacaoDTO> => {
    const response = await api.get(`/sessoes/${sessaoId}/resultado`);
    return response.data;
  }
};