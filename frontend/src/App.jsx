import { useEffect, useState } from 'react'
import { getHealth } from './api/healthApi'

function App() {
  const [status, setStatus] = useState('Checking backend...')

  useEffect(() => {
    getHealth()
      .then((res) => setStatus(`Backend OK: ${JSON.stringify(res.data)}`))
      .catch((err) => setStatus(`Backend unreachable: ${err.message}`))
  }, [])

  return (
    <div>
      <h1>StudyMatch</h1>
      <p>{status}</p>
    </div>
  )
}

export default App