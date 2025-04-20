import React, { useState } from 'react';
import { Formik, Form, Field, FormikHelpers } from 'formik';
import * as Yup from 'yup';
import { 
  TextField, 
  Button, 
  Paper, 
  Typography, 
  Box, 
  FormControl, 
  FormLabel, 
  RadioGroup, 
  FormControlLabel, 
  Radio, 
  Alert,
  InputAdornment,
  CircularProgress
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import { OpcaoVoto, VotoRequest, AssociadoDTO } from '../../types';
import { VotoService } from '../../services/VotoService';
import { AssociadoService } from '../../services/AssociadoService';

const validationSchema = Yup.object({
  idAssociado: Yup.string().required('O ID do associado é obrigatório'),
  opcao: Yup.string().required('A opção de voto é obrigatória')
});

const initialValues: Omit<VotoRequest, 'cpf'> & { cpf?: string } = {
  idAssociado: '',
  opcao: OpcaoVoto.SIM
};

interface VotoFormProps {
  sessaoId: number;
  tituloPauta: string;
  onSuccess?: () => void;
}

const VotoForm: React.FC<VotoFormProps> = ({ sessaoId, tituloPauta, onSuccess }) => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [buscandoAssociado, setBuscandoAssociado] = useState(false);
  const [associadoEncontrado, setAssociadoEncontrado] = useState<AssociadoDTO | null>(null);

  const handleSubmit = async (values: Omit<VotoRequest, 'cpf'>, { resetForm, setSubmitting }: FormikHelpers<Omit<VotoRequest, 'cpf'> & { cpf?: string }>) => {
    try {
      setErrorMessage(null);
      
      // Adiciona o CPF do associado encontrado ao objeto de voto
      const votoData: VotoRequest = {
        ...values,
        cpf: associadoEncontrado?.cpf || ''
      };
      
      await VotoService.registrarVoto(sessaoId, votoData);
      resetForm();
      setAssociadoEncontrado(null);
      if (onSuccess) {
        onSuccess();
      }
      alert('Voto registrado com sucesso!');
    } catch (error: any) {
      console.error('Erro ao registrar voto:', error);
      if (error.response?.data?.message) {
        setErrorMessage(error.response.data.message);
      } else {
        setErrorMessage('Erro ao registrar voto. Tente novamente.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const buscarAssociadoPorId = async (id: string, setFieldValue: any) => {
    if (!id) return;
    
    try {
      setBuscandoAssociado(true);
      setErrorMessage(null);
      
      const associado = await AssociadoService.obterAssociado(id);
      
      if (associado) {
        setAssociadoEncontrado(associado);
      }
    } catch (error) {
      console.error('Erro ao buscar associado:', error);
      setErrorMessage('Associado não encontrado com o ID informado.');
      setAssociadoEncontrado(null);
    } finally {
      setBuscandoAssociado(false);
    }
  };

  return (
    <Paper sx={{ p: 3, mb: 3 }}>
      <Typography variant="h5" component="h2" gutterBottom>
        Registrar Voto
      </Typography>
      <Typography variant="subtitle1" gutterBottom>
        Pauta: {tituloPauta}
      </Typography>
      
      {errorMessage ? (
        <Alert severity="error" sx={{ mb: 3 }}>
          {errorMessage}
        </Alert>
      ) : associadoEncontrado ? (
        <Alert severity="success" sx={{ mb: 3 }}>
          Associado encontrado: <strong>{associadoEncontrado.nome}</strong> ({associadoEncontrado.cpf})
        </Alert>
      ) : (
        <Alert severity="info" sx={{ mb: 3 }}>
          Digite o ID do associado e clique na lupa para buscar. Você só poderá votar após identificar um associado válido.
        </Alert>
      )}
      
      <Formik
        initialValues={initialValues}
        validationSchema={validationSchema}
        onSubmit={handleSubmit}
      >
        {({ isSubmitting, errors, touched, values, setFieldValue, resetForm }) => (
          <Form>
            <Box mb={3}>
              <Field
                as={TextField}
                fullWidth
                id="idAssociado"
                name="idAssociado"
                label="ID do Associado"
                variant="outlined"
                error={touched.idAssociado && Boolean(errors.idAssociado)}
                helperText={touched.idAssociado && errors.idAssociado}
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      {buscandoAssociado ? (
                        <CircularProgress size={24} />
                      ) : (
                        <Button
                          size="small"
                          disabled={!values.idAssociado}
                          onClick={() => buscarAssociadoPorId(values.idAssociado, setFieldValue)}
                        >
                          <SearchIcon />
                        </Button>
                      )}
                    </InputAdornment>
                  ),
                }}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  setFieldValue('idAssociado', e.target.value);
                  if (associadoEncontrado && associadoEncontrado.id !== e.target.value) {
                    setAssociadoEncontrado(null);
                  }
                }}
              />
            </Box>
            <Box mb={3}>
              <FormControl component="fieldset">
                <FormLabel component="legend">Seu voto</FormLabel>
                <Field name="opcao">
                  {({ field }: any) => (
                    <RadioGroup {...field} row>
                      <FormControlLabel
                        value={OpcaoVoto.SIM}
                        control={<Radio />}
                        label="SIM"
                      />
                      <FormControlLabel
                        value={OpcaoVoto.NAO}
                        control={<Radio />}
                        label="NÃO"
                      />
                    </RadioGroup>
                  )}
                </Field>
              </FormControl>
            </Box>
            <Box sx={{ display: 'flex', gap: 2 }}>
              <Button
                type="submit"
                variant="contained"
                color="primary"
                disabled={isSubmitting || !associadoEncontrado}
              >
                {isSubmitting ? 'Enviando...' : 'Votar'}
              </Button>
              
              <Button 
                type="button" 
                variant="outlined" 
                color="secondary"
                onClick={() => {
                  resetForm();
                  setAssociadoEncontrado(null);
                  setErrorMessage(null);
                }}
              >
                Limpar
              </Button>
            </Box>
          </Form>
        )}
      </Formik>
    </Paper>
  );
};

export default VotoForm; 