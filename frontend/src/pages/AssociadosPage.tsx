import React, { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Typography, Box, Button, Paper, Tabs, Tab } from '@mui/material';
import AssociadoList from '../components/Associado/AssociadoList';
import AssociadoForm from '../components/Associado/AssociadoForm';

const AssociadosPage: React.FC = () => {
  const [tabIndex, setTabIndex] = useState(0);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setTabIndex(newValue);
  };

  const handleFormSuccess = () => {
    setTabIndex(0); // Take users back to the member list so they can see their new entry
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Associados
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
          <Tab label="Listar Associados" />
          <Tab label="Cadastrar Associado" />
        </Tabs>
      </Paper>

      <Box sx={{ mt: 3 }}>
        {tabIndex === 0 ? (
          <AssociadoList />
        ) : (
          <AssociadoForm onSuccess={handleFormSuccess} />
        )}
      </Box>
    </Box>
  );
};

export default AssociadosPage;