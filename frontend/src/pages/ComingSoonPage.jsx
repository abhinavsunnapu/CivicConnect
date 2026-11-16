import { Link } from 'react-router'

export default function ComingSoonPage({ title, description }) {
  return (
    <section className="flex min-h-[60vh] items-center bg-linear-to-b from-blue-50/70 to-white px-4 py-20">
      <div className="mx-auto max-w-lg text-center">
        <span className="inline-flex rounded-full border border-blue-200 bg-white px-3 py-1 text-xs font-semibold text-blue-700 shadow-sm">
          Coming soon
        </span>
        <h1 className="mt-6 text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">{title}</h1>
        <p className="mt-4 text-base leading-7 text-slate-600">{description}</p>
        <Link
          to="/"
          className="mt-8 inline-flex rounded-lg bg-blue-600 px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700"
        >
          Back to home
        </Link>
      </div>
    </section>
  )
}
