
import { createTheme } from '@mui/material/styles';

// StudyMatch design colors, defined in D1
export const colors = {
  primary: '#4338CA',
  primaryHover: '#3730A3',
  primaryDark: '#312E81',

  text: '#111827',
  textSecondary: '#4B5563',

  background: '#F9FAFB',
  surface: '#FFFFFF',
  border: '#D1D5DB',

  success: '#15803D',
  warning: '#B45309',
  error: '#B91C1C',
};

const theme = createTheme({
  palette: {
    mode: 'light',

    primary: {
      main: colors.primary,
      dark: colors.primaryHover,
      contrastText: '#FFFFFF',
    },

    background: {
      default: colors.background,
      paper: colors.surface,
    },

    text: {
      primary: colors.text,
      secondary: colors.textSecondary,
    },

    divider: colors.border,

    success: { main: colors.success },
    warning: { main: colors.warning },
    error: { main: colors.error },
  },

  typography: {
    fontFamily: 'Inter, Arial, sans-serif',

    h4: {
      fontSize: '1.5rem',
      fontWeight: 600,
    },

    h5: {
      fontSize: '1.25rem',
      fontWeight: 600,
    },

    h6: {
      fontSize: '1.125rem',
      fontWeight: 600,
    },

    body1: {
      fontSize: '1rem',
    },

    body2: {
      fontSize: '0.875rem',
    },

    button: {
      textTransform: 'none',
      fontWeight: 600,
    },
  },

  shape: {
    borderRadius: 10,
  },

  spacing: 8,

  components: {
    MuiButton: {
      defaultProps: {
        disableElevation: true,
      },
      styleOverrides: {
        root: {
          borderRadius: 8,
          minHeight: 40,
          padding: '8px 16px',
          textTransform: 'none',
        },
      },
    },

    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
        },
      },
    },

    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
      },
    },

    MuiCssBaseline: {
      styleOverrides: {
        body: {
          backgroundColor: colors.background,
        },
      },
    },
  },
});

export default theme;
