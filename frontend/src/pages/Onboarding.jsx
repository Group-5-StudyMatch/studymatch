import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { useFetch } from '../hooks/useFetch'
import { getStudent } from '../api/studentApi'
import { getSkills, submitOnboarding } from '../api/onboardingApi'
import Status from '../components/Status'

// the backend accepts 0 to 3; these labels are a proposal for the team
// we map these values so the user doesn't just see numbers
const LEVELS = [
  { value: 0, label: 'None' },
  { value: 1, label: 'Basic' },
  { value: 2, label: 'Intermediate' },
  { value: 3, label: 'Advanced' },
]

// bouncer for backend errors - translate error codes
function errorMessage(err) {
  const status = err.response?.status
  if (status === 409) return 'Onboarding was already done, or this student already has grades.'
  if (status === 400) return 'The questionnaire must answer every skill.'
  if (status === 404) return 'Student not found.'
  return err.message
}

export default function Onboarding() {
    const { id } = useParams()
  const navigate = useNavigate()

  const student = useFetch(() => getStudent(id), [id])
  const skills = useFetch(getSkills)

  const [answers, setAnswers] = useState({}) // { skillId: level }
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  const choose = (skillId, level) => setAnswers({ ...answers, [skillId]: level })

  const handleSubmit = (e) => {
    e.preventDefault()
    setError(null)

    const missing = skills.data.filter((s) => answers[s.id] === undefined)
    if (missing.length > 0) {
      setError(`Please answer: ${missing.map((s) => s.name).join(', ')}`)
      return
    }

    setSaving(true)
    submitOnboarding(
      id,
      skills.data.map((s) => ({ skillId: s.id, level: answers[s.id] })),
    )
      .then(() => navigate(`/students/${id}/profile`))
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setSaving(false))
  }

  return (
    <>
      <p><Link to="/students">&larr; Back to students</Link></p>
      <h1>Self-assessment{student.data ? `: ${student.data.name}` : ''}</h1>
      <p>Rate your current level in each skill. This builds your initial profile until you have grades.</p>

      <Status loading={student.loading || skills.loading} error={student.error || skills.error} />

      {skills.data && skills.data.length > 0 && (
        <form onSubmit={handleSubmit} style={{ maxWidth: 520 }}>
          {skills.data.map((s) => (
            <fieldset key={s.id} style={{ marginBottom: 12 }}>
              <legend>{s.name}</legend>
              {LEVELS.map((l) => (
                <label key={l.value} style={{ marginRight: 16 }}>
                  <input
                    type="radio"
                    name={`skill-${s.id}`}
                    checked={answers[s.id] === l.value}
                    onChange={() => choose(s.id, l.value)}
                  />{' '}
                  {l.label}
                </label>
              ))}
            </fieldset>
          ))}

          <Status error={error} />
          <button type="submit" disabled={saving}>
            {saving ? 'Saving...' : 'Submit self-assessment'}
          </button>
        </form>
      )}
    </>
  )
}