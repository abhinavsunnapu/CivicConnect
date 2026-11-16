import AboutSection from '../components/landing/AboutSection.jsx'
import CallToActionSection from '../components/landing/CallToActionSection.jsx'
import FeaturesSection from '../components/landing/FeaturesSection.jsx'
import HeroSection from '../components/landing/HeroSection.jsx'
import HowItWorksSection from '../components/landing/HowItWorksSection.jsx'

export default function HomePage() {
  return (
    <>
      <HeroSection />
      <FeaturesSection />
      <HowItWorksSection />
      <AboutSection />
      <CallToActionSection />
    </>
  )
}
