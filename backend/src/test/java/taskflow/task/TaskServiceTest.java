package taskflow.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import taskflow.task.dto.TaskRequest;
import taskflow.task.dto.TaskResponse;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository repository;
    @Mock
    ApplicationEventPublisher events;
    @InjectMocks
    TaskService service;

    @Test
    void createSavesTaskAndPublishesEvent() {
        when(repository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = service.create(new TaskRequest("Comprar pan", "integral"));

        assertThat(response.title()).isEqualTo("Comprar pan");
        assertThat(response.completed()).isFalse();
        verify(events).publishEvent(any(TaskCreatedEvent.class));
    }

    @Test
    void completeMarksTaskAsCompleted() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Task("A", null)));

        assertThat(service.complete(1L).completed()).isTrue();
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(9L)).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(repository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(9L)).isInstanceOf(TaskNotFoundException.class);
        verify(repository, never()).deleteById(any());
    }
}
