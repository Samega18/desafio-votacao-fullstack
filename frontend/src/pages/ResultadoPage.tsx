import React, { useState, useEffect } from 'react';
import { useParams, Link as RouterLink } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  Button, 
  CircularProgress, 
  Alert
} from '@mui/material';
import ResultadoDisplay from '../components/Resultado/ResultadoDisplay';
import { ResultadoVotacaoDTO } from '../types';
import { SessaoVotacaoService } from '../services/SessaoVotacaoService';

const ResultadoPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [resultado, setResultado] = useState<ResultadoVotacaoDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchResultado = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        const sessaoId = parseInt(id, 10);
        const data = await SessaoVotacaoService.obterResultado(sessaoId);
        setResultado(data);
        setError(null);
      } catch (error) {
        console.error('Erro ao carregar resultado:', error);
        setError('Não foi possível carregar o resultado da votação. Tente novamente.');
      } finally {
        setLoading(false);
      }
    };

    fetchResultado();
  }, [id]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !resultado) {
    return (
      <Box sx={{ my: 2 }}>
        <Typography color="error">{error || 'Resultado não encontrado'}</Typography>
        <Button 
          component={RouterLink} 
          to="/sessoes" 
          variant="outlined" 
          sx={{ mt: 2 }}
        >
          Voltar para Sessões
        </Button>
      </Box>
    );
  }

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
          Resultado da Votação
        </Typography>
        <Box sx={{ 
          display: 'flex', 
          flexDirection: { xs: 'column', sm: 'row' },
          gap: 1,
          width: { xs: '100%', sm: '50%' }
        }}>
          <Button
            component={RouterLink}
            to={`/sessoes/${resultado.sessaoId}/votar`}
            variant="outlined"
            fullWidth
            sx={{ 
              minWidth: { xs: '100%', sm: 'auto' }
            }}
          >
            Voltar para Votação
          </Button>
          <Button
            component={RouterLink}
            to="/sessoes"
            variant="outlined"
            fullWidth
            sx={{ 
              minWidth: { xs: '100%', sm: 'auto' }
            }}
          >
            Voltar para Sessões
          </Button>
        </Box>
      </Box>

      {resultado.totalVotos === 0 ? (
        <Alert severity="info" sx={{ mb: 3 }}>
          Ainda não há votos registrados nesta sessão.
        </Alert>
      ) : (
        <ResultadoDisplay resultado={resultado} />
      )}
    </Box>
  );
};

export default ResultadoPage; 