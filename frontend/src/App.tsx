import { useCallback, useEffect, useState } from 'react'
import { tasksApi, type Task, type TaskInput } from './api/tasks'
import { TaskForm } from './components/TaskForm'
import { TaskItem } from './components/TaskItem'

type Filter = 'all' | 'pending' | 'done'

const FILTERS: Record<Filter, { label: string; value: boolean | undefined }> = {
  all: { label: 'Todas', value: undefined },
  pending: { label: 'Pendientes', value: false },
  done: { label: 'Completadas', value: true },
}

export default function App() {
  const [tasks, setTasks] = useState<Task[]>([])
  const [filter, setFilter] = useState<Filter>('all')
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    try {
      setTasks(await tasksApi.list(FILTERS[filter].value))
      setError(null)
    } catch (err) {
      setError((err as Error).message)
    }
  }, [filter])

  useEffect(() => {
    load()
  }, [load])

  async function handleCreate(input: TaskInput) {
    await tasksApi.create(input)
    await load()
  }

  async function run(action: () => Promise<unknown>) {
    try {
      await action()
    } catch (err) {
      setError((err as Error).message)
    }
    await load()
  }

  return (
    <main>
      <h1>TaskFlow</h1>
      <TaskForm onCreate={handleCreate} />
      <nav className="filters">
        {(Object.keys(FILTERS) as Filter[]).map((f) => (
          <button key={f} className={f === filter ? 'active' : ''} onClick={() => setFilter(f)}>
            {FILTERS[f].label}
          </button>
        ))}
      </nav>
      {error && <p className="error">{error}</p>}
      <ul>
        {tasks.map((t) => (
          <TaskItem
            key={t.id}
            task={t}
            onComplete={(id) => run(() => tasksApi.complete(id))}
            onDelete={(id) => run(() => tasksApi.remove(id))}
          />
        ))}
      </ul>
      {tasks.length === 0 && !error && <p className="empty">No hay tareas.</p>}
    </main>
  )
}
