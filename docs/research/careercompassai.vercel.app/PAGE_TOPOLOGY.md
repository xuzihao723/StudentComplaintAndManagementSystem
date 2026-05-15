# CareerCompass Login Page Topology

Target: https://careercompassai.vercel.app/login

## Layout

- Single viewport-height authentication page.
- Desktop uses a two-column grid at `lg` width and above.
- Left column is a fixed visual brand panel with a gray diagonal gradient, subtle 20px grid texture, logo at top left, animated character illustration centered, and legal links at bottom left.
- Right column is a centered login form with `max-width: 420px`.
- Below desktop breakpoint, the left visual panel is hidden and the logo moves above the form.

## Sections

- Brand showcase: static layout plus mouse-driven character eye movement in the clone.
- Login form: heading, supporting text, email field, password field with visibility toggle, remember checkbox, forgot password link, primary login button, Google login button, and signup prompt.
- Legal links: footer-style text links in the left panel.

## Interaction Model

- Static single-screen page with hover states and small form controls.
- Primary and Google buttons use a group-hover reveal: default label translates right and fades out; colored overlay fades in with icon.
- Password eye toggles field visibility.
- The original rendered character eyes at neutral position. The clone adds pointer-following pupils to emulate the intended animated feel without changing layout.
