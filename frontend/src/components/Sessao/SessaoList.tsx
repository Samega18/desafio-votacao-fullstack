import React, { useEffect, useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper, 
  Typography, 
  Button,
  Box,
  Chip,
  CircularProgress,
  TextField,
  InputAdornment,
  Pagination,
  Stack,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  SelectChangeEvent,
  ToggleButtonGroup,
  ToggleButton
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import FilterListIcon from '@mui/icons-material/FilterList';
import { format, isAfter } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { SessaoVotacaoDTO } from '../../types';
import { SessaoVotacaoService } from '../../services/SessaoVotacaoService';

interface SessaoListProps {
  pautaId?: number;
}

const SessaoList: React.FC<SessaoListProps> = ({ pautaId }) => {
  const [sessoes, setSessoes] = useState<SessaoVotacaoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize, setPageSize] = useState(5);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredSessoes, setFilteredSessoes] = useState<SessaoVotacaoDTO[]>([]);
  const [statusFilter, setStatusFilter] = useState<string | null>(null);

  const fetchSessoes = async (pageNumber = page, size = pageSize) => {
    try {
      setLoading(true);
      console.log('Buscando sessões. Page:', pageNumber, 'Size:', size);
      
      const response = await SessaoVotacaoService.listarSessoes(pageNumber, size);
      console.log('Resposta obtida:', response);
      
      // Need to verify the response structure before continuing
      if (!response.content) {
        console.error('Resposta inválida da API - content não encontrado:', response);
        setError('Formato de resposta inválido. Entre em contato com o suporte.');
        setSessoes([]);
        setFilteredSessoes([]);
        return;
      }
      
      // When viewing a specific agenda, filter to show only its sessions
      const data = pautaId 
        ? response.content.filter(sessao => sessao.pautaId === pautaId)
        : response.content;
      
      console.log('Dados processados:', data);
        
      setSessoes(data || []);
      setFilteredSessoes(data || []);
      setTotalPages(response.totalPages || 1);
      setError(null);
    } catch (error) {
      console.error('Erro ao carregar sessões:', error);
      setError('Não foi possível carregar as sessões. Tente novamente.');
      setSessoes([]);
      setFilteredSessoes([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSessoes();
  }, [page, pageSize, pautaId]);

  useEffect(() => {
    filterSessoes();
  }, [searchTerm, statusFilter, sessoes]);

  const filterSessoes = () => {
    let filtered = [...sessoes];
    
    // Apply text search filter based on what the user typed
    if (searchTerm) {
      filtered = filtered.filter(sessao => 
        sessao.tituloPauta.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }
    
    // Filter by session status to help users find what they need
    if (statusFilter) {
      filtered = filtered.filter(sessao => {
        if (statusFilter === 'ativa') {
          return isSessaoAtiva(sessao);
        } else if (statusFilter === 'encerrada') {
          return sessao.encerrada;
        } else if (statusFilter === 'fechada') {
          return !sessao.encerrada && !isSessaoAtiva(sessao);
        }
        return true;
      });
    }
    
    setFilteredSessoes(filtered);
  };

  const handleChangePage = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value - 1);
  };

  const handleChangePageSize = (event: SelectChangeEvent<number>) => {
    setPageSize(event.target.value as number);
    setPage(0);
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
  };

  const handleStatusFilterChange = (
    event: React.MouseEvent<HTMLElement>,
    newStatus: string | null,
  ) => {
    setStatusFilter(newStatus);
  };

  const isSessaoAtiva = (sessao: SessaoVotacaoDTO): boolean => {
    if (sessao.encerrada) return false;
    return !isAfter(new Date(), new Date(sessao.dataFechamento));
  };

  const handleRetry = () => {
    fetchSessoes();
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ my: 2 }}>
        <Typography color="error">{error}</Typography>
        <Button 
          variant="outlined" 
          onClick={handleRetry} 
          sx={{ mt: 2 }}
        >
          Tentar novamente
        </Button>
      </Box>
    );
  }

  if (sessoes.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="body1" gutterBottom>
          Nenhuma sessão de votação encontrada
        </Typography>
      </Paper>
    );
  }

  return (
    <Box>
      <Box sx={{ mb: 3 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Buscar sessão por título da pauta..."
          value={searchTerm}
          onChange={handleSearch}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
        />
      </Box>

      <Box sx={{ mb: 3, display: 'flex', alignItems: 'center' }}>
        <FilterListIcon sx={{ mr: 1 }} />
        <Typography variant="body2" sx={{ mr: 2 }}>
          Filtrar por status:
        </Typography>
        <ToggleButtonGroup
          value={statusFilter}
          exclusive
          onChange={handleStatusFilterChange}
          aria-label="filtro de status"
          size="small"
        >
          <ToggleButton value="ativa" aria-label="ativa">
            Ativas
          </ToggleButton>
          <ToggleButton value="encerrada" aria-label="encerrada">
            Encerradas
          </ToggleButton>
          <ToggleButton value="fechada" aria-label="fechada">
            Fechadas
          </ToggleButton>
        </ToggleButtonGroup>
      </Box>

      {filteredSessoes.length === 0 ? (
        <Paper sx={{ p: 3, textAlign: 'center', mt: 2 }}>
          <Typography variant="body1">
            Nenhuma sessão encontrada com os critérios de busca.
          </Typography>
        </Paper>
      ) : (
        <>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Pauta</TableCell>
                  <TableCell>Abertura</TableCell>
                  <TableCell>Fechamento</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell align="right">Ações</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredSessoes.map((sessao) => (
                  <TableRow key={sessao.id}>
                    <TableCell>{sessao.id}</TableCell>
                    <TableCell>{sessao.tituloPauta}</TableCell>
                    <TableCell>
                      {format(new Date(sessao.dataAbertura), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                    </TableCell>
                    <TableCell>
                      {format(new Date(sessao.dataFechamento), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                    </TableCell>
                    <TableCell>
                      {sessao.encerrada ? (
                        <Chip label="Encerrada" color="error" size="small" />
                      ) : isSessaoAtiva(sessao) ? (
                        <Chip label="Em andamento" color="success" size="small" />
                      ) : (
                        <Chip label="Fechada" color="warning" size="small" />
                      )}
                    </TableCell>
                    <TableCell align="right">
                      {isSessaoAtiva(sessao) && (
                        <Button 
                          component={RouterLink} 
                          to={`/sessoes/${sessao.id}/votar`} 
                          variant="contained" 
                          size="small" 
                          color="primary"
                          sx={{ mr: 1 }}
                        >
                          Votar
                        </Button>
                      )}
                      <Button 
                        component={RouterLink} 
                        to={`/sessoes/${sessao.id}/resultado`} 
                        variant="outlined" 
                        size="small"
                      >
                        Resultado
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Stack 
            direction={{ xs: 'column', sm: 'row' }} 
            spacing={2}
            alignItems="center"
            justifyContent="space-between"
            sx={{ mt: 3 }}
          >
            <FormControl variant="outlined" sx={{ minWidth: 120 }}>
              <InputLabel id="items-per-page-label">Itens por página</InputLabel>
              <Select
                labelId="items-per-page-label"
                value={pageSize}
                onChange={handleChangePageSize}
                label="Itens por página"
              >
                <MenuItem value={5}>5</MenuItem>
                <MenuItem value={10}>10</MenuItem>
                <MenuItem value={25}>25</MenuItem>
              </Select>
            </FormControl>

            <Pagination 
              count={totalPages} 
              page={page + 1} 
              onChange={handleChangePage} 
              variant="outlined" 
              shape="rounded" 
              color="primary"
            />
          </Stack>
        </>
      )}
    </Box>
  );
};

export default SessaoList;