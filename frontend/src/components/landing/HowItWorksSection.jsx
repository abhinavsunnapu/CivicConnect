import SectionHeading from '../common/SectionHeading.jsx'

const STEPS = [
  {
    title: 'Snap & report',
    description: 'Take a photo of the issue, add a short description and drop a pin on the map.',
  },
  {
    title: 'AI classifies it',
    description: 'The image is analysed to detect the issue type and how confident the model is.',
  },
  {
    title: 'Routed to the right team',
    description: 'The report reaches the responsible department, ready to be verified and assigned.',
  },
  {
    title: 'Track until resolved',
    description: 'Follow each status update, from Reported to Resolved, in one place.',
  },
]

export default function HowItWorksSection() {
  return (
    <section id="how-it-works" className="scroll-mt-16 bg-white py-20 sm:py-24">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <SectionHeading
          eyebrow="How it works"
          title="From report to resolution in four steps"
          description="A simple flow for citizens and a clear path for authorities."
        />

        <div className="relative mt-16">
          <div
            className="absolute left-[12.5%] right-[12.5%] top-6 hidden h-px bg-linear-to-r from-blue-200 via-blue-300 to-blue-200 lg:block"
            aria-hidden="true"
          />
          <ol className="relative grid gap-10 sm:grid-cols-2 lg:grid-cols-4 lg:gap-8">
          {STEPS.map((step, index) => (
            <li key={step.title} className="relative text-center">
              <span className="relative mx-auto flex size-12 items-center justify-center rounded-full bg-blue-600 text-base font-bold text-white shadow-md shadow-blue-600/30 ring-8 ring-white">
                {index + 1}
              </span>
              <h3 className="mt-6 text-base font-bold text-slate-900">{step.title}</h3>
              <p className="mx-auto mt-2 max-w-xs text-sm leading-6 text-slate-600">{step.description}</p>
            </li>
          ))}
          </ol>
        </div>
      </div>
    </section>
  )
}
