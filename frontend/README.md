# Sistema de Votação para Cooperativas - Frontend

Este projeto é o frontend do Sistema de Votação para Cooperativas, desenvolvido com React, TypeScript, Material UI e Vite.

## Tecnologias Utilizadas

- React 18
- TypeScript
- Material UI 5
- React Router 6
- Formik e Yup para formulários
- React Query para gerenciamento de estado
- Recharts para visualização de dados
- Axios para comunicação com a API
- Date-fns para manipulação de datas

## Requisitos

- Node.js 18+ instalado
- NPM 8+ ou Yarn

## Como Executar o Projeto

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   cd <pasta-do-repositorio>/frontend
   ```

2. Instale as dependências:
   ```bash
   npm install
   # ou
   yarn
   ```

3. Inicie o servidor de desenvolvimento:
   ```bash
   npm run dev
   # ou
   yarn dev
   ```

4. O navegador deve abrir automaticamente em `http://localhost:3000`

## Funcionalidades Implementadas

- **Gestão de Pautas**:
  - Cadastro de novas pautas
  - Listagem com paginação e busca por título/descrição
  - Visualização detalhada

- **Sessões de Votação**:
  - Abertura de sessões com tempo configurável
  - Listagem com paginação, busca e filtros por status
  - Monitoramento de status (em andamento, encerrada, fechada)

- **Sistema de Votação**:
  - Identificação automática do associado via CPF
  - Interface para votação com validação
  - Controle de votos únicos por associado/sessão

- **Resultados**:
  - Visualização gráfica em tempo real
  - Estatísticas detalhadas
  - Indicador de aprovação/rejeição

- **Associados**:
  - Cadastro de novos associados
  - Listagem com paginação e filtros
  - Busca por nome ou CPF

## Recursos Avançados

- **Paginação**: Todas as listagens contam com paginação no lado do cliente e servidor
- **Busca**: Campos de busca em todas as listagens principais
- **Filtros**: Opções de filtro por status e outros critérios relevantes
- **Integração Automática**: Busca de associados por CPF durante o voto
- **Responsividade**: Interface adaptada para todos os tamanhos de tela

## Estrutura do Projeto

- `src/components`: Componentes reutilizáveis
- `src/pages`: Páginas da aplicação
- `src/services`: Serviços para comunicação com a API
- `src/types`: Definições de tipos TypeScript
- `src/routes`: Configuração de rotas
- `src/styles`: Estilos e temas

## Integração com Backend

O projeto está configurado para se conectar com o backend na URL `http://localhost:8080/api/v1`.

Se o backend estiver sendo executado em uma porta ou URL diferente, ajuste o arquivo `src/services/api.ts` conforme necessário.

## Build para Produção

Para gerar o build de produção, execute:

```bash
npm run build
# ou
yarn build
```

O resultado será gerado na pasta `dist` e pode ser servido usando qualquer servidor HTTP estático.
