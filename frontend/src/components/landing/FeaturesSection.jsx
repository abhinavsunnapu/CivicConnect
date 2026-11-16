import SectionHeading from '../common/SectionHeading.jsx'

const FEATURES = [
  {
    title: 'AI issue detection',
    description:
      'Upload a photo and our YOLOv8-based model identifies the issue, whether it is a pothole, garbage or a damaged streetlight, so reports are classified correctly from the start.',
    points: ['Automatic categorisation', 'Confidence scoring', 'Less manual sorting'],
    icon: (
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M9 3v2m6-2v2M9 19v2m6-2v2M3 9h2m-2 6h2m14-6h2m-2 6h2M7 5h10a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2Zm2 4h6v6H9V9Z"
      />
    ),
  },
  {
    title: 'GPS location tracking',
    description:
      'Every report carries a precise location shown on an interactive OpenStreetMap view, so crews know exactly where to go and hotspots across the city are easy to see.',
    points: ['Pin-point accuracy', 'Interactive city map', 'Hotspot visibility'],
    icon: (
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M12 21s-7-6.4-7-11.5A7 7 0 0 1 19 9.5C19 14.6 12 21 12 21Zm0-9a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5Z"
      />
    ),
  },
  {
    title: 'Complaint management',
    description:
      'Authorities get a clear workflow to verify, assign and resolve complaints, and citizens see every status change from the moment they report to the moment it is fixed.',
    points: ['Status lifecycle tracking', 'Department assignment', 'Citizen notifications'],
    icon: (
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2m-6 9 2 2 4-4"
      />
    ),
  },
]

export default function FeaturesSection() {
  return (
    <section id="features" className="scroll-mt-16 bg-slate-50 py-20 sm:py-24">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <SectionHeading
          eyebrow="Features"
          title="Everything needed to fix your city, faster"
          description="CivicConnect combines computer vision, location data and a clear workflow to turn scattered complaints into issues that get resolved."
        />

        <div className="mt-14 grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {FEATURES.map((feature) => (
            <article
              key={feature.title}
              className="group rounded-2xl border border-slate-200 bg-white p-7 shadow-sm transition hover:-translate-y-1 hover:border-blue-200 hover:shadow-lg hover:shadow-blue-900/5"
            >
              <span className="flex size-12 items-center justify-center rounded-xl bg-blue-50 text-blue-600 transition group-hover:bg-blue-600 group-hover:text-white">
                <svg viewBox="0 0 24 24" className="size-6" fill="none" stroke="currentColor" strokeWidth="1.7" aria-hidden="true">
                  {feature.icon}
                </svg>
              </span>
              <h3 className="mt-6 text-lg font-bold text-slate-900">{feature.title}</h3>
              <p className="mt-3 text-sm leading-6 text-slate-600">{feature.description}</p>
              <ul className="mt-6 space-y-2 border-t border-slate-100 pt-5">
                {feature.points.map((point) => (
                  <li key={point} className="flex items-center gap-2 text-sm text-slate-600">
                    <span className="size-1.5 rounded-full bg-blue-500" />
                    {point}
                  </li>
                ))}
              </ul>
            </article>
          ))}
        </div>
      </div>
    </section>
  )
}
