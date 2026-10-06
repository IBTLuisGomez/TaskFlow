package taskflow.task;

import taskflow.task.dto.TaskRequest;
import taskflow.task.dto.TaskResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) { // inyección por constructor
        this.repository = repository;
    }

    public List<TaskResponse> findAll(Boolean completed) {
        List<Task> tasks = (completed == null)
                ? repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                : repository.findByCompletedOrderByCreatedAtDesc(completed);
        return tasks.stream().map(TaskResponse::from).toList();
    }

    public TaskResponse findById(Long id) {
        return TaskResponse.from(getTask(id));
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task saved = repository.save(new Task(request.title(), request.description()));
        return TaskResponse.from(saved);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getTask(id);
        task.update(request.title(), request.description());
        return TaskResponse.from(task); // sin save(): dirty checking
    }

    @Transactional
    public TaskResponse complete(Long id) {
        Task task = getTask(id);
        task.complete();
        return TaskResponse.from(task);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private Task getTask(Long id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }
}