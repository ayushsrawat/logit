import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouterProvider } from 'react-router-dom'
import { router } from './router'
import { SearchProvider } from './context/SearchContext'
import './index.css'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <SearchProvider>
      <RouterProvider router={router} />
    </SearchProvider>
  </StrictMode>,
)
