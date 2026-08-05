# Contributing

## AI agents

If a PR was developed with an AI coding agent, mention the tool and model in the PR description.

Project-specific guidelines for agents are documented here so they can be followed from the start.

## Frontend

### UI component libraries

This project uses two UI libraries. Do not introduce a third.

- **PrimeReact** — preferred for components: buttons, cards, messages, panels, tabs
- **Ant Design** — already present for `Radio`, `Tabs`, and file upload; do not expand its usage

### Form inputs

In the company survey megaform (`frontend/src/components/company-survey-v2/`), text inputs and selects are mostly unstyled native elements. Do not add custom CSS to make native inputs look like antd or PrimeReact components — stick to one of these two approaches:

- Native unstyled `<input>` / `<select>`
- PrimeReact input components

### Buttons

Use `<Button>` from `primereact/button` or a plain native `<button>`. Do not use `Button` from antd in the survey form.

### Validation

Validation logic for form fields can be placed in Zummon (`zummon/src/commonMain/`) if the logic is reusable across frontend and backend. For simple frontend-only validations, local pure functions in the component file are acceptable.

### PR descriptions

Include a summary of what the PR does and any non-obvious decisions made.