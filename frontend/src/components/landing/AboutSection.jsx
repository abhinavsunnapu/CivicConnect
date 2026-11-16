import SectionHeading from '../common/SectionHeading.jsx'

const AUDIENCES = [
  {
    title: 'For citizens',
    points: [
      'Report an issue in under a minute',
      'No need to work out which office to contact',
      'See exactly where your complaint stands',
    ],
  },
  {
    title: 'For authorities',
    points: [
      'Receive reports that are already classified and located',
      'Prioritise work using map-based hotspots',
      'Show the public what has been resolved',
    ],
  },
]

export default function AboutSection() {
  return (
    <section id="about" className="scroll-mt-16 bg-slate-50 py-20 sm:py-24">
      <div className="mx-auto grid max-w-7xl gap-12 px-4 sm:px-6 lg:grid-cols-2 lg:items-center lg:px-8">
        <div>
          <SectionHeading
            align="left"
            eyebrow="About CivicConnect"
            title="Closing the gap between citizens and city services"
          />
          <div className="mt-6 space-y-4 text-base leading-7 text-slate-600">
            <p>
              Civic issues often go unreported or get lost between departments. Citizens rarely know where to complain
              or whether anyone acted, and authorities receive vague, duplicate or misrouted reports.
            </p>
            <p>
              CivicConnect brings the whole process onto one transparent platform. Photo evidence, precise locations
              and AI classification mean every report is actionable from the start.
            </p>
          </div>
        </div>

        <div className="grid gap-6 sm:grid-cols-2">
          {AUDIENCES.map((audience) => (
            <div key={audience.title} className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
              <h3 className="text-base font-bold text-slate-900">{audience.title}</h3>
              <ul className="mt-4 space-y-3">
                {audience.points.map((point) => (
                  <li key={point} className="flex gap-3 text-sm leading-6 text-slate-600">
                    <svg viewBox="0 0 20 20" className="mt-0.5 size-5 shrink-0 text-blue-600" fill="currentColor" aria-hidden="true">
                      <path
                        fillRule="evenodd"
                        d="M10 18a8 8 0 1 0 0-16 8 8 0 0 0 0 16Zm3.7-9.3a1 1 0 0 0-1.4-1.4L9 10.58 7.7 9.3a1 1 0 0 0-1.4 1.4l2 2a1 1 0 0 0 1.4 0l4-4Z"
                        clipRule="evenodd"
                      />
                    </svg>
                    {point}
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
