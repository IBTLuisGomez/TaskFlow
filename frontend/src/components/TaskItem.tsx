import type { Task } from '../api/tasks'

interface Props {
  task: Task
  onComplete: (id: number) => void
  onDelete: (id: number) => void
}

export function TaskItem({ task, onComplete, onDelete }: Props) {
  return (
    <li className={`card item ${task.completed ? 'done' : ''}`}>
      <div>
        <strong>{task.title}</strong>
        {task.description && <p>{task.description}</p>}
        <small>{new Date(task.createdAt).toLocaleString()}</small>
      </div>
      <div className="actions">
        {!task.completed && <button onClick={() => onComplete(task.id)}>Completar</button>}
        <button className="danger" onClick={() => onDelete(task.id)}>Eliminar</button>
      </div>
    </li>
  )
}
