import React, { useEffect, useState } from 'react';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper, 
  Typography, 
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
  Button,
  IconButton,
  Tooltip,
  Snackbar,
  Alert
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import ContentCopyIcon from '@mui/icons-material/ContentCopy';
import { AssociadoDTO } from '../../types';
import { AssociadoService } from '../../services/AssociadoService';

const AssociadoList: React.FC = () => {
  const [associados, setAssociados] = useState<AssociadoDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [pageSize, setPageSize] = useState(5);
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredAssociados, setFilteredAssociados] = useState<AssociadoDTO[]>([]);
  const [statusFilter, setStatusFilter] = useState<boolean | null>(null);
  const [copySuccess, setCopySuccess] = useState(false);

  const fetchAssociados = async (pageNumber = page, size = pageSize) => {
    try {
      setLoading(true);
      
      // First try to see if they entered a member ID directly - fastest way to find someone
      if (searchTerm && searchTerm.match(/^[0-9a-fA-F-]{36}$/)) {
        // This looks like a UUID, so let's try a direct lookup
        try {
          const associado = await AssociadoService.obterAssociado(searchTerm);
          if (associado) {
            setAssociados([associado]);
            setFilteredAssociados([associado]);
            setTotalPages(1);
            setError(null);
            setLoading(false);
            return;
          }
        } catch (err) {
          console.log('ID não encontrado, tentando buscar como nome ou CPF');
        }
      }
      
      // If it's not an ID or we couldn't find it, search by name or CPF instead
      const response = await AssociadoService.listarAssociados(pageNumber, size, searchTerm);
      
      // The API can return data in different formats, so we need to handle both cases
      const content = Array.isArray(response) ? response : (response.content || []);
      const total = Array.isArray(response) 
        ? Math.ceil(response.length / size) 
        : (response.totalPages || Math.max(1, Math.ceil(content.length / size)));
        
      setAssociados(content);
      setFilteredAssociados(content);
      setTotalPages(total);
      setError(null);
    } catch (error) {
      console.error('Erro ao carregar associados:', error);
      setError('Não foi possível carregar os associados. Tente novamente.');
      setAssociados([]);
      setFilteredAssociados([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAssociados();
  }, [page, pageSize]);

  useEffect(() => {
    if (statusFilter !== null) {
      const filtered = associados.filter(associado => associado.ativo === statusFilter);
      setFilteredAssociados(filtered);
    } else {
      setFilteredAssociados(associados);
    }
  }, [statusFilter, associados]);

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

  const handleSearchSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    fetchAssociados(0, pageSize);
  };

  const handleStatusFilter = (status: boolean | null) => {
    setStatusFilter(status);
  };

  const handleCopyId = (id: string) => {
    navigator.clipboard.writeText(id)
      .then(() => {
        setCopySuccess(true);
        setTimeout(() => setCopySuccess(false), 3000);
      })
      .catch(err => {
        console.error('Erro ao copiar para a área de transferência:', err);
      });
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
          onClick={() => fetchAssociados()} 
          sx={{ mt: 2 }}
        >
          Tentar novamente
        </Button>
      </Box>
    );
  }

  if (associados.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="h6" component="div" gutterBottom>
          Nenhum associado encontrado
        </Typography>
      </Paper>
    );
  }

  return (
    <Box>
      <Box sx={{ mb: 3 }}>
        <form onSubmit={handleSearchSubmit}>
          <TextField
            fullWidth
            variant="outlined"
            placeholder="Buscar associado por nome ou CPF..."
            value={searchTerm}
            onChange={handleSearch}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              ),
              endAdornment: (
                <InputAdornment position="end">
                  <Button 
                    type="submit" 
                    variant="contained" 
                    size="small"
                  >
                    Buscar
                  </Button>
                </InputAdornment>
              )
            }}
          />
        </form>
      </Box>

      <Box sx={{ mb: 3, display: 'flex', gap: 1 }}>
        <Button 
          variant={statusFilter === null ? "contained" : "outlined"} 
          onClick={() => handleStatusFilter(null)}
          size="small"
        >
          Todos
        </Button>
        <Button 
          variant={statusFilter === true ? "contained" : "outlined"}
          color="success"
          onClick={() => handleStatusFilter(true)}
          size="small"
        >
          Ativos
        </Button>
        <Button 
          variant={statusFilter === false ? "contained" : "outlined"}
          color="error"
          onClick={() => handleStatusFilter(false)}
          size="small"
        >
          Inativos
        </Button>
      </Box>

      {filteredAssociados.length === 0 ? (
        <Paper sx={{ p: 3, textAlign: 'center', mt: 2 }}>
          <Typography variant="body1">
            Nenhum associado encontrado com os critérios de busca.
          </Typography>
        </Paper>
      ) : (
        <>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell width="40%">ID</TableCell>
                  <TableCell width="25%">Nome</TableCell>
                  <TableCell width="20%">CPF</TableCell>
                  <TableCell align="center" width="15%">Status</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredAssociados.map((associado) => (
                  <TableRow key={associado.id}>
                    <TableCell width="40%">
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Paper 
                          variant="outlined" 
                          sx={{ 
                            p: 0.8, 
                            flex: 1, 
                            backgroundColor: '#f8f9fa',
                            borderColor: '#e0e0e0',
                            maxWidth: 'calc(100% - 45px)'
                          }}
                        >
                          <Typography 
                            variant="body2" 
                            sx={{ 
                              fontSize: '0.9rem', 
                              fontFamily: 'monospace',
                              color: '#555',
                              overflow: 'hidden',
                              textOverflow: 'ellipsis',
                              whiteSpace: 'nowrap'
                            }}
                          >
                            {associado.id}
                          </Typography>
                        </Paper>
                        <Tooltip title="Copiar ID">
                          <IconButton 
                            size="small" 
                            onClick={() => handleCopyId(associado.id)}
                            color="primary"
                            sx={{ 
                              backgroundColor: '#f1f3f9',
                              '&:hover': {
                                backgroundColor: '#e3e7f7'
                              }
                            }}
                          >
                            <ContentCopyIcon fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </Box>
                    </TableCell>
                    <TableCell width="25%">{associado.nome}</TableCell>
                    <TableCell width="20%">{associado.cpf}</TableCell>
                    <TableCell align="center" width="15%">
                      {associado.ativo ? (
                        <Chip label="Ativo" color="success" size="small" />
                      ) : (
                        <Chip label="Inativo" color="error" size="small" />
                      )}
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

      <Snackbar 
        open={copySuccess} 
        autoHideDuration={3000} 
        onClose={() => setCopySuccess(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity="success" sx={{ width: '100%' }}>
          ID copiado para a área de transferência!
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default AssociadoList;