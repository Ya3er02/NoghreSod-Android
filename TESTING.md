# Testing Guide

## Test Types

### Unit Tests

Test individual functions/classes in isolation.

**Location**: `app/src/test/kotlin/`

**Example**:
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

**Run**:
```bash
./gradlew test
```

### Instrumented Tests

Test on actual device or emulator.

**Location**: `app/src/androidTest/kotlin/`

**Example**:
```kotlin
class MyScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testButtonClick() {
        // UI test
    }
}
```

**Run**:
```bash
./gradlew connectedAndroidTest
```

## Testing Tools

- **JUnit4**: Unit testing framework
- **MockK**: Mocking library
- **Truth**: Assertion library
- **Compose Test**: Compose UI testing

## Writing Tests

### Unit Test Template

```kotlin
class FeatureTest {
    @Before
    fun setup() {
        // Initialize test dependencies
    }

    @Test
    fun testSuccessCase() {
        // Arrange: Set up test data
        val input = "test"
        
        // Act: Execute code being tested
        val result = classUnderTest.method(input)
        
        // Assert: Verify results
        assertEquals(expected, result)
    }

    @After
    fun teardown() {
        // Clean up
    }
}
```

### Mocking with MockK

```kotlin
@Test
fun testWithMock() {
    // Create mock
    val repository = mockk<MyRepository>()
    
    // Set expectations
    coEvery { repository.getData() } returns listOf("item1", "item2")
    
    // Use in test
    val result = repository.getData()
    
    // Verify
    coVerify { repository.getData() }
}
```

## Test Coverage

### Checking Coverage

```bash
./gradlew jacocoTestReport
```

Report location: `build/reports/jacoco/index.html`

### Coverage Targets

- ViewModels: 80%+
- Repositories: 70%+
- Utilities: 90%+
- Data: 60%+

## Running Tests

### All Tests
```bash
./gradlew test
```

### Specific Test Class
```bash
./gradlew test --tests "com.noghre.sod.presentation.viewmodel.AuthViewModelTest"
```

### Specific Test Method
```bash
./gradlew test --tests "com.noghre.sod.presentation.viewmodel.AuthViewModelTest.testLoginSuccess"
```

### UI Tests
```bash
./gradlew connectedAndroidTest
```

## Test Best Practices

1. **Test Naming**: Describe what is tested
   ```kotlin
   fun testLoginWithValidCredentialsSucceeds()
   fun testSearchWithEmptyQueryReturnsError()
   ```

2. **Arrange-Act-Assert**: Clear test structure

3. **One Assertion**: Focus on single behavior

4. **Mock External Dependencies**: Use mocks for repos, APIs

5. **Use Fixtures**: Create reusable test data

6. **Test Error Cases**: Don't just test happy path

## Continuous Integration

Tests run automatically on:
- Push to main branch
- Pull requests
- Scheduled nightly builds

**Configuration**: `.github/workflows/android-ci.yml`
