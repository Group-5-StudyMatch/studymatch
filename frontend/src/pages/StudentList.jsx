import { Link } from 'react-router-dom'
import { useFetch } from '../hooks/useFetch'
import { getStudents } from '../api/studentApi'
import Status from '../components/Status'

export default function StudentList() {
  const { data, loading, error } = useFetch(getStudents)

  return (
    <>
      <h1>Students</h1>
      <p><Link to="/students/new">+ New student</Link></p>
      <Status loading={loading} error={error} />
      {data && data.length === 0 && <p>No students yet.</p>}
      {data && data.length > 0 && (
        <table style={{ width: '100%', textAlign: 'left' }}>
          <thead>
            <tr>
              <th>Number</th><th>Name</th><th>Email</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {data.map((s) => (
              <tr key={s.id}>
                <td>{s.institutionalNumber}</td>
                <td>{s.name}</td>
                <td>{s.email}</td>
                <td>
                  <Link to={`/students/${s.id}/edit`}>edit</Link>{' | '}
                  <Link to={`/students/${s.id}/history`}>history</Link>{' | '}
                  <Link to={`/students/${s.id}/profile`}>profile</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  )
}