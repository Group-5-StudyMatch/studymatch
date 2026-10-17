import {Route, Routes} from "react-router-dom";

// importing the different pages of the app to be used in the routing
import Layout from './components/Layout'
import Home from './pages/Home'
import StudentList from './pages/StudentList'
import StudentForm from './pages/StudentForm'
import AcademicHistory from './pages/AcademicHistory'
import StudentProfile from './pages/StudentProfile'
import Onboarding from './pages/Onboarding'

// the main app component 
export default function App() {
  return(
    // <Routes> looks at the current URL and renders the first <Route> that matches the path.
    <Routes>
      <Route element={<Layout />}>
      <Route path="/" element={<Home />} />
      <Route path="/students" element={<StudentList />} />
      <Route path="/students/new" element={<StudentForm />} />
      <Route path="/students/:id/edit" element={<StudentForm />} />
      <Route path="/students/:id/history" element={<AcademicHistory />} />
      <Route path="/students/:id/profile" element={<StudentProfile />} />
      <Route path="/onboarding" element={<Onboarding />} />

      </Route>
      
    </Routes>
  )
}