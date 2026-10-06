package taskflow.task;

public record TaskCreatedEvent(Long taskId, String title) {
}
