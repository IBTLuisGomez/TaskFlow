export interface Task {
  id: number
  title: string
  description: string | null
  completed: boolean
  createdAt: string
}

export interface TaskInput {
  title: string
  description?: string
}

export class ApiError extends Error {
  fieldErrors: Record<string, string>

  constructor(message: string, fieldErrors: Record<string, string> = {}) {
    super(message)
    this.fieldErrors = fieldErrors
  }
}

const BASE = '/api/tasks'

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, {
    ...init,
    headers: { 'Content-Type': 'application/json', ...init?.headers },
  })
  if (!res.ok) {
    // El backend responde siempre con ProblemDetail (RFC 9457)
    const problem = await res.json().catch(() => ({}))
    throw new ApiError(problem.detail ?? problem.title ?? `Error ${res.status}`, problem.errors)
  }
  return res.status === 204 ? (undefined as T) : res.json()
}

export const tasksApi = {
  list: (completed?: boolean) =>
    request<Task[]>(completed === undefined ? BASE : `${BASE}?completed=${completed}`),
  create: (input: TaskInput) =>
    request<Task>(BASE, { method: 'POST', body: JSON.stringify(input) }),
  complete: (id: number) => request<Task>(`${BASE}/${id}/complete`, { method: 'PATCH' }),
  remove: (id: number) => request<void>(`${BASE}/${id}`, { method: 'DELETE' }),
}
