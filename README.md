# E-Lesson (functional DDD showcase)

## Exercise #2: Workflows, simple types

Well done, the specification is complete. Now it's time to implement some stuff. 

### Exercise description

Based on this Event Storming fragment:

![Started Lesson](images/ex1/started-lesson-ex1.jpg)

- Go to [CurrentLesson.kt](src/main/kotlin/com/krzykrucz/elesson/currentlesson/domain/CurrentLesson.kt)
- Create input & output types (`base-types-kt/refined-types` are here to help)
- Create properly typed `StartLesson` workflow
- Go to [StartLessonSteps.kt](src/test/kotlin/com/krzykrucz/elesson/currentlesson/domain/StartLessonSteps.kt)
- Implement `Given` and `When` steps
- Make test pass

#### Tips
1. When in doubt - ask domain expert.
2. The more types, the better!
3. Aim to be as close to business as possible.
4. Remember about `Product` and `Sum` types.
