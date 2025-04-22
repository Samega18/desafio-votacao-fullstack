import React, { useState, useEffect } from 'react';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import { 
  Typography, 
  Box, 
  Button, 
  Paper, 
  Divider, 
  Card, 
  CardContent,
  CircularProgress,
  Tabs,
  Tab
} from '@mui/material';
import SessaoForm from '../components/Sessao/SessaoForm';
import SessaoList from '../components/Sessao/SessaoList';
import { PautaDTO } from '../types';
import { PautaService } from '../services/PautaService';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';

const PautaDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [pauta, setPauta] = useState<PautaDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tabIndex, setTabIndex] = useState(0);

  useEffect(() => {
    const fetchPauta = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        const pautaId = parseInt(id, 10);
        const data = await PautaService.obterPauta(pautaId);
        setPauta(data);
        setError(null);
      } catch (error) {
        console.error('Erro ao carregar detalhes da pauta:', error);
        setError('Não foi possível carregar os detalhes da pauta. Tente novamente.');
      } finally {
        setLoading(false);
      }
    };

    fetchPauta();
  }, [id]);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabIndex(newValue);
  };

  const handleSessaoSuccess = () => {
    fetchPauta();
    setTabIndex(1); // Show the sessions tab so users can see their newly created session
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error || !pauta) {
    return (
      <Box sx={{ my: 2 }}>
        <Typography color="error">{error || 'Pauta não encontrada'}</Typography>
        <Button 
          component={RouterLink} 
          to="/pautas" 
          variant="outlined" 
          sx={{ mt: 2 }}
        >
          Voltar para Pautas
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Detalhes da Pauta
        </Typography>
        <Button
          component={RouterLink}
          to="/pautas"
          variant="outlined"
        >
          Voltar para Pautas
        </Button>
      </Box>

      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h5" component="h2" gutterBottom>
            {pauta.titulo}
          </Typography>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            ID: {pauta.id} | Criado em: {format(new Date(pauta.dataCriacao), 'dd/MM/yyyy HH:mm', { locale: ptBR })}
          </Typography>
          <Divider sx={{ my: 2 }} />
          <Typography variant="body1" paragraph>
            {pauta.descricao}
          </Typography>
        </CardContent>
      </Card>

      <Paper sx={{ mb: 3 }}>
        <Tabs 
          value={tabIndex} 
          onChange={handleTabChange} 
          centered
          variant="fullWidth"
          sx={{
            '& .MuiTab-root': {
              fontSize: { xs: '0.75rem', sm: '0.875rem' },
              minWidth: 0,
              p: { xs: 1, sm: 2 },
              textTransform: 'none'
            }
          }}
        >
          <Tab label="Abrir Sessão de Votação" />
          <Tab label="Sessões Existentes" />
        </Tabs>
      </Paper>

      {tabIndex === 0 && (
        <SessaoForm 
          pautaId={pauta.id} 
          onSuccess={handleSessaoSuccess} 
        />
      )}

      {tabIndex === 1 && (
        <SessaoList pautaId={pauta.id} />
      )}
    </Box>
  );
};

export default PautaDetailPage;