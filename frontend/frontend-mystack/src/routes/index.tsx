import { createFileRoute } from '@tanstack/react-router'
import { HomePage } from '@/components/auth/home-page'

export const Route = createFileRoute('/')({
  component: App,
})

function App() {
  return (
    <div className={'flex h-screen w-screen items-center justify-center'}>
    <HomePage />
  </div>
  )
}
