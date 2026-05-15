export function statusTone(status) {
  const tones = {
    SUBMITTED: 'info',
    UNDER_REVIEW: 'warning',
    AWAITING_STUDENT_INFO: 'danger',
    ASSIGNED: 'primary',
    IN_PROGRESS: 'warning',
    RESOLVED: 'success',
    FOLLOW_UP_REQUESTED: 'danger',
    CLOSED: 'info'
  };
  return tones[status] || 'info';
}

export function caseMetrics(cases) {
  const list = Array.isArray(cases) ? cases : [];
  return {
    total: list.length,
    open: list.filter((item) => !['RESOLVED', 'CLOSED'].includes(item.status)).length,
    resolved: list.filter((item) => item.status === 'RESOLVED').length,
    followUps: list.filter((item) => item.status === 'FOLLOW_UP_REQUESTED').length
  };
}

export function formatStatus(status) {
  return String(status || '').replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, (char) => char.toUpperCase());
}

