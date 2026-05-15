import { describe, expect, it } from 'vitest';
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { authModeConfig, authModeEntries, brandName } from './authPresentation';

describe('auth presentation', () => {
  it('uses the complaint system brand and exposes the expected auth modes', () => {
    expect(brandName).toBe('Complaint System');
    expect(authModeEntries().map((item) => item.mode)).toEqual([
      'login',
      'register',
      'forgot',
      'public-submit',
      'public-track'
    ]);
    expect(authModeConfig('public-submit').title).toBe('Submit anonymously');
    expect(authModeConfig('public-track').title).toBe('Track anonymous case');
  });

  it('removes unrelated template and legal-login copy from App.vue', () => {
    const source = readFileSync(resolve(process.cwd(), 'src/App.vue'), 'utf8');
    expect(source).not.toContain('CareerCompass');
    expect(source).not.toContain('Log in with Google');
    expect(source).not.toContain('Privacy Policy');
    expect(source).not.toContain('Terms of Service');
  });
});
