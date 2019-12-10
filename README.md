# E-Lesson (functional DDD showcase)

## Exercise #5: Be Explicit!

This exercise will show you how to use types to enforce as much constraints as possible.

### Exercise description

- Go to [CurrentLesson.kt](src/main/kotlin/com/krzykrucz/elesson/currentlesson/domain/CurrentLesson.kt)
- Figure out how to use data types to enforce more constraints in fragments labeled as TODO
- Create smart constructor for `LessonHourNumber` 

#### Tips
1. When in doubt - ask domain expert.
2. Ask yourself if given data type isn't too broad. For example, can teacher's name be empty?
3. Use `com.github.VirtusLab.base-types-kt:refined-types` library for help
