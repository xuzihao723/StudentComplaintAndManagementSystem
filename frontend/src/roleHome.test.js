import { describe, expect, it } from 'vitest';
import { roleHome } from './roleHome';

describe('roleHome', () => {
  it('maps each role to its dashboard', () => {
    expect(roleHome('STUDENT')).toBe('student-overview');
    expect(roleHome('OFFICER')).toBe('officer-overview');
    expect(roleHome('DEPARTMENT_STAFF')).toBe('department-overview');
    expect(roleHome('ADMIN')).toBe('admin-overview');
  });
});
