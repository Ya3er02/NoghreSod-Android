# Contributing to NoghreSod Android

First off, thank you for considering contributing to the NoghreSod Android application! It's people like you that make this application such a great tool.

## Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* **Use a clear and descriptive title**
* **Describe the exact steps which reproduce the problem**
* **Provide specific examples to demonstrate the steps**
* **Describe the behavior you observed after following the steps**
* **Explain which behavior you expected to see instead and why**
* **Include screenshots and animated GIFs if possible**
* **Include your environment details** (device, Android version, app version)

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **Use a clear and descriptive title**
* **Provide a step-by-step description of the suggested enhancement**
* **Provide specific examples to demonstrate the steps**
* **Describe the current behavior and expected behavior**
* **Explain why this enhancement would be useful**

### Pull Requests

* Fill in the required template
* Follow the Kotlin style guide
* Include appropriate test cases
* Update documentation as needed
* End all files with a newline

## Development Setup

1. Fork and clone the repository
2. Follow [SETUP.md](SETUP.md) for development environment setup
3. Create a new branch for your feature: `git checkout -b feature/my-feature`
4. Make your changes
5. Test your changes thoroughly
6. Commit with clear messages: `git commit -m "[FEATURE] Add my feature"`
7. Push to your fork: `git push origin feature/my-feature`
8. Open a Pull Request

## Commit Messages

Use clear and descriptive commit messages:

```
[TYPE] Brief description

Detailed explanation of the change (optional)

Types: FEATURE, FIX, REFACTOR, DOCS, STYLE, TEST
```

Examples:
- `[FEATURE] Add product search functionality`
- `[FIX] Fix login validation bug`
- `[DOCS] Update README with API configuration`

## Code Style

### Kotlin

* Follow [Kotlin conventions](https://kotlinlang.org/docs/coding-conventions.html)
* Use meaningful variable names
* Keep functions small and focused
* Use data classes for DTOs
* Prefer immutable variables (val over var)

### Composables

* Use `@Composable` annotation
* Keep composables focused on single responsibility
* Use modifier as parameter (last position)
* Provide preview with `@Preview`

### Comments

* Add comments for complex logic
* Use KDoc for public APIs
* Avoid obvious comments

## Testing

Please include tests for your changes:

```kotlin
class MyViewModelTest {
    @Test
    fun testLoginSuccess() {
        // Arrange
        // Act
        // Assert
    }
}
```

Run tests before submitting:

```bash
./gradlew test
```

## Documentation

Update documentation if your change affects:

- User-facing features
- API usage
- Architecture
- Setup process

## Performance

- Minimize recompositions in Compose
- Use `remember` for expensive computations
- Profile with Android Profiler before submitting
- Avoid blocking operations on main thread

## Review Process

When you submit a pull request:

1. One or more maintainers will review your code
2. We may suggest changes or improvements
3. Tests must pass before merging
4. Code must follow style guidelines
5. Documentation must be updated

## Additional Notes

### Issue Labels

* `bug` - Something isn't working
* `enhancement` - New feature or request
* `documentation` - Improvements or additions to documentation
* `good first issue` - Good for newcomers
* `help wanted` - Extra attention is needed

### Project Structure

- Respect the existing architecture
- Don't move files without discussion
- Keep dependencies minimal
- Test before submitting

## Questions?

Feel free to open an issue for questions or discussions!

Thank you for contributing! 🎉
