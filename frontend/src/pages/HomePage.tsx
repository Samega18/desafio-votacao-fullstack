import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { Typography, Box, Paper, Grid, Button, Card, CardContent, CardActions } from '@mui/material';
import AssignmentIcon from '@mui/icons-material/Assignment';
import HowToVoteIcon from '@mui/icons-material/HowToVote';
import PeopleIcon from '@mui/icons-material/People';
import EqualizerIcon from '@mui/icons-material/Equalizer';

const HomePage: React.FC = () => {
  return (
    <Box>
      <Paper sx={{ p: 4, mb: 4, bgcolor: 'primary.light', color: 'white' }}>
        <Typography variant="h3" gutterBottom>
          Sistema de Votação para Cooperativas
        </Typography>
        <Typography variant="h6">
          Plataforma segura e eficiente para gerenciar votações em assembleias e reuniões cooperativas.
        </Typography>
      </Paper>

      <Grid container spacing={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1 }}>
              <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                <AssignmentIcon fontSize="large" color="primary" />
              </Box>
              <Typography variant="h5" component="h2" gutterBottom align="center">
                Pautas
              </Typography>
              <Typography>
                Crie e gerencie pautas para votação. Defina títulos claros e descrições detalhadas para cada assunto.
              </Typography>
            </CardContent>
            <CardActions>
              <Button 
                component={RouterLink} 
                to="/pautas" 
                fullWidth
                variant="contained"
              >
                Acessar Pautas
              </Button>
            </CardActions>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1 }}>
              <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                <HowToVoteIcon fontSize="large" color="primary" />
              </Box>
              <Typography variant="h5" component="h2" gutterBottom align="center">
                Sessões
              </Typography>
              <Typography>
                Abra sessões de votação com tempo definido. Monitore o andamento e encerramento automático.
              </Typography>
            </CardContent>
            <CardActions>
              <Button 
                component={RouterLink} 
                to="/sessoes" 
                fullWidth
                variant="contained"
              >
                Acessar Sessões
              </Button>
            </CardActions>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1 }}>
              <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                <PeopleIcon fontSize="large" color="primary" />
              </Box>
              <Typography variant="h5" component="h2" gutterBottom align="center">
                Associados
              </Typography>
              <Typography>
                Cadastre associados com validação de CPF. Gerencie a participação nos processos de votação.
              </Typography>
            </CardContent>
            <CardActions>
              <Button 
                component={RouterLink} 
                to="/associados" 
                fullWidth
                variant="contained"
              >
                Acessar Associados
              </Button>
            </CardActions>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <CardContent sx={{ flexGrow: 1 }}>
              <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
                <EqualizerIcon fontSize="large" color="primary" />
              </Box>
              <Typography variant="h5" component="h2" gutterBottom align="center">
                Resultados
              </Typography>
              <Typography>
                Visualize estatísticas detalhadas das votações. Acompanhe os resultados em tempo real.
              </Typography>
            </CardContent>
            <CardActions>
              <Button 
                component={RouterLink} 
                to="/sessoes" 
                fullWidth
                variant="contained"
              >
                Ver Resultados
              </Button>
            </CardActions>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default HomePage; 