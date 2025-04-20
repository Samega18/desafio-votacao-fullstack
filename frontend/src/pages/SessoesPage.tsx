import React, { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  Button, 
  Paper, 
  Card, 
  CardContent, 
  Divider,
  CircularProgress,
  IconButton,
  Tooltip
} from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import SessaoList from '../components/Sessao/SessaoList';

const SessoesPage: React.FC = () => {
  const [refreshKey, setRefreshKey] = useState(0);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const handleRefresh = () => {
    setIsRefreshing(true);
    setRefreshKey(prevKey => prevKey + 1);
    
    // Simular tempo de carregamento para feedback visual
    setTimeout(() => {
      setIsRefreshing(false);
    }, 1000);
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Sessões de Votação
        </Typography>
        <Box>
          <Button
            component={RouterLink}
            to="/pautas"
            variant="outlined"
            sx={{ mr: 2 }}
          >
            Ir para Pautas
          </Button>
          <Button
            component={RouterLink}
            to="/"
            variant="outlined"
          >
            Voltar para Início
          </Button>
        </Box>
      </Box>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h6" component="h2">
              Gerenciamento de Sessões
            </Typography>
            <Tooltip title="Atualizar lista de sessões">
              <IconButton onClick={handleRefresh} disabled={isRefreshing}>
                {isRefreshing ? <CircularProgress size={24} /> : <RefreshIcon />}
              </IconButton>
            </Tooltip>
          </Box>
          <Divider sx={{ my: 2 }} />
          <Typography variant="body1" paragraph>
            Aqui você pode visualizar todas as sessões de votação cadastradas no sistema. 
            As sessões permitem que os associados votem nas pautas durante um período determinado.
          </Typography>
          {/* <Typography variant="body2" color="text.secondary" paragraph>
            • Sessões <strong>Em andamento</strong> estão abertas para votação.<br />
            • Sessões <strong>Encerradas</strong> já tiveram seus votos computados e o resultado pode ser consultado.<br />
            • Sessões <strong>Fechadas</strong> tiveram seu prazo expirado e não aceitam mais votos.
          </Typography> */}
          <Box sx={{ mt: 2 }}>
            <Button
              component={RouterLink}
              to="/pautas"
              variant="contained"
              color="primary"
            >
              Criar Nova Sessão de Votação
            </Button>
          </Box>
        </CardContent>
      </Card>

      <Box sx={{ mt: 3 }}>
        <SessaoList key={refreshKey} />
      </Box>
    </Box>
  );
};

export default SessoesPage; 