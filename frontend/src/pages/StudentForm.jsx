// import the usual react state and lifecycle hooks
import {useEffect, useState} from "react";
// import the routing tools we need from React Router
import { Link, useNavigate, useParams } from 'react-router-dom'

import { createStudent, getStudent, updateStudent } from '../api/studentApi'
import Status from '../components/Status'

// a blank slate for the form so we dont have to type this out multiple times
const EMPTY = { institutionalNumber: '', name: '', email: '' }

export default function StudentForm() {
  // look at the URL. is there an 'id' sitting in it?
  const { id } = useParams()
  // if there is an id, we are editing an existing student. if not, we are creating a new one
  const isEdit = Boolean(id)
  // the teleportation tool to send the user back to the list after they save the form
  const navigate = useNavigate()
  // the state holding whatever the user has typed into the form. start with a blank slate.
  const [form, setForm] = useState(EMPTY)
   //(only starts as true if we are editing)
  const [loading, setLoading] = useState(isEdit)
  // are we sending data to the backend right now? (to disable the form while we wait for a response)
  const [saving, setSaving] = useState(false)
  // general form errors like "the server is down" or "you are not allowed to do that"
  const [error, setError] = useState(null)
  // specific errors for specific boxes
  const [fieldErrors, setFieldErrors] = useState({})

  // when the page first loads, check if we need to fetch an existing student
  useEffect(() => {
    // if its a new student, skip this all. The form should be blank.
    if (!isEdit) return

    // go ask the backend for the student's info
    getStudent(id)
      .then((res) => 
      setForm({
        institutionalNumber: res.data.institutionalNumber,
        name: res.data.name,
        email: res.data.email,
      }),
      )
      // if the request fails, show an error message
      .catch((err) => setError(err.response?.data?.message || err.message))
      // stop loading whether it worked or failed, so the form will show up
      .finally(() => setLoading(false))
  }, [id, isEdit])

  // this runs every single time the user presses a key in an input box.
  const handleChange = (e) => {
    // keep everything currently in the form, but update the box that changed.
  setForm({ ...form, [e.target.name]: e.target.value })
  }

  // make sure the user didnt leave any boxes blank, or type an invalid email
  const validate = () => {
    const errors = {}
    
    if(!form.institutionalNumber.trim()) errors.institutionalNumber = 'Required'
    if(!form.name.trim()) errors.name = 'Required'
    if(!form.email.trim()) errors.email = 'Required'
    else if (!/^\S+@\S+\.\S+$/.test(form.email.trim())) errors.email = 'Invalid email'
    return errors
    }

    // to handle form submission
  const handleSubmit = (e) => {
    // prevent the browser from reloading the page
    e.preventDefault()
    setError(null) // clear any old errors

    // run checks
    const errors = validate()
    setFieldErrors(errors)

    // if the errors object isnt empty, stop here and show the errors
    if (Object.keys(errors).length > 0) return

    // clean the data before sending it to the backend
    const payLoad = {
      institutionalNumber: form.institutionalNumber.trim(),
      name: form.name.trim(),
      email: form.email.trim(),
    }

    // show the "saving..." message and disable the form
    setSaving(true)

    // if we are editing, send an update request. if not, send a create request
    const request = isEdit ? updateStudent(id, payLoad) : createStudent(payLoad)

    request
      .then((res) => navigate(isEdit ? '/students' : `/students/${res.data.id}/onboarding`)) // redirects the student to onboarding
      .catch((err) => {
        // a 409 error usually means Conflict. Would be telling the email already exists
        if (err.response?.status === 409) {
          setError('A student with that email already exists.')
        } else {
          setError(err.response?.data?.message || err.message)
        }
      })
      .finally(() => setSaving(false)) // stop showing the "saving..." message
    }

    // A helper function to stamp out input boxes so we dont repeat the same html blocks
    const field = (name, label, type = 'text') => (
      <div style={{ marginBottom: 12 }}>
        <label htmlFor={name} style={{ display: 'block' }}>{label}</label>
        <input
          id={name}
          name={name}
          type={type}
          value={form[name]}
          onChange={handleChange}
          style={{ width: '100%', padding: 6 }}
      />
     {fieldErrors[name] && (
        <small style={{ color: 'crimson' }}>{fieldErrors[name]}</small>
      )}
    </div>
  )
    // if we are still fetching the existent students data, just show the spinner
    if (loading) return <Status loading />

    
    return (
    <>
      
      <h1>{isEdit ? 'Edit student' : 'New student'}</h1>
      
      <form onSubmit={handleSubmit} noValidate style={{ maxWidth: 420 }}>
        
        
        {field('institutionalNumber', 'Institutional number')}
        {field('name', 'Name')}
        {field('email', 'Email', 'email')}

        <Status error={error} />

        <button type="submit" disabled={saving}>
          {saving ? 'Saving...' : 'Save'}
        </button>{' '}
        
        <Link to="/students">Cancel</Link>
      </form>
    </>
  )
}