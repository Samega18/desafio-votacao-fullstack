import React from 'react';
import { Formik, Form, Field, FormikHelpers } from 'formik';
import * as Yup from 'yup';
import { TextField, Button, Paper, Typography, Box } from '@mui/material';
import { AssociadoRequest } from '../../types';
import { AssociadoService } from '../../services/AssociadoService';

const validationSchema = Yup.object({
  nome: Yup.string().required('O nome é obrigatório').max(100, 'O nome deve ter no máximo 100 caracteres'),
  cpf: Yup.string()
    .required('O CPF é obrigatório')
    .length(11, 'O CPF deve ter 11 dígitos')
    .matches(/^\d+$/, 'O CPF deve conter apenas números')
});

const initialValues: AssociadoRequest = {
  nome: '',
  cpf: ''
};

interface AssociadoFormProps {
  onSuccess?: () => void;
}

const AssociadoForm: React.FC<AssociadoFormProps> = ({ onSuccess }) => {
  const handleSubmit = async (values: AssociadoRequest, { resetForm, setSubmitting }: FormikHelpers<AssociadoRequest>) => {
    try {
      await AssociadoService.cadastrarAssociado(values);
      resetForm();
      if (onSuccess) {
        onSuccess();
      }
      alert('Associado cadastrado com sucesso!');
    } catch (error) {
      console.error('Erro ao cadastrar associado:', error);
      alert('Erro ao cadastrar associado. Tente novamente.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Paper sx={{ p: 3, mb: 3 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Cadastrar Associado
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
                id="nome"
                name="nome"
                label="Nome"
                variant="outlined"
                error={touched.nome && Boolean(errors.nome)}
                helperText={touched.nome && errors.nome}
              />
            </Box>
            <Box mb={3}>
              <Field
                as={TextField}
                fullWidth
                id="cpf"
                name="cpf"
                label="CPF (apenas números)"
                variant="outlined"
                error={touched.cpf && Boolean(errors.cpf)}
                helperText={touched.cpf && errors.cpf}
              />
            </Box>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Cadastrando...' : 'Cadastrar'}
            </Button>
          </Form>
        )}
      </Formik>
    </Paper>
  );
};

export default AssociadoForm; 