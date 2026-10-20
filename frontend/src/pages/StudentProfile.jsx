import { Link, useParams } from 'react-router-dom'
import { useFetch } from '../hooks/useFetch'

// calls  for the backend for the student info and their calculated profile
import { getStudent } from '../api/studentApi'
import { getCompetencies } from '../api/competencyApi'

// our loading and error display component
import Status from '../components/Status'

// a dictionary to translate backend codes into nice readable text
const SOURCE_LABELS = {
  ACADEMIC: 'Academic records',
  SELF_ASSESSMENT: 'Self-assessment',
}


export default function StudentProfile() {
  // Grab the student ID right out of the URL (e.g., /students/123/profile)
  const { id } = useParams()
  // make two API calls at the same time - one for the name, one for the skills
  const student = useFetch(() => getStudent(id), [id])
  const competencies = useFetch(() => getCompetencies(id), [id])

  // highest score first; entries without a score (future "no data") go last
  const sorted = [...(competencies.data || [])].sort(
    (a, b) => (b.score ?? -1) - (a.score ?? -1),
  )

  return (
    <>
      <p><Link to="/students">&larr; Back to students</Link></p>
      <h1>Profile{student.data ? `: ${student.data.name}` : ''}</h1>
      <Status loading={student.loading} error={student.error} />

      <h2>Competencies</h2>
      <Status loading={competencies.loading} error={competencies.error} />

      {competencies.data && competencies.data.length === 0 && (
        <p>
          No competency data yet. Scores appear once the student has grades in
          course units that are mapped to skills.{' '}
          <Link to={`/students/${id}/history`}>Go to academic history</Link>
        </p>
      )}

      {sorted.length > 0 && (
        <table style={{ width: '100%', textAlign: 'left' }}>
          <thead>
            <tr>
              <th>Competency</th>
              <th style={{ width: '40%' }}>Score</th>
              <th>Source</th>
            </tr>
          </thead>
          <tbody>
            {sorted.map((c) => {
              const hasScore = c.score !== null && c.score !== undefined
              const pct = hasScore ? Math.round(c.score * 100) : 0
              // Until the backend sends `source`, every score comes from grades
              const source = c.source ?? 'ACADEMIC'
              return (
                <tr key={c.skillId}>
                  <td>{c.skillName}</td>
                  <td>
                    {hasScore ? (
                      <>
                        <progress value={pct} max="100" style={{ width: '70%' }} />{' '}
                        {pct}% ({(c.score * 20).toFixed(1)} / 20)
                      </>
                    ) : (
                      <em>No data</em>
                    )}
                  </td>
                  <td>{hasScore ? SOURCE_LABELS[source] ?? source : '-'}</td>
                </tr>
              )
            })}
          </tbody>
        </table>
      )}

      <p style={{ marginTop: 24 }}>
        <Link to={`/students/${id}/history`}>Academic history</Link>
      </p>
    </>
  )
}