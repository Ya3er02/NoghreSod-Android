# Contributing to NoghreSod Android

Thank you for your interest in contributing to the NoghreSod jewelry e-commerce app! We appreciate your efforts to make this project better.

## Table of Contents

- [Development Setup](#development-setup)
- [Code Style](#code-style)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)
- [Testing Requirements](#testing-requirements)
- [Code of Conduct](#code-of-conduct)

---

## Development Setup

### Prerequisites

- **Android Studio:** Hedgehog (2023.1.1) or later
- **JDK:** Version 17 or later
- **Android SDK:** API 34 (Android 14)
- **Gradle:** 8.0+

### Initial Setup

1. **Fork and Clone**
   ```bash
   git clone https://github.com/YOUR_USERNAME/NoghreSod-Android.git
   cd NoghreSod-Android
   ```

2. **Configure Local Properties**
   ```bash
   cp local.properties.example local.properties
   # Edit local.properties with your API credentials
   ```

3. **Sync Gradle**
   ```bash
   ./gradlew clean build
   ```

4. **Verify Setup**
   ```bash
   ./gradlew test
   ```

---

## Code Style

### Kotlin Guidelines

We follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) and [Google's Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).

#### Key Rules

- **Naming:** Use meaningful, descriptive names
  ```kotlin
  // ✅ Good
  val isProductAvailable = true
  fun fetchProductDetails(productId: String)
  
  // ❌ Bad
  val a = true
  fun getProduct(p: String)
  ```

- **Functions:** Keep them small and focused
  ```kotlin
  // ✅ Good - Single responsibility
  fun calculateDiscount(price: Double): Double {
      return price * 0.1
  }
  
  // ❌ Bad - Does multiple things
  fun processOrder(price: Double, tax: Double): Pair<Double, String> {
      val discount = price * 0.1
      val total = price + tax - discount
      return Pair(total, formatCurrency(total))
  }
  ```

- **Documentation:** Write KDoc for public APIs
  ```kotlin
  /**
   * Fetches products from the remote API.
   *
   * @param category Optional category filter
   * @param sortBy Sort order ("price", "rating", "newest")
   * @return Flow of Result containing product list
   *
   * Example:
   * ```
   * getProductsUseCase(params).collect { result ->
   *     result.onSuccess { products -> updateUI(products) }
   * }
   * ```
   */
  class GetProductsUseCase : UseCase<GetProductsParams, List<Product>>(ioDispatcher) {
      // ...
  }
  ```

- **Class Organization:** Follow standard structure
  ```kotlin
  class UserViewModel @Inject constructor(
      private val getUserUseCase: GetUserUseCase
  ) : ViewModel() {
      // 1. Properties
      private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
      val uiState = _uiState.asStateFlow()

      // 2. Initialization
      init { loadUser() }

      // 3. Public methods
      fun retry() { loadUser() }

      // 4. Private methods
      private fun loadUser() { /* ... */ }
  }
  ```

### Format Configuration

Use the provided `.editorconfig`:

```ini
[*.kt]
indent_size = 4
max_line_length = 120
```

### IDE Configuration

In Android Studio:
1. File → Settings → Editor → Code Style → Kotlin
2. Import scheme from: [Google Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
3. Enable "Hard wrap at": 120 characters

---

## Commit Messages

Use [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Commit Types

- **feat:** New feature
- **fix:** Bug fix
- **docs:** Documentation changes
- **style:** Code style changes (formatting, missing semicolons, etc.)
- **refactor:** Code refactoring without behavior change
- **perf:** Performance improvements
- **test:** Adding or updating tests
- **build:** Build system or dependency changes
- **ci:** CI/CD configuration changes
- **chore:** Maintenance tasks

### Examples

```bash
# ✅ Good
git commit -m "feat(cart): add product quantity adjustment"
git commit -m "fix(payment): handle network timeout during checkout"
git commit -m "docs(readme): add setup instructions"
git commit -m "refactor(domain): simplify Result sealed class"
git commit -m "perf(image): optimize image caching strategy"

# ❌ Bad
git commit -m "fixed stuff"
git commit -m "Updated code"
git commit -m "Changes"
```

### Commit Body Template

```
feat(feature-name): brief description

Detailed explanation of what changed and why.
Include any relevant context or motivation.

Fixes #issue_number
Breaking change: No
```

---

## Pull Request Process

### Before Creating PR

1. **Create Feature Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes** following code style guidelines

3. **Local Testing**
   ```bash
   ./gradlew clean build
   ./gradlew test
   ./gradlew lint
   ```

4. **Commit Changes** with conventional messages

5. **Push to Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

### Creating the PR

1. Go to GitHub and create a Pull Request
2. Use the PR template (auto-populated)
3. Fill all sections:
   - **Description:** Clear overview of changes
   - **Type:** Select one or more
   - **Related Issues:** Link any related issues
   - **Changes:** Bullet list of modifications
   - **Screenshots:** Visual changes if applicable

4. Complete the **Checklist**

### PR Requirements

Before approval, all of the following must be met:

- ✅ All automated checks pass (CI/CD)
- ✅ Code review approved
- ✅ Tests included and passing
- ✅ Documentation updated
- ✅ No merge conflicts
- ✅ PR description is clear
- ✅ Commits follow conventional format

---

## Testing Requirements

### Unit Tests

Write unit tests for all business logic:

```kotlin
@Test
fun `Result Success should return data`() {
    val result = Result.Success("test")
    assertTrue(result.isSuccess)
    assertEquals("test", result.getOrNull())
}

@Test
`fun validateEmail returns Valid for correct email`() {
    val result = InputValidators.validateEmail("test@example.com")
    assertEquals(ValidationResult.Valid, result)
}
```

### Integration Tests

Test repository and data layer interactions:

```kotlin
@Test
fun `getUserProfile fetches from database when cached`() = runTest {
    // Setup
    val userId = "123"
    val user = User(id = userId, name = "Test")
    
    // Act
    repository.cacheUser(user)
    val result = repository.getUserProfile(userId)
    
    // Assert
    assertTrue(result.isSuccess)
    assertEquals(user, result.getOrNull())
}
```

### Screenshot Tests

For UI changes, add Paparazzi screenshot tests:

```kotlin
@Test
fun errorView_noInternet() {
    paparazzi.snapshot {
        ErrorView(
            error = NetworkException.NoInternetException(),
            onRetry = {}
        )
    }
}
```

### Test Coverage

Maintain minimum 80% code coverage:

```bash
./gradlew jacocoTestReport
# View report: app/build/reports/jacoco/index.html
```

---

## Code of Conduct

### Be Respectful

- Treat all contributors with respect
- Use inclusive language
- Be open to feedback and different perspectives

### Be Professional

- Avoid offensive or discriminatory language
- Keep discussions focused on the project
- Maintain a positive and constructive tone

### Be Collaborative

- Help other contributors
- Share knowledge and expertise
- Acknowledge contributions from others

---

## Getting Help

- **Questions:** Check existing issues and documentation first
- **Setup Issues:** See [IMPLEMENTATION_QUICK_START.md](IMPLEMENTATION_QUICK_START.md)
- **Code Questions:** Ask in relevant PR or issue
- **Architecture Questions:** Refer to [IMPROVEMENTS_PART_1.md](IMPROVEMENTS_PART_1.md)

---

## Recognition

Contributors are recognized in:
- GitHub contributor graph
- Release notes
- Project documentation

---

**Thank you for contributing to NoghreSod! 🙏**
