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
  
  listarAssociados: async (page = 0, size = 10, termo?: string, tipoBusca: 'nome' | 'cpf' = 'nome'): Promise<PageResponse<AssociadoDTO>> => {
    const params: Record<string, any> = { page, size };
    
    if (termo) {
      if (tipoBusca === 'nome') {
        params.nome = termo;
      } else if (tipoBusca === 'cpf') {
        params.cpf = termo;
      }
    }
    
    const response = await api.get('/associados/busca', { params });
    return response.data;
  },
  
  buscarPorCpf: async (cpf: string): Promise<AssociadoDTO> => {
    // Usando o novo endpoint de busca com o filtro por CPF
    const response = await api.get('/associados/busca', { 
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