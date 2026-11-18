import { Link as RouterLink } from 'react-router-dom'
import { Box, Button, Typography } from '@mui/material'

export default function NotFound() {
  return (
    <Box sx={{ textAlign: 'center', py: 8 }}>
      <Typography variant="h2" component="p" color="primary" sx={{ fontWeight: 700 }}>
        404
      </Typography>
      <Typography variant="h5" component="h2" sx={{ mb: 1 }}>
        Page not found
      </Typography>
      <Typography color="text.secondary" sx={{ mb: 3 }}>
        The page you are looking for does not exist or was moved.
      </Typography>
      <Button variant="contained" component={RouterLink} to="/">
        Go to the start page
      </Button>
    </Box>
  )
}