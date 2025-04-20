export enum OpcaoVoto {
  SIM = 'SIM',
  NAO = 'NAO'
}

export interface Associado {
  id: string;
  cpf: string;
  nome: string;
  dataCadastro: string;
  ativo: boolean;
}

export interface AssociadoDTO {
  id: string;
  nome: string;
  cpf: string;
  ativo: boolean;
}

export interface AssociadoRequest {
  cpf: string;
  nome: string;
}

export interface Pauta {
  id: number;
  titulo: string;
  descricao: string;
  dataCriacao: string;
  sessoes?: SessaoVotacao[];
}

export interface PautaDTO {
  id: number;
  titulo: string;
  descricao: string;
  dataCriacao: string;
}

export interface PautaRequest {
  titulo: string;
  descricao: string;
}

export interface SessaoVotacao {
  id: number;
  pauta: Pauta;
  dataAbertura: string;
  dataFechamento: string;
  encerrada: boolean;
  votos?: Voto[];
}

export interface SessaoVotacaoDTO {
  id: number;
  pautaId: number;
  tituloPauta: string;
  dataAbertura: string;
  dataFechamento: string;
  encerrada: boolean;
}

export interface SessaoRequest {
  duracaoMinutos: number;
}

export interface Voto {
  id: number;
  idAssociado: string;
  cpfAssociado: string;
  opcao: OpcaoVoto;
  dataHoraVoto: string;
  sessaoVotacao: SessaoVotacao;
}

export interface VotoDTO {
  id: number;
  idAssociado: string;
  opcao: OpcaoVoto;
  dataHoraVoto: string;
}

export interface VotoRequest {
  idAssociado: string;
  cpf: string;
  opcao: OpcaoVoto;
}

export interface ResultadoVotacao {
  id: number;
  sessaoVotacao: SessaoVotacao;
  totalVotos: number;
  votosSim: number;
  votosNao: number;
  dataApuracao: string;
}

export interface ResultadoVotacaoDTO {
  id: number;
  sessaoId: number;
  pautaId: number;
  tituloPauta: string;
  totalVotos: number;
  votosSim: number;
  votosNao: number;
  percentualSim: number;
  percentualNao: number;
  aprovado: boolean;
}

export interface StatusVotacao {
  status: 'ABLE_TO_VOTE' | 'UNABLE_TO_VOTE';
}

// These interfaces help us work with paginated data from the backend
export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
}