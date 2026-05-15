export const brandName = 'Complaint System';

const modes = {
  login: {
    title: 'Welcome back!',
    subtitle: 'Please enter your account details'
  },
  register: {
    title: 'Create student account',
    subtitle: 'Register with your name, email, and password'
  },
  forgot: {
    title: 'Reset password',
    subtitle: 'Generate a demo reset token for this local system'
  },
  'public-submit': {
    title: 'Submit anonymously',
    subtitle: 'File an anonymous public case without logging in'
  },
  'public-track': {
    title: 'Track anonymous case',
    subtitle: 'Use your case number and tracking code'
  }
};

export function authModeConfig(mode) {
  return modes[mode] || modes.login;
}

export function authModeEntries() {
  return Object.entries(modes).map(([mode, config]) => ({ mode, ...config }));
}
