// Grab the state hook from React
import { useState } from 'react'
// routing tools to create links and grab the student id from the URL
import { Link, useParams } from 'react-router-dom'
// bring in our trusty custom fetch hook to handle the API heavy lifting
import { useFetch } from '../hooks/useFetch'
// import the API calls to talk to the backend
import { getStudent } from '../api/studentApi'
import {
  addAcademicRecord,
  getAcademicRecords,
  getCourseUnits,
} from '../api/academicApi'

// our reusable loading/error component
import Status from '../components/Status'

// a blank slate for the form so we can easily reset it later
const EMPTY = { courseUnitId: '', assessmentName: '', grade: '' }

// helper to translate backend errors(400/404/409) into human readable text
function saveErrorMessage(err){
  const status = err.response?.status
  if (status === 409) return 'This student already has a grade for that assessment in that course unit.'
  if (status === 400) return 'Invalid data: the grade must be a whole number from 0 to 20'
  if (status === 404) return 'Student or Course Unit not found.'
  // fallback for anything else
  return err.message
}


export default function AcademicHistory() {
  // grab the specific students ID right out of the URL
  const { id } = useParams()

  // fire off three separate API calls as the page loads
  // 1. get student's basic info
  const student = useFetch(() => getStudent(id), [id])
  // 2. get all grades they have
  const records = useFetch(() => getAcademicRecords(id), [id])
  // 3. get the master list of all possible course units for the dropdown menu
  const units = useFetch(getCourseUnits)


  // set up the standard form state variables
  const [form, setForm] = useState(EMPTY)
  const [fieldErrors, setFieldErrors] = useState({})
  const [saveError, setSaveError] = useState(null)
  const [saving, setSaving] = useState(false)


  // since a record only holds courseUnitId, this turns the array of course units into a
  // dictionary to find the name of a course without looping through the array every time.
  const unitsById = Object.fromEntries((units.data || []).map((u) => [u.id, u]))

  // updates the form state whenever the user types something or selects a dropdown menu
  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  // validates the data before sending it to the backend.
  const validate = () => {
    const errors = {}
    if(!form.courseUnitId) errors.courseUnitId = 'Choose a course unit'
    if(!form.assessmentName.trim()) errors.assessmentName = 'Required'

    // convert the grade string into a number for math checks
    const grade = Number(form.grade)
    if (form.grade === '') errors.grade = "required"
    // ensure its not a decimal and sits strictly between 0 and 20
    else if (!Number.isInteger(grade) || grade < 0 || grade > 20)
      errors.grade = 'Whole number from 0 to 20'

    return errors
  }


  // the user clicked "Add grade"
  const handleSubmit = (e) => {
    e.preventDefault() // stop the page from reloading
    setSaveError(null) // clear old errors

    // run the checks
    const errors = validate()
    setFieldErrors(errors)
    if (Object.keys(errors).length > 0) return // stop if there are errors

    setSaving(true) // show the 'Saving...' exit

    //send data to the backend
    addAcademicRecord(id, {
      courseUnitId: Number(form.courseUnitId),
      assessmentName: form.assessmentName.trim(),
      grade: Number(form.grade),
    })
    .then(() => {
      setForm({ ...EMPTY, courseUnitId: form.courseUnitId })

      // refresh the grades table   
      records.reload()
    })
    // catch any backend errors
    .catch((err) => setSaveError(saveErrorMessage(err)))
    .finally(() => setSaving(false))
  }

  // helper method to display red error messages under a specific input box   
  const errorText = (name) =>
    fieldErrors[name] && <small style={{ color: 'crimson' }}>{fieldErrors[name]}</small>

  return (
    <>

      <p><Link to="/students">&larr; Back to students</Link></p>
      

      <h1>Academic history{student.data ? `: ${student.data.name}` : ''}</h1>
      <Status loading={student.loading} error={student.error} />


      <h2>Grades</h2>
      <Status loading={records.loading} error={records.error} />
      

      {records.data && records.data.length === 0 && <p>No academic records yet.</p>}
      

      {records.data && records.data.length > 0 && (
        <table style={{ width: '100%', textAlign: 'left' }}>
          <thead>
            <tr><th>Course unit</th><th>Assessment</th><th>Grade</th></tr>
          </thead>
          <tbody>

            {records.data.map((r) => (
              <tr key={r.id}>

                <td>{unitsById[r.courseUnitId]?.name ?? `Unit #${r.courseUnitId}`}</td>
                <td>{r.assessmentName}</td>
                <td>{r.grade}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}


      <h2>Add a grade</h2>
      <Status loading={units.loading} error={units.error} />
      

      {units.data && units.data.length === 0 && (
        <p>No course units exist yet. Create them in the backend first.</p>
      )}
      

      {units.data && units.data.length > 0 && (
        <form onSubmit={handleSubmit} noValidate style={{ maxWidth: 420 }}>
          

          <div style={{ marginBottom: 12 }}>
            <label htmlFor="courseUnitId" style={{ display: 'block' }}>Course unit</label>
            <select
              id="courseUnitId"
              name="courseUnitId"
              value={form.courseUnitId}
              onChange={handleChange}
              style={{ width: '100%', padding: 6 }}
            >
              <option value="">Select...</option>

              {units.data.map((u) => (
                <option key={u.id} value={u.id}>
                  {u.name}{u.course ? ` (${u.course.name})` : ''}
                </option>
              ))}
            </select>
            {errorText('courseUnitId')}
          </div>


          <div style={{ marginBottom: 12 }}>
            <label htmlFor="assessmentName" style={{ display: 'block' }}>Assessment name</label>
            <input
              id="assessmentName"
              name="assessmentName"
              value={form.assessmentName}
              onChange={handleChange}
              placeholder="e.g. Teste 1"
              style={{ width: '100%', padding: 6 }}
            />
            {errorText('assessmentName')}
          </div>


          <div style={{ marginBottom: 12 }}>
            <label htmlFor="grade" style={{ display: 'block' }}>Grade (0 to 20)</label>
            <input
              id="grade"
              name="grade"
              type="number"
              min="0"
              max="20"
              step="1"
              value={form.grade}
              onChange={handleChange}
              style={{ width: '100%', padding: 6 }}
            />
            {errorText('grade')}
          </div>


          <Status error={saveError} />
          

          <button type="submit" disabled={saving}>
            {saving ? 'Saving...' : 'Add grade'}
          </button>
        </form>
      )}
    </>
  )

  
}