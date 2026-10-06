import { useState, type FormEvent } from 'react'
import { ApiError, type TaskInput } from '../api/tasks'

interface Props {
  onCreate: (input: TaskInput) => Promise<void>
}

export function TaskForm({ onCreate }: Props) {
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [saving, setSaving] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setSaving(true)
    setErrors({})
    try {
      await onCreate({ title, description: description || undefined })
      setTitle('')
      setDescription('')
    } catch (err) {
      setErrors(err instanceof ApiError ? { ...err.fieldErrors, _: err.message } : { _: 'Error inesperado' })
    } finally {
      setSaving(false)
    }
  }

  const hasFieldErrors = !!(errors.title || errors.description)

  return (
    <form className="card form" onSubmit={handleSubmit}>
      <input
        placeholder="Título"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        aria-invalid={!!errors.title}
      />
      {errors.title && <small className="error">{errors.title}</small>}
      <textarea
        placeholder="Descripción (opcional)"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      {errors.description && <small className="error">{errors.description}</small>}
      {errors._ && !hasFieldErrors && <small className="error">{errors._}</small>}
      <button disabled={saving}>{saving ? 'Guardando...' : 'Añadir tarea'}</button>
    </form>
  )
}
