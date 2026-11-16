import { Link } from 'react-router'
import Logo from '../common/Logo.jsx'

const FOOTER_COLUMNS = [
  {
    title: 'Platform',
    links: [
      { label: 'Features', to: '/#features' },
      { label: 'How it works', to: '/#how-it-works' },
      { label: 'Report an Issue', to: '/report' },
    ],
  },
  {
    title: 'Company',
    links: [
      { label: 'About', to: '/#about' },
      { label: 'Login', to: '/login' },
    ],
  },
]

const CURRENT_YEAR = new Date().getFullYear()

export default function Footer() {
  return (
    <footer className="bg-slate-950 text-slate-400">
      <div className="mx-auto max-w-7xl px-4 py-14 sm:px-6 lg:px-8">
        <div className="grid gap-10 md:grid-cols-[2fr_1fr_1fr]">
          <div className="max-w-sm">
            <Logo light />
            <p className="mt-4 text-sm leading-6">
              An AI-powered platform that helps citizens report civic issues and helps local authorities resolve them
              faster and more openly.
            </p>
          </div>

          {FOOTER_COLUMNS.map((column) => (
            <div key={column.title}>
              <h3 className="text-sm font-semibold text-white">{column.title}</h3>
              <ul className="mt-4 space-y-3">
                {column.links.map((link) => (
                  <li key={link.label}>
                    <Link to={link.to} className="text-sm transition hover:text-white">
                      {link.label}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        <div className="mt-12 flex flex-col gap-2 border-t border-slate-800 pt-6 text-xs sm:flex-row sm:items-center sm:justify-between">
          <p>&copy; {CURRENT_YEAR} CivicConnect. All rights reserved.</p>
          <p>Maps &copy; OpenStreetMap contributors</p>
        </div>
      </div>
    </footer>
  )
}
