import { useEffect } from 'react'
import { useLocation } from 'react-router'

/**
 * Scrolls to the element matching the URL hash (e.g. "/#features") after
 * each navigation, or to the top of the page when there is no hash.
 */
export default function useScrollToHash() {
  const { pathname, hash, key } = useLocation()

  useEffect(() => {
    if (!hash) {
      window.scrollTo({ top: 0 })
      return
    }
    const target = document.getElementById(decodeURIComponent(hash.slice(1)))
    target?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }, [pathname, hash, key])
}
