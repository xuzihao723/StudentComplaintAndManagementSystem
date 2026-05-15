import { describe, expect, it } from 'vitest';
import { emptyStateForView } from './uiPresentation';

describe('ui presentation helpers', () => {
  it('points students without cases to the submit flow', () => {
    expect(emptyStateForView('my-cases', 'STUDENT')).toMatchObject({
      title: 'No cases yet',
      action: 'Submit a new case'
    });
  });

  it('points officers without queue items to monitoring work', () => {
    expect(emptyStateForView('review-queue', 'OFFICER')).toMatchObject({
      title: 'Review queue is clear',
      action: 'Open all cases'
    });
  });

  it('explains empty overdue queues as an operational success state', () => {
    expect(emptyStateForView('overdue-cases', 'ADMIN')).toMatchObject({
      title: 'No overdue cases',
      tone: 'success'
    });
  });
});
