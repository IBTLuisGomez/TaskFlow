package taskflow.task;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("La tarea " + id + " no existe");
    }
}