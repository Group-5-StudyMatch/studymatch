// filters students by name or institutional number (case-insensitive)
export function filterStudents(students, query) {
  const q = (query || '').trim().toLowerCase()
  if (!q) return students
  return students.filter(
    (s) =>
      s.name.toLowerCase().includes(q) ||
      s.institutionalNumber.toLowerCase().includes(q),
  )
}