import api from './api';
import { AssociadoDTO, AssociadoRequest, PageResponse } from '../types';

export const AssociadoService = {
  cadastrarAssociado: async (associado: AssociadoRequest): Promise<AssociadoDTO> => {
    const response = await api.post('/associados', associado);
    return response.data;
  },

  obterAssociado: async (id: string): Promise<AssociadoDTO> => {
    const response = await api.get(`/associados/${id}`);
    return response.data;
  },
  
  listarAssociados: async (page = 0, size = 10, nome?: string, cpf?: string): Promise<PageResponse<AssociadoDTO>> => {
    const params: Record<string, any> = { page, size };
    
    if (nome) params.nome = nome;
    if (cpf) params.cpf = cpf;
    
    const response = await api.get('/associados', { params });
    return response.data;
  },
  
  buscarPorCpf: async (cpf: string): Promise<AssociadoDTO> => {
    // We have to use the list endpoint with a filter since there's no dedicated CPF endpoint
    // Limiting to one result since we only need the matching member
    const response = await api.get('/associados', { 
      params: { 
        cpf,
        page: 0,
        size: 1 
      } 
    });
    
    // Got a response - let's see if we found anyone
    if (response.data.content && response.data.content.length > 0) {
      return response.data.content[0];
    }
    
    // Couldn't find anyone with this CPF in the database
    throw new Error('Associado não encontrado com o CPF informado.');
  }
};