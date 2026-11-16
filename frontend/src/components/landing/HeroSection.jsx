import { Link } from 'react-router'

const HIGHLIGHTS = ['Photo & GPS evidence', 'AI-assisted routing', 'Transparent status updates']

// Illustrative sample data for the static product preview only.
const PREVIEW_ISSUES = [
  { title: 'Pothole on MG Road', tag: 'Roads', status: 'In Progress', tone: 'bg-amber-100 text-amber-700' },
  { title: 'Overflowing garbage bin', tag: 'Sanitation', status: 'Verified', tone: 'bg-blue-100 text-blue-700' },
  { title: 'Streetlight not working', tag: 'Electricity', status: 'Resolved', tone: 'bg-emerald-100 text-emerald-700' },
]

function CheckIcon() {
  return (
    <svg viewBox="0 0 20 20" className="size-4 text-blue-600" fill="currentColor" aria-hidden="true">
      <path
        fillRule="evenodd"
        d="M16.7 5.3a1 1 0 0 1 0 1.4l-7.5 7.5a1 1 0 0 1-1.4 0L3.3 9.7a1 1 0 1 1 1.4-1.4l3.8 3.79 6.8-6.8a1 1 0 0 1 1.4 0Z"
        clipRule="evenodd"
      />
    </svg>
  )
}

function ProductPreview() {
  return (
    <div className="relative" aria-hidden="true">
      <div className="absolute -inset-4 rounded-3xl bg-linear-to-tr from-blue-100 via-sky-50 to-white blur-2xl" />
      <div className="relative overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-xl shadow-slate-900/5">
        <div className="flex items-center gap-1.5 border-b border-slate-100 px-4 py-3">
          <span className="size-2.5 rounded-full bg-slate-200" />
          <span className="size-2.5 rounded-full bg-slate-200" />
          <span className="size-2.5 rounded-full bg-slate-200" />
          <span className="ml-3 text-xs font-medium text-slate-400">Issue overview</span>
        </div>

        {/* Stylised map */}
        <div className="relative h-44 bg-blue-50 bg-[linear-gradient(to_right,rgb(191_219_254/0.6)_1px,transparent_1px),linear-gradient(to_bottom,rgb(191_219_254/0.6)_1px,transparent_1px)] bg-size-[28px_28px]">
          <div className="absolute left-0 right-0 top-1/2 h-3 -translate-y-1/2 bg-white/80" />
          <div className="absolute bottom-0 left-1/3 top-0 w-3 bg-white/80" />
          {[
            'left-[22%] top-[28%] bg-amber-500',
            'left-[58%] top-[18%] bg-blue-600',
            'left-[72%] top-[58%] bg-emerald-500',
            'left-[40%] top-[66%] bg-blue-600',
          ].map((pin) => (
            <span key={pin} className={`absolute flex size-4 items-center justify-center rounded-full ring-4 ring-white ${pin}`} />
          ))}
        </div>

        <ul className="divide-y divide-slate-100">
          {PREVIEW_ISSUES.map((issue) => (
            <li key={issue.title} className="flex items-center justify-between gap-3 px-4 py-3">
              <div className="min-w-0">
                <p className="truncate text-sm font-semibold text-slate-800">{issue.title}</p>
                <p className="text-xs text-slate-400">{issue.tag}</p>
              </div>
              <span className={`shrink-0 rounded-full px-2.5 py-1 text-xs font-semibold ${issue.tone}`}>{issue.status}</span>
            </li>
          ))}
        </ul>
      </div>

      <div className="absolute -bottom-5 -left-4 hidden items-center gap-3 rounded-xl border border-slate-200 bg-white px-4 py-3 shadow-lg shadow-slate-900/5 sm:flex">
        <span className="flex size-9 items-center justify-center rounded-lg bg-blue-50 text-blue-600">
          <svg viewBox="0 0 24 24" className="size-5" fill="none" stroke="currentColor" strokeWidth="1.8">
            <path strokeLinecap="round" strokeLinejoin="round" d="M9.5 3.5h5M12 3.5v3m-6 4.5a6 6 0 1 0 12 0 6 6 0 0 0-12 0Zm3 0h6M12 8v6" />
          </svg>
        </span>
        <div>
          <p className="text-xs text-slate-400">AI classification</p>
          <p className="text-sm font-semibold text-slate-800">Pothole detected</p>
        </div>
      </div>
    </div>
  )
}

export default function HeroSection() {
  return (
    <section id="home" className="relative overflow-hidden bg-linear-to-b from-blue-50/70 to-white">
      <div className="mx-auto grid max-w-7xl items-center gap-14 px-4 py-20 sm:px-6 lg:grid-cols-2 lg:gap-12 lg:px-8 lg:py-28">
        <div>
          <span className="inline-flex items-center gap-2 rounded-full border border-blue-200 bg-white px-3 py-1 text-xs font-semibold text-blue-700 shadow-sm">
            <span className="size-1.5 rounded-full bg-blue-600" />
            AI-powered civic reporting
          </span>

          <h1 className="mt-6 text-4xl font-extrabold leading-tight tracking-tight text-slate-900 sm:text-5xl lg:text-6xl">
            Report civic issues.
            <span className="block text-blue-600">Track them to resolution.</span>
          </h1>

          <p className="mt-6 max-w-xl text-lg leading-8 text-slate-600">
            CivicConnect lets citizens report potholes, garbage, broken streetlights and more with a photo and a map
            pin. AI identifies the problem and sends it to the right department, so you can follow every step until it's
            fixed.
          </p>

          <div className="mt-8 flex flex-col gap-3 sm:flex-row">
            <Link
              to="/report"
              className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-6 py-3 text-sm font-semibold text-white shadow-md shadow-blue-600/25 transition hover:bg-blue-700"
            >
              Report an Issue
              <svg viewBox="0 0 20 20" className="size-4" fill="currentColor" aria-hidden="true">
                <path
                  fillRule="evenodd"
                  d="M3 10a1 1 0 0 1 1-1h9.59l-3.3-3.3a1 1 0 1 1 1.42-1.4l5 5a1 1 0 0 1 0 1.4l-5 5a1 1 0 0 1-1.42-1.4l3.3-3.3H4a1 1 0 0 1-1-1Z"
                  clipRule="evenodd"
                />
              </svg>
            </Link>
            <Link
              to="/#how-it-works"
              className="inline-flex items-center justify-center rounded-lg border border-slate-200 bg-white px-6 py-3 text-sm font-semibold text-slate-700 shadow-sm transition hover:border-slate-300 hover:bg-slate-50"
            >
              See how it works
            </Link>
          </div>

          <ul className="mt-8 flex flex-wrap gap-x-6 gap-y-2">
            {HIGHLIGHTS.map((item) => (
              <li key={item} className="flex items-center gap-2 text-sm font-medium text-slate-600">
                <CheckIcon />
                {item}
              </li>
            ))}
          </ul>
        </div>

        <ProductPreview />
      </div>
    </section>
  )
}
