import { describe, expect, it } from 'vitest';
import { formatDueState, priorityTone } from './advancedPresentation';

describe('advanced presentation helpers', () => {
  it('marks overdue cases clearly', () => {
    expect(formatDueState({ overdue: true })).toBe('Overdue');
  });

  it('uses danger tone for urgent priority', () => {
    expect(priorityTone('URGENT')).toBe('danger');
  });
});

