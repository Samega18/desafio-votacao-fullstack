import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from '../components/Layout/Layout';
import HomePage from '../pages/HomePage';
import PautasPage from '../pages/PautasPage';
import PautaDetailPage from '../pages/PautaDetailPage';
import SessoesPage from '../pages/SessoesPage';
import VotarPage from '../pages/VotarPage';
import ResultadoPage from '../pages/ResultadoPage';
import AssociadosPage from '../pages/AssociadosPage';

const AppRoutes: React.FC = () => {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/pautas" element={<PautasPage />} />
        <Route path="/pautas/:id" element={<PautaDetailPage />} />
        <Route path="/sessoes" element={<SessoesPage />} />
        <Route path="/sessoes/:id/votar" element={<VotarPage />} />
        <Route path="/sessoes/:id/resultado" element={<ResultadoPage />} />
        <Route path="/associados" element={<AssociadosPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
};

export default AppRoutes; 