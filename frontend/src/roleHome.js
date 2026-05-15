export function roleHome(role) {
  const routes = {
    STUDENT: 'student-overview',
    OFFICER: 'officer-overview',
    DEPARTMENT_STAFF: 'department-overview',
    ADMIN: 'admin-overview'
  };
  return routes[role] || 'login';
}
