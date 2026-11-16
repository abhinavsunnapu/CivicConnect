import { Outlet } from 'react-router'
import Footer from '../components/layout/Footer.jsx'
import Navbar from '../components/layout/Navbar.jsx'
import useScrollToHash from '../hooks/useScrollToHash.js'

export default function MainLayout() {
  useScrollToHash()

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}
