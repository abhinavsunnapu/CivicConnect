import { createBrowserRouter } from 'react-router'
import MainLayout from '../layouts/MainLayout.jsx'
import ComingSoonPage from '../pages/ComingSoonPage.jsx'
import HomePage from '../pages/HomePage.jsx'
import NotFoundPage from '../pages/NotFoundPage.jsx'

const router = createBrowserRouter([
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <HomePage /> },
      {
        path: 'report',
        element: (
          <ComingSoonPage
            title="Issue reporting is on its way"
            description="You'll soon be able to report a civic issue with a photo and its exact location, then follow it all the way to resolution."
          />
        ),
      },
      {
        path: 'login',
        element: (
          <ComingSoonPage
            title="Sign in is coming soon"
            description="Citizen and authority accounts are being built. Check back shortly to sign in and manage your reports."
          />
        ),
      },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
])

export default router
