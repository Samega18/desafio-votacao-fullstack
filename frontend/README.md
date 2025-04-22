# Sistema de Votação para Cooperativas - Frontend

<p align="center">
  <img src="./public/vote-icon.svg" alt="Logo do Sistema de Votação" width="120" />
</p>

## 📋 Sobre o Projeto

Este é o frontend do Sistema de Votação para Cooperativas, uma aplicação web desenvolvida para gerenciar processos de votação em assembleias e reuniões de cooperativas.

## 🚀 Tecnologias Utilizadas

- **React 18** - Biblioteca JavaScript para construção de interfaces
- **TypeScript** - Superset tipado de JavaScript
- **Vite 6** - Build tool e dev server ultrarrápido para aplicações modernas
- **Material UI 5** - Framework de componentes React com design system completo
- **React Router 6** - Biblioteca para gerenciamento de rotas
- **Formik & Yup** - Gerenciamento de formulários e validação
- **React Query** - Gerenciamento de estado e cache para dados da API
- **Recharts** - Biblioteca para visualização de dados e gráficos
- **Axios** - Cliente HTTP para requisições à API
- **Date-fns** - Utilitário para manipulação de datas

## ⚙️ Pré-requisitos

Antes de começar, você precisará ter instalado em sua máquina:

- [Node.js](https://nodejs.org/) (versão 18.x ou superior)
- [npm](https://www.npmjs.com/) (versão 8.x ou superior, normalmente vem com o Node.js)

## 🔧 Instalação

1. Clone o repositório

```bash
git clone <url-do-repositorio>
cd Desafio_Votacao/frontend
```

2. Instale as dependências

```bash
npm install
```

## 🖥️ Executando o Projeto

### Ambiente de Desenvolvimento

O projeto utiliza **Vite** como ferramenta de desenvolvimento, oferecendo Hot Module Replacement (HMR) para uma experiência de desenvolvimento mais rápida.

Para iniciar o servidor de desenvolvimento:

```bash
npm run dev
```

O aplicativo estará disponível em [http://localhost:3000](http://localhost:3000) por padrão (a porta pode variar se 3000 estiver em uso).

### Compilação para Produção

Para criar uma build otimizada para produção:

```bash
npm run build
```

Para visualizar a build de produção localmente:

```bash
npm run preview
```

## 📁 Estrutura do Projeto

```
/src
  /assets        # Recursos estáticos (imagens, etc)
  /components    # Componentes React reutilizáveis
    /Associado   # Componentes relacionados a associados
    /Layout      # Componentes de layout da aplicação
    /Pauta       # Componentes relacionados a pautas
    /Resultado   # Componentes de visualização de resultados
    /Sessao      # Componentes de sessões de votação
    /Voto        # Componentes relacionados ao processo de votação
  /pages         # Páginas da aplicação
  /routes        # Configuração de rotas
  /services      # Serviços para comunicação com a API
  /styles        # Estilos e tema da aplicação
  /types         # Definições de tipos TypeScript
  App.tsx        # Componente principal da aplicação
  main.tsx       # Ponto de entrada da aplicação
```

## 🔄 Funcionalidades Implementadas

- **Gestão de Pautas**:
  - Cadastro de novas pautas
  - Listagem com paginação e busca por título/descrição
  - Visualização detalhada de pautas

- **Sessões de Votação**:
  - Abertura de sessões para pautas específicas
  - Configuração de tempo de duração
  - Monitoramento de sessões ativas e encerradas

- **Gestão de Associados**:
  - Cadastro e manutenção de associados
  - Validação de CPF
  - Controle de associados aptos a votar

- **Processo de Votação**:
  - Interface intuitiva para votação
  - Validação para evitar votos duplicados
  - Feedback imediato após votação

- **Resultados**:
  - Visualização gráfica dos resultados
  - Estatísticas detalhadas por pauta
  - Exportação de resultados

## 🛠️ Solução de Problemas

### Porta em uso

Se a porta 3000 estiver em uso, o Vite automaticamente tentará usar a próxima porta disponível. Você também pode especificar uma porta diferente:

```bash
npm run dev -- --port 3001
```

### Problemas com dependências

Se encontrar problemas com as dependências, tente:

```bash
npm ci
# ou para limpar o cache
npm cache clean --force && npm install
```

### Erros de CORS

Se encontrar erros de CORS ao comunicar com o backend, verifique se a URL da API está configurada corretamente no arquivo de serviços.

## 📝 Notas de Desenvolvimento

- O projeto utiliza ESLint para garantir a qualidade do código
- TypeScript é usado em todo o projeto para tipagem estática
- Material UI é utilizado como biblioteca de componentes principal

---

Desenvolvido com ❤️ para o desafio de votação de cooperativas.