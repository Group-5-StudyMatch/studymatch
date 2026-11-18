import { useCallback, useMemo, useState } from 'react'
import { Alert, Snackbar } from '@mui/material'
import { ToastContext } from '../context/ToastContext'

export default function ToastProvider({ children }) {
  const [open, setOpen] = useState(false)
  const [toast, setToast] = useState({ message: '', severity: 'info', key: 0 })

  const show = useCallback((message, severity = 'info') => {
    // a new key restarts the auto-hide timer when toasts follow each other
    setToast({ message, severity, key: Date.now() })
    setOpen(true)
  }, [])

  const api = useMemo(
    () => ({
      show,
      success: (message) => show(message, 'success'),
      error: (message) => show(message, 'error'),
      warning: (message) => show(message, 'warning'),
      info: (message) => show(message, 'info'),
    }),
    [show],
  )

  const handleClose = (_event, reason) => {
    if (reason === 'clickaway') return
    setOpen(false)
  }

  return (
    <ToastContext.Provider value={api}>
      {children}
      <Snackbar
        key={toast.key}
        open={open}
        autoHideDuration={4000}
        onClose={handleClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert severity={toast.severity} variant="filled" onClose={handleClose} sx={{ width: '100%' }}>
          {toast.message}
        </Alert>
      </Snackbar>
    </ToastContext.Provider>
  )
}