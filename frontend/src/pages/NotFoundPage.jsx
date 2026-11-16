import { Link } from 'react-router'

export default function NotFoundPage() {
  return (
    <section className="flex min-h-[60vh] items-center px-4 py-20">
      <div className="mx-auto max-w-lg text-center">
        <p className="text-sm font-semibold text-blue-600">404</p>
        <h1 className="mt-3 text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">Page not found</h1>
        <p className="mt-4 text-base leading-7 text-slate-600">
          The page you're looking for doesn't exist or has been moved.
        </p>
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
