import React from 'react';
import { Formik, Form, Field, FormikHelpers } from 'formik';
import * as Yup from 'yup';
import { TextField, Button, Paper, Typography, Box } from '@mui/material';
import { SessaoRequest } from '../../types';
import { SessaoVotacaoService } from '../../services/SessaoVotacaoService';

const validationSchema = Yup.object({
  duracaoMinutos: Yup.number()
    .required('A duração é obrigatória')
    .integer('A duração deve ser um número inteiro')
    .min(1, 'A duração deve ser de pelo menos 1 minuto')
    .max(60, 'A duração não pode ser maior que 60 minutos')
});

const initialValues: SessaoRequest = {
  duracaoMinutos: 1
};

interface SessaoFormProps {
  pautaId: number;
  onSuccess?: () => void;
}

const SessaoForm: React.FC<SessaoFormProps> = ({ pautaId, onSuccess }) => {
  const handleSubmit = async (values: SessaoRequest, { setSubmitting }: FormikHelpers<SessaoRequest>) => {
    try {
      await SessaoVotacaoService.abrirSessao(pautaId, values);
      if (onSuccess) {
        onSuccess();
      }
      alert('Sessão de votação aberta com sucesso!');
    } catch (error) {
      console.error('Erro ao abrir sessão de votação:', error);
      alert('Erro ao abrir sessão de votação. Tente novamente.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Paper sx={{ p: 3, mb: 3 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Abrir Sessão de Votação
      </Typography>
      <Typography variant="body1" sx={{ mb: 3 }}>
        Defina o tempo de duração da sessão. Após este período, a sessão será encerrada automaticamente.
      </Typography>
      <Formik
        initialValues={initialValues}
        validationSchema={validationSchema}
        onSubmit={handleSubmit}
      >
        {({ isSubmitting, errors, touched }) => (
          <Form>
            <Box mb={3}>
              <Field
                as={TextField}
                fullWidth
                id="duracaoMinutos"
                name="duracaoMinutos"
                label="Duração (minutos)"
                type="number"
                variant="outlined"
                InputProps={{ inputProps: { min: 1, max: 60 } }}
                error={touched.duracaoMinutos && Boolean(errors.duracaoMinutos)}
                helperText={touched.duracaoMinutos && errors.duracaoMinutos}
              />
            </Box>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Abrindo...' : 'Abrir Sessão'}
            </Button>
          </Form>
        )}
      </Formik>
    </Paper>
  );
};

export default SessaoForm;