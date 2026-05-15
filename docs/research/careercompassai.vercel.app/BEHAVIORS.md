# CareerCompass Login Behaviors

## Extracted Details

- Fonts: Inter weights 400, 500, 600, 700.
- Primary color: `hsl(231 48% 48%)`.
- Background: `hsl(0 0% 100%)`.
- Foreground: `hsl(222.2 84% 4.9%)`.
- Muted text: `hsl(215.4 16.3% 46.9%)`.
- Border: `hsl(214.3 31.8% 91.4%)`.
- Left panel gradient: `#9ca3af` to `#6b7280` to `#4b5563`.
- Illustration colors: `#6c3ff5`, `#2d2d2d`, `#ff9b6b`, `#e8d754`.

## States

- Inputs: rounded full, 48px tall, border at roughly 60% opacity, focus ring and primary border.
- Password button: icon color changes from muted to foreground on hover.
- Primary button: default text translates right 48px and fades out over 300ms; primary overlay fades in over 300ms.
- Google button: same motion pattern as primary login button.
- Legal and form links: underline or color emphasis on hover.
- Mobile: visual panel hidden, logo centered above the form.

## Animation Details From JS Chunk

- The login page passes `isTyping`, `showPassword`, and `passwordLength` into a separate character component.
- Each character block computes its own `faceX`, `faceY`, and `bodySkew` from the mouse position relative to that block's bounding box.
- Formula family:
  - `faceX = clamp((mouseX - centerX) / 20, -15, 15)`
  - `faceY = clamp((mouseY - upperFaceY) / 30, -10, 10)`
  - `bodySkew = clamp(-(mouseX - centerX) / 120, -6, 6)`
- Email focus sets `isTyping`. Purple grows from 400px to 440px, skews about 12deg farther, and shifts right by 40px. Charcoal also changes skew, with a short 800ms reaction where its head position jumps.
- Password text while hidden sets the same "private typing" posture as typing.
- Password text while visible resets block skew to 0deg and forces the pupils toward the left/up direction. A random glance flips the purple pupils briefly.
- Purple and charcoal eyes blink independently on randomized 3-7 second timers, closing to 2px height for 150ms.

## Notes

The target page source and JS chunks were fetched directly. Browser MCP was not available in this environment, so screenshots were not captured through a browser tool; implementation was based on rendered HTML, Tailwind classes, downloaded CSS, and the extracted bundled React animation logic.
