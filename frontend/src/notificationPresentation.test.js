import { describe, expect, it } from 'vitest';
import { canOpenNotificationCase, unreadNotificationCount } from './notificationPresentation';

describe('notification presentation helpers', () => {
  it('counts only unread notifications', () => {
    expect(unreadNotificationCount([
      { id: 1, readFlag: false },
      { id: 2, readFlag: true },
      { id: 3, readFlag: false }
    ])).toBe(2);
  });

  it('opens only notifications that are bound to a case', () => {
    expect(canOpenNotificationCase({ caseId: 12 })).toBe(true);
    expect(canOpenNotificationCase({ caseId: null })).toBe(false);
    expect(canOpenNotificationCase({})).toBe(false);
  });
});
