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
  CircularProgress,
  TextField,
  IconButton,
  InputAdornment,
  Pagination,
  Stack,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  SelectChangeEvent
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { PautaDTO } from '../../types';
import { PautaService } from '../../services/PautaService';

const PautaList: React.FC = () => {
  const [pautas, setPautas] = useState<PautaDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize, setPageSize] = useState(5);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredPautas, setFilteredPautas] = useState<PautaDTO[]>([]);

  const fetchPautas = async (pageNumber = page, size = pageSize) => {
    try {
      setLoading(true);
      const response = await PautaService.listarPautas(pageNumber, size);
      setPautas(response.content || response);
      setFilteredPautas(response.content || response);
      setTotalPages(response.totalPages || Math.ceil(response.length / size));
      setError(null);
    } catch (error) {
      console.error('Erro ao carregar pautas:', error);
      setError('Não foi possível carregar as pautas. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPautas();
  }, [page, pageSize]);

  useEffect(() => {
    if (searchTerm) {
      const filtered = pautas.filter(pauta => 
        pauta.titulo.toLowerCase().includes(searchTerm.toLowerCase()) ||
        pauta.descricao.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredPautas(filtered);
    } else {
      setFilteredPautas(pautas);
    }
  }, [searchTerm, pautas]);

  const handleChangePage = (event: React.ChangeEvent<unknown>, value: number) => {
    const newPage = value - 1;
    setPage(newPage);
    // Chamamos fetchPautas diretamente para garantir que os dados sejam atualizados imediatamente
    fetchPautas(newPage, pageSize);
  };

  const handleChangePageSize = (event: SelectChangeEvent<number>) => {
    const newSize = event.target.value as number;
    setPageSize(newSize);
    setPage(0);
    // Chamamos fetchPautas diretamente para garantir que os dados sejam atualizados imediatamente
    fetchPautas(0, newSize);
  };

  const handleSearch = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(event.target.value);
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
          onClick={() => fetchPautas()} 
          sx={{ mt: 2 }}
        >
          Tentar novamente
        </Button>
      </Box>
    );
  }

  if (pautas.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="h6" component="div" gutterBottom>
          Nenhuma pauta encontrada
        </Typography>
        <Button 
          component={RouterLink} 
          to="/pautas/nova" 
          variant="contained" 
          color="primary" 
          sx={{ mt: 2 }}
        >
          Criar Nova Pauta
        </Button>
      </Paper>
    );
  }

  return (
    <Box>
      <Box sx={{ mb: 3 }}>
        <TextField
          fullWidth
          variant="outlined"
          placeholder="Buscar pauta por título ou descrição..."
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

      {filteredPautas.length === 0 ? (
        <Paper sx={{ p: 3, textAlign: 'center', mt: 2 }}>
          <Typography variant="body1">
            Nenhuma pauta encontrada com os termos de busca.
          </Typography>
        </Paper>
      ) : (
        <>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Título</TableCell>
                  <TableCell>Data de Criação</TableCell>
                  <TableCell align="right">Ações</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredPautas.map((pauta) => (
                  <TableRow key={pauta.id}>
                    <TableCell>{pauta.id}</TableCell>
                    <TableCell>{pauta.titulo}</TableCell>
                    <TableCell>
                      {format(new Date(pauta.dataCriacao), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
                    </TableCell>
                    <TableCell align="right">
                      <Button 
                        component={RouterLink} 
                        to={`/pautas/${pauta.id}`} 
                        variant="outlined" 
                        size="small" 
                        sx={{ mr: 1 }}
                      >
                        Detalhes
                      </Button>
                      <Button 
                        component={RouterLink} 
                        to={`/pautas/${pauta.id}`} 
                        variant="contained" 
                        size="small" 
                        color="primary"
                      >
                        Abrir Sessão
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

export default PautaList;