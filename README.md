# E-Lesson (functional DDD showcase)

## Exercise #3: Explicit dependencies

Start Lesson workflow is starting to shape up! In this task, we will focus on dependencies of it.

### Exercise description

Based on this Event Storming fragment:

![Started Lesson](images/ex1/started-lesson-ex1.jpg)

- Go to [CurrentLesson.kt](src/main/kotlin/com/krzykrucz/elesson/currentlesson/domain/CurrentLesson.kt)
- Complete `Check Schedule` and `Fetch Class Registry` workflows
- Include them in `StartLesson` workflow
- Go to [StartLessonSteps.kt](src/test/kotlin/com/krzykrucz/elesson/currentlesson/domain/StartLessonSteps.kt)
- Implement missing `Check Schedule` and `Fetch Class Registry` steps
- Make test pass

#### Tips
1. When in doubt - ask domain expert.
2. Dependencies should be explicit! Don't be afraid to include them in workflow function.
