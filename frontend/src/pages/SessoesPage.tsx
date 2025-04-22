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
    
    // Give users a moment to see the loading state for better UX
    setTimeout(() => {
      setIsRefreshing(false);
    }, 1000); // Short delay helps users perceive the system is working
  };

  return (
    <Box>
      <Box sx={{ 
        display: 'flex', 
        flexDirection: { xs: 'column', sm: 'row' },
        justifyContent: 'space-between', 
        alignItems: { xs: 'flex-start', sm: 'center' }, 
        mb: 3,
        gap: 2
      }}>
        <Typography variant="h4" component="h1">
          Sessões de Votação
        </Typography>
        <Box sx={{ 
          display: 'flex', 
          flexDirection: { xs: 'column', sm: 'row' },
          gap: 1,
          width: { xs: '100%', sm: '50%' }
        }}>
          <Button
            component={RouterLink}
            to="/pautas"
            variant="outlined"
            fullWidth
            sx={{ 
              minWidth: { xs: '100%', sm: 'auto' }
            }}
          >
            Ir para Pautas
          </Button>
          <Button
            component={RouterLink}
            to="/"
            variant="outlined"
            fullWidth
            sx={{ 
              minWidth: { xs: '100%', sm: 'auto' }
            }}
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