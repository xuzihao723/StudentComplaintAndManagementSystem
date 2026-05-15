const STATES = {
  'my-cases': {
    STUDENT: {
      title: 'No cases yet',
      message: 'Start with a clear description, choose the right category, and attach evidence if you have it.',
      action: 'Submit a new case',
      tone: 'neutral'
    }
  },
  'review-queue': {
    OFFICER: {
      title: 'Review queue is clear',
      message: 'New submissions and follow-up requests will appear here when they need attention.',
      action: 'Open all cases',
      tone: 'success'
    }
  },
  'department-cases': {
    DEPARTMENT_STAFF: {
      title: 'No assigned department cases',
      message: 'Cases assigned to your department will show here with SLA and priority signals.',
      action: 'Check overdue cases',
      tone: 'neutral'
    }
  },
  'overdue-cases': {
    default: {
      title: 'No overdue cases',
      message: 'SLA targets are currently on track for the cases visible to you.',
      action: 'Return to overview',
      tone: 'success'
    }
  },
  'all-cases': {
    default: {
      title: 'No cases match these filters',
      message: 'Try clearing search, status, priority, or SLA filters to widen the list.',
      action: 'Clear filters',
      tone: 'neutral'
    }
  },
  'internal-notes': {
    default: {
      title: 'Open a case to add internal notes',
      message: 'Investigation notes are attached to case dossiers and remain hidden from students.',
      action: 'Open a case',
      tone: 'neutral'
    }
  }
};

export function emptyStateForView(view, role) {
  const byView = STATES[view] || {};
  return byView[role] || byView.default || {
    title: 'No records to show',
    message: 'When matching records are available, they will appear in this workspace.',
    action: 'Refresh',
    tone: 'neutral'
  };
}
