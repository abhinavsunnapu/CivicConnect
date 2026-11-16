import { Link } from 'react-router'

export default function Logo({ light = false }) {
  return (
    <Link to="/" className="group inline-flex items-center gap-2.5" aria-label="CivicConnect home">
      <span className="flex size-9 items-center justify-center rounded-lg bg-blue-600 shadow-sm shadow-blue-600/30 transition group-hover:bg-blue-700">
        <svg viewBox="0 0 24 24" className="size-5" fill="none" aria-hidden="true">
          <path
            d="M12 2.5c-3.87 0-7 3.02-7 6.75 0 5.06 7 12.25 7 12.25s7-7.19 7-12.25C19 5.52 15.87 2.5 12 2.5Z"
            fill="#fff"
          />
          <circle cx="12" cy="9.25" r="2.75" fill="#2563eb" />
        </svg>
      </span>
      <span className={`text-lg font-bold tracking-tight ${light ? 'text-white' : 'text-slate-900'}`}>
        Civic<span className={light ? 'text-blue-300' : 'text-blue-600'}>Connect</span>
      </span>
    </Link>
  )
}
