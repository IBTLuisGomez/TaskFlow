package taskflow.notification;

public interface NotificationService {

    void notifyTaskCreated(Long taskId, String title);
}
