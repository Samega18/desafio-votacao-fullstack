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
    // Como não há um endpoint específico para busca por CPF, vamos listar todos os associados
    // com o CPF como filtro, o que deve retornar no máximo 1 registro
    const response = await api.get('/associados', { 
      params: { 
        cpf,
        page: 0,
        size: 1 
      } 
    });
    
    // Verificar se foi encontrado algum associado com o CPF fornecido
    if (response.data.content && response.data.content.length > 0) {
      return response.data.content[0];
    }
    
    // Se não encontrar, lança um erro
    throw new Error('Associado não encontrado com o CPF informado.');
  }
}; 