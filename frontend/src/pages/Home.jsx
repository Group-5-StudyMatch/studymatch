// bring in the custom hook for fetching data from an API
import { useFetch } from '../hooks/useFetch';

// bring in the function that pings Spring Boot to see if its running
import {getHealth} from '../api/healthApi';


import {
  Box,
  Typography,
  Card,
  CardContent,
  Stack,
  Alert,
  CircularProgress,
  Button,
} from '@mui/material';

import { Link } from 'react-router-dom';


// the main home page component.
export default function Home() {

    // check the health of the backend right away
    const {data, loading, error} = useFetch(getHealth)
    
    return (
        <Box sx={{ maxWidth: 900, mx: 'auto', py: 3 }}>
      <Stack spacing={3}>

        {/* Page heading */}
        <Box>
          <Typography variant="h4" component="h1">
            StudyMatch
          </Typography>

          <Typography color="text.secondary">
            Student profiles and group formation platform
          </Typography>
        </Box>

        {/* Main content */}
        <Card variant="outlined">
          <CardContent>
            <Stack spacing={2}>

              <Typography variant="h6">
                System status
              </Typography>

              {loading && (
                <CircularProgress
                  size={24}
                  aria-label="Checking backend connection"
                />
              )}

              {error && (
                <Alert severity="error">
                  Backend connection failed: {error}
                </Alert>
              )}

              {!loading && data && (
                <Alert severity="success">
                  Backend is healthy!
                </Alert>
              )}

              {data && (
                <Typography
                  variant="body2"
                  component="pre"
                  sx={{ overflowX: 'auto' }}
                >
                  {JSON.stringify(data, null, 2)}
                </Typography>
              )}

              <Box>
                <Button
                  variant="contained"
                  component={Link}
                  to="/students"
                >
                  View students
                </Button>
              </Box>

            </Stack>
          </CardContent>
        </Card>

      </Stack>
    </Box>
    );
} 