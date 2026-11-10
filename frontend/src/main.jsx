import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
// material UI imports
import { ThemeProvider, CssBaseline } from '@mui/material';
// import the custom theme for the app
import theme from './theme/theme.js';

import './index.css'
import App from './App.jsx'

// Inter font
import '@fontsource/inter/400.css';
import '@fontsource/inter/500.css';
import '@fontsource/inter/600.css';
import '@fontsource/inter/700.css';

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
      <App />
      </BrowserRouter>
    </ThemeProvider>
  </StrictMode>
);