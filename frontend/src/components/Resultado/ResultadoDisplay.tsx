import React from 'react';
import { 
  Paper, 
  Typography, 
  Box, 
  Grid, 
  Divider,
  Chip
} from '@mui/material';
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { ResultadoVotacaoDTO } from '../../types';

interface ResultadoDisplayProps {
  resultado: ResultadoVotacaoDTO;
}

const COLORS = ['#4caf50', '#f44336'];

const ResultadoDisplay: React.FC<ResultadoDisplayProps> = ({ resultado }) => {
  const data = [
    { name: 'SIM', value: resultado.votosSim },
    { name: 'NÃO', value: resultado.votosNao }
  ];

  const RADIAN = Math.PI / 180;
  const renderCustomizedLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent, index }: any) => {
    const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
    const x = cx + radius * Math.cos(-midAngle * RADIAN);
    const y = cy + radius * Math.sin(-midAngle * RADIAN);

    return (
      <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central">
        {`${(percent * 100).toFixed(0)}%`}
      </text>
    );
  };

  return (
    <Paper sx={{ p: 3, mb: 3 }}>
      <Box sx={{ mb: 3 }}>
        <Typography variant="h5" gutterBottom>
          Resultado da Votação
        </Typography>
        <Typography variant="subtitle1" gutterBottom>
          Pauta: {resultado.tituloPauta} (ID: {resultado.pautaId})
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Sessão de votação: {resultado.sessaoId}
        </Typography>
      </Box>

      <Divider sx={{ my: 2 }} />

      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Box sx={{ height: 300 }}>
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={data}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={renderCustomizedLabel}
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {data.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </Box>
        </Grid>
        <Grid item xs={12} md={6}>
          <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', justifyContent: 'center' }}>
            <Typography variant="h6" gutterBottom>
              Resumo da Votação
            </Typography>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body1">Total de votos:</Typography>
              <Typography variant="body1" fontWeight="bold">{resultado.totalVotos}</Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
              <Typography variant="body1" color="success.main">Votos SIM:</Typography>
              <Typography variant="body1" fontWeight="bold" color="success.main">
                {resultado.votosSim} ({resultado.percentualSim.toFixed(1)}%)
              </Typography>
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
              <Typography variant="body1" color="error.main">Votos NÃO:</Typography>
              <Typography variant="body1" fontWeight="bold" color="error.main">
                {resultado.votosNao} ({resultado.percentualNao.toFixed(1)}%)
              </Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', mt: 2 }}>
              <Chip 
                label={resultado.aprovado ? "APROVADA" : "REJEITADA"} 
                color={resultado.aprovado ? "success" : "error"}
                sx={{ 
                  fontSize: '1.2rem', 
                  fontWeight: 'bold', 
                  padding: '20px 16px', 
                  height: 'auto',
                  '& .MuiChip-label': {
                    padding: '0 8px'
                  }
                }}
              />
            </Box>
          </Box>
        </Grid>
      </Grid>
    </Paper>
  );
};

export default ResultadoDisplay; 