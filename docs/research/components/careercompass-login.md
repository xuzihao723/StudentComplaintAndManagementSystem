# Component Spec: CareerCompass Login

## Source Content

- Brand: CareerCompass
- Heading: Welcome back!
- Supporting text: Please enter your details
- Fields: Email, Password
- Options: Remember for 30 days, Forgot password?
- Primary action: Log in
- Secondary action: Log in with Google
- Signup prompt: Don't have an account? Sign Up
- Legal links: Privacy Policy, Terms of Service

## Desktop Structure

- Root: `min-height: 100dvh`, `max-height: 100dvh`, two equal columns.
- Left showcase: `padding: 48px`, gray diagonal gradient, grid texture, logo top, legal links bottom.
- Character stage: `width: 550px`, `height: 500px`.
- Purple block: `left: 70px`, `width: 180px`, `height: 400px`, color `#6c3ff5`.
- Charcoal block: `left: 240px`, `width: 120px`, `height: 310px`, color `#2d2d2d`.
- Coral block: `left: 0`, `width: 240px`, `height: 200px`, color `#ff9b6b`, `border-radius: 120px 120px 0 0`.
- Yellow block: `left: 310px`, `width: 140px`, `height: 230px`, color `#e8d754`, `border-radius: 70px 70px 0 0`.
- Right login: centered, `padding: 32px`, form max width `420px`.

## Responsive

- At `max-width: 1080px`, hide the showcase, switch to one column, show centered mobile brand.
- At `max-width: 620px`, reduce side padding and stack remember/forgot row.

## Motion

- Button hover: first label fades/translates right; overlay with primary background fades in.
- Character tracking: clone maps pointer delta to per-block CSS variables for face offsets and skew. This mirrors the original React component's `faceX`, `faceY`, and `bodySkew` calculations.
- Whole stage movement: the illustration stage adds a subtle panel translate so the left visual area follows the pointer instead of only the pupils moving.
- Email focus: purple block grows and translates right; charcoal reacts upward for 800ms.
- Hidden password with text: uses the same privacy posture as typing.
- Visible password with text: resets skew and forces the characters to look left/up, with randomized purple glances.
- Blink behavior: purple and charcoal eyes close independently on randomized timers.
