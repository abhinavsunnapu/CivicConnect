# CivicConnect Frontend

The web client for CivicConnect, built with React, Vite, Tailwind CSS and React Router.

## Scripts

| Command | Description |
|---|---|
| `npm install` | Install dependencies |
| `npm run dev` | Start the development server at http://localhost:5173 |
| `npm run build` | Create a production build in `dist/` |
| `npm run preview` | Serve the production build locally |
| `npm run lint` | Lint the source with oxlint |

## Source layout

```
src/
├── assets/       # Static images and icons imported by components
├── components/   # Reusable UI (common/, layout/, landing/)
├── context/      # React context providers (shared app state)
├── hooks/        # Custom React hooks
├── layouts/      # Page shells (navbar + content + footer)
├── pages/        # Route-level pages
├── routes/       # React Router configuration
└── services/     # API clients (to be added with the backend)
```
