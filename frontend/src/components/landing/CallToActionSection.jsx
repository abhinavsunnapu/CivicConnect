import { Link } from 'react-router'

export default function CallToActionSection() {
  return (
    <section className="bg-white py-20 sm:py-24">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="relative overflow-hidden rounded-3xl bg-linear-to-br from-blue-600 to-blue-800 px-6 py-14 text-center shadow-xl shadow-blue-900/20 sm:px-12 sm:py-16">
          <div className="absolute -right-16 -top-16 size-64 rounded-full bg-white/10 blur-2xl" aria-hidden="true" />
          <div className="absolute -bottom-20 -left-10 size-64 rounded-full bg-sky-400/20 blur-2xl" aria-hidden="true" />

          <h2 className="relative mx-auto max-w-2xl text-3xl font-bold tracking-tight text-white sm:text-4xl">
            Spotted an issue in your neighbourhood?
          </h2>
          <p className="relative mx-auto mt-4 max-w-xl text-base leading-7 text-blue-100">
            Report it in a minute and help your city fix it faster.
          </p>
          <div className="relative mt-8 flex flex-col justify-center gap-3 sm:flex-row">
            <Link
              to="/report"
              className="inline-flex items-center justify-center rounded-lg bg-white px-6 py-3 text-sm font-semibold text-blue-700 shadow-md transition hover:bg-blue-50"
            >
              Report an Issue
            </Link>
            <Link
              to="/login"
              className="inline-flex items-center justify-center rounded-lg border border-white/30 px-6 py-3 text-sm font-semibold text-white transition hover:bg-white/10"
            >
              Login
            </Link>
          </div>
        </div>
      </div>
    </section>
  )
}
