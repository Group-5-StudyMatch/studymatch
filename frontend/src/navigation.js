import DashboardIcon from '@mui/icons-material/Dashboard'
import PeopleIcon from '@mui/icons-material/People'
import GroupsIcon from '@mui/icons-material/Groups'
import ManageAccountsIcon from '@mui/icons-material/ManageAccounts'
import SchoolIcon from '@mui/icons-material/School'
import MenuBookIcon from '@mui/icons-material/MenuBook'
import GradingIcon from '@mui/icons-material/Grading'
import PersonIcon from '@mui/icons-material/Person'
import HistoryIcon from '@mui/icons-material/History'
import FlagIcon from '@mui/icons-material/Flag'
import DescriptionIcon from '@mui/icons-material/Description'

// the items follow section 4 of docs/ui-decisions.md.
// Rule: never show a link to a screen that does not exist yet.
// When a screen is built, change its `ready` to true.
const NAV_ITEMS = [
  { label: 'Dashboard', to: '/dashboard', icon: DashboardIcon, roles: ['ADMIN', 'TEACHER'], ready: false },
  { label: 'Users', to: '/admin/users', icon: ManageAccountsIcon, roles: ['ADMIN'], ready: false },
  { label: 'Students', to: '/students', icon: PeopleIcon, roles: ['ADMIN', 'TEACHER'], ready: true },
  { label: 'Courses', to: '/admin/courses', icon: SchoolIcon, roles: ['ADMIN'], ready: false },
  { label: 'Course units', to: '/admin/course-units', icon: MenuBookIcon, roles: ['ADMIN'], ready: false },
  { label: 'Academic records', to: '/admin/academic-records', icon: GradingIcon, roles: ['ADMIN'], ready: false },
  { label: 'Formations', to: '/formations', icon: GroupsIcon, roles: ['ADMIN', 'TEACHER'], ready: false },
  { label: 'My profile', to: '/me/profile', icon: PersonIcon, roles: ['STUDENT'], ready: false },
  { label: 'My history', to: '/me/history', icon: HistoryIcon, roles: ['STUDENT'], ready: false },
  { label: 'My goals', to: '/me/goals', icon: FlagIcon, roles: ['STUDENT'], ready: false },
  { label: 'My portfolio', to: '/me/portfolio', icon: DescriptionIcon, roles: ['STUDENT'], ready: false },
]

export function navItemsFor(role) {
  return NAV_ITEMS.filter((item) => item.ready && item.roles.includes(role))
}

// breadcrumbs are built from the URL. Numeric ids are skipped so no raw id is shown.
const LABELS = {
  students: 'Students',
  edit: 'Edit student',
  profile: 'Profile',
  history: 'Academic history',
  onboarding: 'Questionnaire',
  portfolio: 'Portfolio',
  formations: 'Formations',
  dashboard: 'Dashboard',
  users: 'Users',
  courses: 'Courses',
  'course-units': 'Course units',
  'academic-records': 'Academic records',
}
const SKIPPED = new Set(['admin', 'me'])
const SINGULAR = { students: 'student', formations: 'formation' }

export function buildCrumbs(pathname) {
  const segments = pathname.split('/').filter(Boolean)
  const crumbs = []
  let path = ''

  segments.forEach((segment, index) => {
    path += `/${segment}`
    if (SKIPPED.has(segment) || /^\d+$/.test(segment)) return

    let label = LABELS[segment]
    if (segment === 'new') {
      label = `New ${SINGULAR[segments[index - 1]] ?? ''}`.trim()
    }
    if (label) crumbs.push({ label, to: path })
  })

  return crumbs
}