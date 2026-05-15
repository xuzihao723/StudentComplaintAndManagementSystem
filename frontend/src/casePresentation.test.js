import { describe, expect, it } from 'vitest';
import { caseMetrics, formatStatus, statusTone } from './casePresentation';

describe('case presentation helpers', () => {
  it('uses distinct tones for action-critical states', () => {
    expect(statusTone('RESOLVED')).toBe('success');
    expect(statusTone('FOLLOW_UP_REQUESTED')).toBe('danger');
    expect(statusTone('AWAITING_STUDENT_INFO')).toBe('danger');
  });

  it('summarizes operational case counts', () => {
    expect(caseMetrics([
      { status: 'SUBMITTED' },
      { status: 'IN_PROGRESS' },
      { status: 'RESOLVED' },
      { status: 'FOLLOW_UP_REQUESTED' },
      { status: 'CLOSED' }
    ])).toEqual({ total: 5, open: 3, resolved: 1, followUps: 1 });
  });

  it('formats machine status for human labels', () => {
    expect(formatStatus('AWAITING_STUDENT_INFO')).toBe('Awaiting Student Info');
  });
});

