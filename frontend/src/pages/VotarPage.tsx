import React, { useState, useEffect } from 'react';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  Button, 
  CircularProgress, 
  Paper,
  Alert,
  Divider
} from '@mui/material';
import VotoForm from '../components/Voto/VotoForm';
import { SessaoVotacaoDTO } from '../types';
import { SessaoVotacaoService } from '../services/SessaoVotacaoService';
import { format, isAfter } from 'date-fns';
import { ptBR } from 'date-fns/locale';

const VotarPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [sessao, setSessao] = useState<SessaoVotacaoDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSessao = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        const sessaoId = parseInt(id, 10);
        const data = await SessaoVotacaoService.obterSessao(sessaoId);
        setSessao(data);
        setError(null);
      } catch (error) {
        console.error('Erro ao carregar sessão:', error);
        setError('Não foi possível carregar os detalhes da sessão. Tente novamente.');
      } finally {
        setLoading(false);
      }
    };

    fetchSessao();
  }, [id]);

  const handleVotoSuccess = () => {
    if (sessao) {
      navigate(`/sessoes/${sessao.id}/resultado`);
    } else {
      navigate('/sessoes');
    }
  };

  const isSessaoAtiva = (sessao: SessaoVotacaoDTO): boolean => {
    if (sessao.encerrada) return false;
    return !isAfter(new Date(), new Date(sessao.dataFechamento));
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !sessao) {
    return (
      <Box sx={{ my: 2 }}>
        <Typography color="error">{error || 'Sessão não encontrada'}</Typography>
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
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Votar
        </Typography>
        <Button
          component={RouterLink}
          to="/sessoes"
          variant="outlined"
        >
          Voltar para Sessões
        </Button>
      </Box>

      <Paper sx={{ p: 3, mb: 4 }}>
        <Typography variant="h6" gutterBottom>
          Detalhes da Sessão de Votação
        </Typography>
        <Typography variant="body1">
          <strong>Pauta:</strong> {sessao.tituloPauta}
        </Typography>
        <Typography variant="body2" gutterBottom>
          <strong>ID da Sessão:</strong> {sessao.id}
        </Typography>
        <Divider sx={{ my: 2 }} />
        <Typography variant="body2">
          <strong>Início:</strong> {format(new Date(sessao.dataAbertura), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
        </Typography>
        <Typography variant="body2">
          <strong>Término:</strong> {format(new Date(sessao.dataFechamento), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
        </Typography>
        <Typography variant="body2">
          <strong>Status:</strong> {sessao.encerrada ? 'Encerrada' : isSessaoAtiva(sessao) ? 'Em andamento' : 'Fechada'}
        </Typography>
      </Paper>

      {!isSessaoAtiva(sessao) ? (
        <Alert severity="error" sx={{ mb: 3 }}>
          Esta sessão de votação não está disponível para receber votos. A sessão pode ter sido encerrada ou o prazo expirado.
        </Alert>
      ) : (
        <VotoForm
          sessaoId={sessao.id}
          tituloPauta={sessao.tituloPauta}
          onSuccess={handleVotoSuccess}
        />
      )}
    </Box>
  );
};

export default VotarPage; 