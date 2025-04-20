import React, { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Typography, Box, Button, Paper, Tabs, Tab } from '@mui/material';
import PautaList from '../components/Pauta/PautaList';
import PautaForm from '../components/Pauta/PautaForm';

const PautasPage: React.FC = () => {
  const [tabIndex, setTabIndex] = useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabIndex(newValue);
  };

  const handleFormSuccess = () => {
    setTabIndex(0); // Mudar para a aba de listagem após criar uma pauta
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Pautas
        </Typography>
        <Button
          component={RouterLink}
          to="/"
          variant="outlined"
        >
          Voltar para Início
        </Button>
      </Box>

      <Paper sx={{ mb: 4 }}>
        <Tabs value={tabIndex} onChange={handleTabChange} centered>
          <Tab label="Listar Pautas" />
          <Tab label="Nova Pauta" />
        </Tabs>
      </Paper>

      <Box sx={{ mt: 3 }}>
        {tabIndex === 0 ? (
          <PautaList />
        ) : (
          <PautaForm onSuccess={handleFormSuccess} />
        )}
      </Box>
    </Box>
  );
};

export default PautasPage; 