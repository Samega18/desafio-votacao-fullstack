import React from 'react';
import { Formik, Form, Field, FormikHelpers } from 'formik';
import * as Yup from 'yup';
import { TextField, Button, Paper, Typography, Box } from '@mui/material';
import { PautaRequest } from '../../types';
import { PautaService } from '../../services/PautaService';

const validationSchema = Yup.object({
  titulo: Yup.string().required('O título é obrigatório').max(100, 'O título deve ter no máximo 100 caracteres'),
  descricao: Yup.string().required('A descrição é obrigatória').max(500, 'A descrição deve ter no máximo 500 caracteres'),
});

const initialValues: PautaRequest = {
  titulo: '',
  descricao: '',
};

interface PautaFormProps {
  onSuccess?: () => void;
}

const PautaForm: React.FC<PautaFormProps> = ({ onSuccess }) => {
  const handleSubmit = async (values: PautaRequest, { resetForm, setSubmitting }: FormikHelpers<PautaRequest>) => {
    try {
      await PautaService.criarPauta(values);
      resetForm();
      if (onSuccess) {
        onSuccess();
      }
      alert('Pauta criada com sucesso!');
    } catch (error) {
      console.error('Erro ao criar pauta:', error);
      alert('Erro ao criar pauta. Tente novamente.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Paper sx={{ p: 3, mb: 3 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Nova Pauta
      </Typography>
      <Formik
        initialValues={initialValues}
        validationSchema={validationSchema}
        onSubmit={handleSubmit}
      >
        {({ isSubmitting, errors, touched }) => (
          <Form>
            <Box mb={2}>
              <Field
                as={TextField}
                fullWidth
                id="titulo"
                name="titulo"
                label="Título"
                variant="outlined"
                error={touched.titulo && Boolean(errors.titulo)}
                helperText={touched.titulo && errors.titulo}
              />
            </Box>
            <Box mb={3}>
              <Field
                as={TextField}
                fullWidth
                id="descricao"
                name="descricao"
                label="Descrição"
                variant="outlined"
                multiline
                rows={4}
                error={touched.descricao && Boolean(errors.descricao)}
                helperText={touched.descricao && errors.descricao}
              />
            </Box>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Criando...' : 'Criar Pauta'}
            </Button>
          </Form>
        )}
      </Formik>
    </Paper>
  );
};

export default PautaForm; 