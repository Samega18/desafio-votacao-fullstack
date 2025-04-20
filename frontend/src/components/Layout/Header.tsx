import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';

const Header: React.FC = () => {
  return (
    <AppBar position="static">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          Sistema de Votação para Cooperativas
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <Button color="inherit" component={RouterLink} to="/">
            Início
          </Button>
          <Button color="inherit" component={RouterLink} to="/pautas">
            Pautas
          </Button>
          <Button color="inherit" component={RouterLink} to="/sessoes">
            Sessões
          </Button>
          <Button color="inherit" component={RouterLink} to="/associados">
            Associados
          </Button>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Header; 