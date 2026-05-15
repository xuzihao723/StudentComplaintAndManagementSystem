export function priorityTone(priority) {
  return {
    URGENT: 'danger',
    HIGH: 'warning',
    NORMAL: 'primary',
    LOW: 'info'
  }[priority] || 'info';
}

export function formatDueState(item) {
  if (item?.overdue) return 'Overdue';
  if (!item?.dueAt) return 'No SLA';
  const due = new Date(item.dueAt).getTime();
  if (Number.isNaN(due)) return 'No SLA';
  return due < Date.now() ? 'Due soon' : 'On track';
}
