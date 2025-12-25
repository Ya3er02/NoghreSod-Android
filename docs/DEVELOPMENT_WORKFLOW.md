# 🔐4 NoghreSod Android - Development Workflow

**Version:** 1.0.0  
**Last Updated:** 2025-12-25  
**Team:** NoghreSod Development Team

---

## Table of Contents

1. [Development Setup](#development-setup)
2. [Branch Management](#branch-management)
3. [Git Workflow](#git-workflow)
4. [Code Review Process](#code-review-process)
5. [Testing Requirements](#testing-requirements)
6. [Deployment Pipeline](#deployment-pipeline)
7. [Issue Tracking](#issue-tracking)
8. [Communication](#communication)

---

## Development Setup

### System Requirements

- **OS:** macOS, Linux, or Windows (WSL2)
- **JDK:** OpenJDK 17+
- **Android SDK:** API 28+ (min), API 34+ (target)
- **Android Studio:** Latest stable version (Hedgehog 2023.1.1+)
- **Git:** 2.40+
- **Gradle:** 8.0+ (included in project)

### Initial Setup

```bash
# 1. Clone repository
git clone https://github.com/Ya3er02/NoghreSod-Android.git
cd NoghreSod-Android

# 2. Create local properties file
cp local.properties.template local.properties

# Edit local.properties with your SDK path
# sdk.dir=/Users/yourname/Library/Android/sdk

# 3. Install dependencies
./gradlew dependencies

# 4. Run tests (optional)
./gradlew test

# 5. Build debug APK
./gradlew assembleDebug

# 6. Open in Android Studio
open -a "Android Studio" .
```

### IDE Configuration

**Android Studio Settings:**

1. **Editor > Code Style > Kotlin**
   - Line length: 100 (soft limit)
   - Import layout: Custom (import android.*, androidx.*, then others)

2. **Editor > Inspections**
   - Enable KTLint inspection
   - Set inspection profile to "NoghreSod"

3. **Build Tools > Gradle**
   - Gradle JVM: Java 17
   - Offline mode: Disabled

4. **Plugins to Install**
   - KTLint
   - Kotlin
   - Android Lint Bundle
   - Git Toolbox
   - OpenAPI (Swagger)

---

## Branch Management

### Branch Naming Convention

```
<type>/<scope>/<description>

Examples:
- feature/auth/biometric-login
- bugfix/security/certificate-pinning
- hotfix/payment/crash-on-checkout
- docs/api/endpoint-documentation
- refactor/data/room-migration
- test/ui/compose-testing
```

**Types:**
- `feature/` - New feature development
- `bugfix/` - Bug fixes (non-critical)
- `hotfix/` - Emergency fixes (production issues)
- `refactor/` - Code refactoring (no functional change)
- `test/` - Testing improvements
- `docs/` - Documentation updates
- `chore/` - Build config, dependencies

### Branch Lifecycle

```
main (production)
  |
  +-- release/v1.2.0 (release candidate)
  |      |
  |      +-- (hotfixes only)
  |
  +-- develop (staging/integration)
         |
         +-- feature/auth/biometric-login (from develop)
         +-- feature/payment/razorpay (from develop)
         +-- bugfix/ui/layout-crash (from develop)
```

### Protection Rules

**main branch:**
- ✅ Requires pull request review (2+ reviewers)
- ✅ Requires status checks to pass
- ✅ Requires branch to be up-to-date
- ❌ Direct commits not allowed
- ❌ Force push not allowed

**develop branch:**
- ✅ Requires pull request review (1+ reviewer)
- ✅ Requires CI checks
- ✅ Auto-delete head branches

---

## Git Workflow

### Feature Development

```bash
# 1. Update develop branch
git checkout develop
git pull origin develop

# 2. Create feature branch
git checkout -b feature/auth/biometric-login

# 3. Make changes and commit (atomic commits)
git add .
git commit -m "feat(auth): add biometric authentication manager

Implements fingerprint and face recognition with:
- Biometric API integration
- Fallback to PIN
- Session management

Related-To: JIRA-456"

# 4. Push to remote
git push origin feature/auth/biometric-login

# 5. Create pull request on GitHub
# (Use PR template)
```

### Commit Message Format

Follow **Conventional Commits** spec:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type:**
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation
- `style` - Code style (no functional change)
- `refactor` - Code restructuring
- `perf` - Performance improvement
- `test` - Testing
- `chore` - Build, dependencies, etc.

**Example:**

```
feat(payment): implement Razorpay integration

- Add Razorpay SDK initialization
- Create PaymentRepository for payment handling
- Implement payment success/failure callbacks
- Add error tracking for failed transactions

Breaking Change: Payment API updated
Closes: JIRA-123
Co-authored-by: Ali <ali@noghresod.ir>
```

### Rebase vs Merge

**Pull requests should be REBASED before merging:**

```bash
# Update from main/develop
git fetch origin
git rebase origin/develop

# If conflicts
git status  # See conflicting files
# Resolve conflicts in editor
git add .
git rebase --continue

# Force push to PR branch
git push origin feature/auth/biometric-login --force-with-lease
```

---

## Code Review Process

### Creating a Pull Request

1. **Push your branch**
   ```bash
   git push origin feature/auth/biometric-login
   ```

2. **Open PR on GitHub**
   - Use PR template (auto-populated)
   - Link related issues/tickets
   - Add description and screenshots if UI changes
   - Request reviewers

3. **PR Template Fields**
   ```markdown
   ## Description
   Clear description of changes
   
   ## Motivation
   Why this change?
   
   ## Type of Change
   - [ ] New feature
   - [ ] Bug fix
   - [ ] Breaking change
   
   ## Related Issues
   Closes #123
   
   ## Testing Done
   Manual testing steps
   
   ## Screenshots (if applicable)
   Before/after
   
   ## Checklist
   - [ ] Tests added/updated
   - [ ] Documentation updated
   - [ ] No breaking changes
   - [ ] Follows code style
   ```

### Review Checklist

**Reviewers check:**

- [ ] Code follows style guide (KTLint)
- [ ] Logic is correct and efficient
- [ ] Tests cover changes (unit + integration)
- [ ] No hard-coded values
- [ ] Error handling is appropriate
- [ ] No security vulnerabilities
- [ ] Documentation is accurate
- [ ] No unnecessary dependencies added
- [ ] Performance acceptable
- [ ] Accessibility considerations (if UI)

### Review Comments

**Comment Types:**

- ✅ **APPROVED** - Minor comments, code is good
- ❌ **REQUEST_CHANGES** - Issues must be fixed before merge
- 💬 **COMMENT** - Question or suggestion

### Addressing Feedback

```bash
# 1. Make changes
git add .
git commit -m "refactor(auth): address review comments

- Simplify token validation logic
- Add missing null checks
- Update documentation"

# 2. Push changes
git push origin feature/auth/biometric-login

# 3. Re-request review
# (GitHub auto-notifies on new commits)
```

---

## Testing Requirements

### Unit Tests

**Coverage target:** 80%+

```bash
# Run unit tests
./gradlew test

# Run with coverage report
./gradlew testDebugUnitTestCoverage
```

### Integration Tests

```bash
# Run on connected device/emulator
./gradlew connectedAndroidTest
```

### Lint Checks

```bash
# Run Android lint
./gradlew lint

# Run KTLint
./gradlew ktlintCheck

# Auto-fix KTLint issues
./gradlew ktlintFormat
```

### Manual Testing

**Before PR:**

1. **Build and run**
   ```bash
   ./gradlew installDebug
   ```

2. **Test on multiple API levels** (28, 30, 33, 34)

3. **Test on different device types**
   - Phone (small/medium/large screens)
   - Tablet
   - Foldable (if applicable)

4. **Test edge cases**
   - Network errors
   - Empty states
   - Rate limiting
   - Permission denials

---

## Deployment Pipeline

### CI/CD with GitHub Actions

```yaml
# .github/workflows/ci.yml
name: CI
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: ./gradlew ktlintCheck
      - run: ./gradlew test
      - run: ./gradlew lint
```

### Release Process

**Version Numbering:** Semantic Versioning (MAJOR.MINOR.PATCH)

```bash
# 1. Prepare release branch
git checkout -b release/v1.2.0 develop

# 2. Update version in build.gradle.kts
versionCode = 120  # MAJOR=1, MINOR=2, PATCH=0
versionName = "1.2.0"

# 3. Update CHANGELOG.md

# 4. Commit changes
git commit -m "chore: prepare release v1.2.0"

# 5. Create PR to main
# After approval and CI passes:

# 6. Merge to main
git checkout main
git merge --no-ff release/v1.2.0

# 7. Tag release
git tag -a v1.2.0 -m "Release version 1.2.0"

# 8. Merge back to develop
git checkout develop
git merge --no-ff release/v1.2.0

# 9. Push everything
git push origin main develop --tags

# 10. Build and upload to Play Store
./gradlew bundleRelease
# Upload bundle to Google Play Console
```

---

## Issue Tracking

### JIRA Integration

**Issue Types:**
- 🎯 **Task** - Regular work item
- 🐛 **Bug** - Defect or issue
- ✨ **Feature** - New functionality
- 📚 **Epic** - Large feature
- 🔄 **Subtask** - Part of task

**Link commits to JIRA:**

```bash
# Commit message includes JIRA ticket
git commit -m "feat(auth): implement biometric login

Implements ticket requirements:
- Support fingerprint recognition
- Support face recognition
- Fallback to PIN

JIRA-456"
```

**GitHub Integration:**
- Link PR to JIRA issue
- Mention in PR description: `Related-To: JIRA-456`
- Auto-transition on merge

---

## Communication

### Team Sync

- **Daily Standup:** 09:00 AM (15 min)
  - What done yesterday?
  - What doing today?
  - Any blockers?

- **Sprint Planning:** Bi-weekly Monday (1 hour)
  - Review backlog
  - Assign stories
  - Estimate effort

- **Code Review Sync:** Wednesday (30 min)
  - Discuss complex PRs
  - Align on standards
  - Pair programming setup

### Chat Channels

- **#android-dev** - General development discussion
- **#code-reviews** - PR notifications and discussions
- **#incidents** - Production issues and hotfixes
- **#general** - Team announcements

---

## Useful Commands

```bash
# Build and install
./gradlew installDebug

# Run specific test
./gradlew test --tests ProductViewModelTest

# Generate reports
./gradlew testDebugUnitTestCoverage

# Check dependencies
./gradlew dependencies

# Update gradle wrapper
./gradlew wrapper --gradle-version=8.2

# Clean build
./gradlew clean build

# Profile app
./gradlew profileDebugBuild

# Generate API documentation
./gradlew dokkaHtml
```

---

**Last Updated:** 2025-12-25  
**Maintainer:** Development Team Lead
