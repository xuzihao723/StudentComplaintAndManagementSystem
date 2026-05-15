import { describe, expect, it } from 'vitest';
import { dashboardMetrics, flattenNav, navForRole, recentCases } from './dashboardPresentation';

describe('dashboard presentation helpers', () => {
  const cases = [
    { id: 1, status: 'SUBMITTED', priority: 'URGENT', overdue: true, updatedAt: '2026-05-11T02:00:00Z' },
    { id: 2, status: 'RESOLVED', priority: 'NORMAL', overdue: false, updatedAt: '2026-05-10T02:00:00Z' },
    { id: 3, status: 'IN_PROGRESS', priority: 'HIGH', overdue: false, updatedAt: '2026-05-12T02:00:00Z' }
  ];

  it('adds case operations destinations for admins', () => {
    expect(flattenNav(navForRole('ADMIN')).map((item) => item.label)).toEqual([
      'Overview',
      'All Cases',
      'Users',
      'Reports',
      'Audit Logs',
      'Settings',
      'Profile & Password'
    ]);
  });

  it('keeps student task destinations visible', () => {
    expect(flattenNav(navForRole('STUDENT')).map((item) => item.label)).toEqual([
      'Overview',
      'Submit Case',
      'My Cases',
      'Anonymous Tracking',
      'Profile & Password'
    ]);
  });

  it('groups navigation by actual work area', () => {
    expect(navForRole('OFFICER').map((group) => group.label)).toEqual(['Workspace', 'Cases', 'Reports', 'Account']);
  });

  it('summarizes dashboard case signals', () => {
    expect(dashboardMetrics(cases)).toMatchObject({
      total: 3,
      open: 2,
      overdue: 1,
      highPriority: 2
    });
  });

  it('sorts recent cases by update time', () => {
    expect(recentCases(cases, 2).map((item) => item.id)).toEqual([3, 1]);
  });
});
