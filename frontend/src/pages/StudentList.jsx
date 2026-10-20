import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { useFetch } from '../hooks/useFetch'
import { getStudents } from '../api/studentApi'
import Status from '../components/Status'
import { filterStudents } from '../utils/studentFilters'

export default function StudentList() {
  const { data, loading, error } = useFetch(getStudents)

  // Text typed in the search bar
  const [query, setQuery] = useState('')

  // Recompute the visible list only when the data or the query changes
  const visibleStudents = useMemo(
    () => (data ? filterStudents(data, query) : []),
    [data, query]
  )

  return (
    <>
      <h1>Students</h1>
      <p><Link to="/students/new">+ New student</Link></p>
      <Status loading={loading} error={error} />
      {data && data.length === 0 && <p>No students yet.</p>}
      {data && data.length > 0 && (

<>
  <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
    <input
      type="search"
      placeholder="Search by name or institutional number"
      value={query}
      onChange={(e) => setQuery(e.target.value)}
      aria-label="Search students"
      style={{ flex: 1 }}
    />
    {query && (
      <button type="button" onClick={() => setQuery('')}>
        Clear
      </button>
    )}
  </div>

  {visibleStudents.length === 0 ? (
    <p>No students match your search.</p>
  ) : (
    <table style={{ width: '100%', textAlign: 'left' }}>
      <thead>
        <tr>
          <th>Number</th>
          <th>Name</th>
          <th>Email</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {visibleStudents.map((s) => (
          <tr key={s.id}>
            <td>{s.institutionalNumber}</td>
            <td>{s.name}</td>
            <td>{s.email}</td>
            <td>
              <Link to={`/students/${s.id}/edit`}>edit</Link>{' | '}
              <Link to={`/students/${s.id}/history`}>history</Link>{' | '}
              <Link to={`/students/${s.id}/profile`}>profile</Link>{' | '}
              <Link to={`/students/${s.id}/onboarding`}>onboarding</Link>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  )}
</>

      )}
    </>
  )
}