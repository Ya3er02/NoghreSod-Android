# Performance Optimization Guide

## Compose Performance

### Minimize Recompositions

```kotlin
// Use remember for stable values
val screenModifier = remember {
    Modifier.fillMaxSize().padding(16.dp)
}
```

### Use key for List Items

```kotlin
LazyColumn {
    items(
        items = products,
        key = { it.id }  // Prevents unnecessary recompositions
    ) { product ->
        ProductCard(product)
    }
}
```

## Network Optimization

### Image Caching

Coil automatically handles caching. Ensure:
- Memory cache enabled
- Disk cache enabled
- Appropriate image sizes

## Database Optimization

### Use Indexes

```kotlin
@Entity(indices = [Index(value = ["userId"])])
data class Order(
    @PrimaryKey val id: String,
    val userId: String
)
```

## Profiling

1. Connect device
2. **View** → **Tool Windows** → **Profiler**
3. Monitor CPU, Memory, and Network

## Build Performance

```bash
# Enable Gradle parallel builds and caching
# Already configured in gradle.properties
```
