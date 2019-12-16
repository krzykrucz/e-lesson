# E-Lesson (functional DDD showcase)

## Exercise #11: Port&Adapters 

It is recommended to push IO-based operations to the edge. Port and adapters architecture is a perfect way to achieve it.

In this exercise we will be implementing some Primary and Secondary adapters.

### Exercise description

#### Persistence
- Take a look at [Ports.kt](src/main/kotlin/com/krzykrucz/elesson/currentlesson/adapters/Ports.kt) which defines `PersistStartedLesson` port
- Go to [PersistenceAdapter.kt](src/main/kotlin/com/krzykrucz/elesson/currentlesson/adapters/PersistenceAdapter.kt)
- Implement required logic using [Database.kt](src/main/kotlin/com/krzykrucz/elesson/currentlesson/infrastructure/Database.kt) from `infrastructure` package
- Your implementation should be a `Spring Bean`

#### Endpoint

- Go to [EnpointAdapter.kt](src/main/kotlin/com/krzykrucz/elesson/currentlesson/adapters/EnpointAdapter.kt)
- Implement `startLessonRoute` so that it is possible to start lesson via REST endpoint
- Make `Acceptance` tests for adapters pass

#### Tips

- Check adapters tests to ensure required logic
- Do not mix adapters logic with domain workflows
- `Spring` framework is just an example of possible implementation
