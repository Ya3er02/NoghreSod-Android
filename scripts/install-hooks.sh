#!/bin/bash
#
# Git Hooks Installation Script
# Installs pre-commit and pre-push hooks for code quality
#

set -e

echo "Installing Git Hooks for NoghreSod..."

# Create hooks directory
mkdir -p .git/hooks

# ============== Pre-commit Hook ==============
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash
# Pre-commit hook: Check code formatting and quality

echo "Running pre-commit checks..."

# Check for Kotlin files
if git diff --cached --name-only | grep -E '\.kt$' > /dev/null; then
    echo "  Running Kotlin Lint..."
    ./gradlew lintDebug --quiet || {
        echo "  ❌ Lint checks failed. Fix issues before committing."
        exit 1
    }
    echo "  ✓ Lint checks passed"
fi

# Check for secrets
echo "  Scanning for secrets..."
if git diff --cached | grep -E '(password|api_key|secret|token)' -i; then
    echo "  ❌ Potential secret found in commit. Please remove before committing."
    exit 1
fi
echo "  ✓ No secrets detected"

# Check commit message
echo "  Validating commit message format..."
COMMIT_MSG=$(cat "$1" 2>/dev/null || echo "")
if [[ ! $COMMIT_MSG =~ ^(feat|fix|docs|style|refactor|perf|test|chore|ci)(\(.*\))?:\  ]]; then
    echo "  ❌ Invalid commit message format"
    echo "  Use: <type>(<scope>): <subject>"
    echo "  Types: feat, fix, docs, style, refactor, perf, test, chore, ci"
    exit 1
fi
echo "  ✓ Commit message format valid"

echo "✓ Pre-commit checks passed"
exit 0
EOF

chmod +x .git/hooks/pre-commit
echo "✓ Pre-commit hook installed"

# ============== Pre-push Hook ==============
cat > .git/hooks/pre-push << 'EOF'
#!/bin/bash
# Pre-push hook: Run tests before pushing

echo "Running pre-push checks..."

# Get the branch name
BRANCH=$(git rev-parse --abbrev-ref HEAD)

if [ "$BRANCH" = "main" ] || [ "$BRANCH" = "develop" ]; then
    echo "  Running unit tests on branch: $BRANCH"
    
    ./gradlew testDebugUnitTest --quiet || {
        echo "  ❌ Unit tests failed. Fix before pushing."
        exit 1
    }
    echo "  ✓ Unit tests passed"
    
    echo "  Running code analysis..."
    ./gradlew detekt --quiet || {
        echo "  ⚠ Code quality issues found. Consider fixing before push."
    }
    echo "  ✓ Code analysis complete"
else
    echo "  Skipping tests for feature branch: $BRANCH"
fi

echo "✓ Pre-push checks passed"
exit 0
EOF

chmod +x .git/hooks/pre-push
echo "✓ Pre-push hook installed"

# ============== Commit Message Hook ==============
cat > .git/hooks/commit-msg << 'EOF'
#!/bin/bash
# Commit message validation

COMMIT_MSG_FILE="$1"
COMMIT_MSG=$(cat "$COMMIT_MSG_FILE")

# Pattern: <type>(<scope>): <subject>
if ! echo "$COMMIT_MSG" | grep -qE '^(feat|fix|docs|style|refactor|perf|test|chore|ci)(\(.*\))?:\s.{10,}'; then
    echo "❌ Invalid commit message format"
    echo "Expected format: <type>(<scope>): <subject>"
    echo "Example: feat(products): Add product filtering"
    echo ""
    echo "Types:"
    echo "  feat     - New feature"
    echo "  fix      - Bug fix"
    echo "  docs     - Documentation changes"
    echo "  style    - Code style changes"
    echo "  refactor - Code refactoring"
    echo "  perf     - Performance improvements"
    echo "  test     - Tests"
    echo "  chore    - Build, dependencies"
    echo "  ci       - CI/CD changes"
    exit 1
fi

exit 0
EOF

chmod +x .git/hooks/commit-msg
echo "✓ Commit message hook installed"

# ============== Post-merge Hook ==============
cat > .git/hooks/post-merge << 'EOF'
#!/bin/bash
# Post-merge hook: Update dependencies if gradle files changed

echo "Post-merge hook running..."

if git diff-tree -r --name-only --no-commit-id ORIG_HEAD HEAD | grep -E '(build\.gradle|settings\.gradle|libs\.versions\.toml)' > /dev/null; then
    echo "Gradle files changed. Syncing project..."
    ./gradlew --refresh-dependencies --quiet || true
    echo "✓ Project synced"
fi

exit 0
EOF

chmod +x .git/hooks/post-merge
echo "✓ Post-merge hook installed"

echo ""
echo "========================================"
echo "✓ All Git hooks installed successfully!"
echo "========================================"
echo ""
echo "Hooks configured:"
echo "  • pre-commit   - Code quality checks"
echo "  • pre-push     - Unit tests validation"
echo "  • commit-msg   - Commit message validation"
echo "  • post-merge   - Dependency sync"
echo ""
echo "To uninstall: rm -rf .git/hooks"
echo ""
