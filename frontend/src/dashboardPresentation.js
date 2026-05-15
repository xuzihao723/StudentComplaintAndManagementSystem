export function navForRole(role) {
  if (role === 'ADMIN') {
    return [
      group('Workspace', [{ view: 'admin-overview', label: 'Overview' }]),
      group('Cases', [{ view: 'all-cases', label: 'All Cases' }]),
      group('Management', [
        { view: 'users', label: 'Users' },
        { view: 'reports', label: 'Reports' },
        { view: 'audit-logs', label: 'Audit Logs' }
      ]),
      group('Settings', [
        { view: 'settings', label: 'Settings' },
        { view: 'profile-password', label: 'Profile & Password' }
      ])
    ];
  }
  if (role === 'OFFICER') {
    return [
      group('Workspace', [{ view: 'officer-overview', label: 'Overview' }]),
      group('Cases', [
        { view: 'review-queue', label: 'Review Queue' },
        { view: 'all-cases', label: 'All Cases' },
        { view: 'overdue-cases', label: 'Overdue Cases' }
      ]),
      group('Reports', [{ view: 'reports', label: 'Reports' }]),
      group('Account', [{ view: 'profile-password', label: 'Profile & Password' }])
    ];
  }
  if (role === 'DEPARTMENT_STAFF') {
    return [
      group('Workspace', [{ view: 'department-overview', label: 'Overview' }]),
      group('Cases', [
        { view: 'department-cases', label: 'My Department Cases' },
        { view: 'overdue-cases', label: 'Overdue Cases' },
        { view: 'internal-notes', label: 'Internal Notes' }
      ]),
      group('Account', [{ view: 'profile-password', label: 'Profile & Password' }])
    ];
  }
  if (role === 'STUDENT') {
    return [
      group('Workspace', [{ view: 'student-overview', label: 'Overview' }]),
      group('Cases', [
        { view: 'submit-case', label: 'Submit Case' },
        { view: 'my-cases', label: 'My Cases' },
        { view: 'anonymous-tracking', label: 'Anonymous Tracking' }
      ]),
      group('Account', [{ view: 'profile-password', label: 'Profile & Password' }])
    ];
  }
  return [group('Workspace', [{ view: 'login', label: 'Overview' }])];
}

export function flattenNav(groups) {
  return (Array.isArray(groups) ? groups : []).flatMap((group) => group.items || []);
}

export function dashboardMetrics(cases) {
  const list = Array.isArray(cases) ? cases : [];
  return {
    total: list.length,
    open: list.filter((item) => !['RESOLVED', 'CLOSED'].includes(item.status)).length,
    resolved: list.filter((item) => item.status === 'RESOLVED').length,
    followUps: list.filter((item) => ['FOLLOW_UP_REQUESTED', 'REOPEN_REQUESTED'].includes(item.status)).length,
    overdue: list.filter((item) => item.overdue).length,
    highPriority: list.filter((item) => ['HIGH', 'URGENT'].includes(item.priority)).length
  };
}

export function recentCases(cases, limit = 5) {
  return [...(Array.isArray(cases) ? cases : [])]
    .sort((left, right) => new Date(right.updatedAt || 0) - new Date(left.updatedAt || 0))
    .slice(0, limit);
}

function group(label, items) {
  return { label, items };
}
