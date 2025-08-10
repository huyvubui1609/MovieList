# Movie List App

A modern Android application built with Kotlin that displays trending movies and allows users to search and view movie details. The app follows clean architecture principles and implements offline-first caching strategy for optimal user experience.

## 🏗️ Architecture Overview

This project follows **Clean Architecture** principles with clear separation of concerns across multiple layers:

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │     UI      │  │ ViewModels  │  │    Navigation       │  │
│  │  (Compose)  │  │             │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Models    │  │  Use Cases  │  │   Repository        │  │
│  │             │  │             │  │   Interfaces        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                               │
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Repository │  │   Network   │  │    Local Storage    │  │
│  │     Impl    │  │   (Retrofit)│  │      (Room)         │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Layer Responsibilities

- **Presentation Layer**: UI components, ViewModels, and navigation logic
- **Domain Layer**: Business logic, use cases, and domain models
- **Data Layer**: Data sources, repositories, and data mapping

## 📱 Features

- **Trending Movies**: Browse popular movies from TMDB API
- **Search**: Search for movies by title
- **Movie Details**: View detailed information about movies
- **Offline Support**: App works offline with cached data
- **Error Handling**: Graceful handling of network errors
- **Loading States**: Smooth loading indicators

## 🗂️ Project Structure

```
app/
├── src/main/java/com/example/movielist/
│   ├── data/                    # Data layer implementation
│   │   ├── local/              # Room database, DAOs, entities
│   │   ├── network/            # Retrofit API, DTOs
│   │   ├── repository/         # Repository implementations
│   │   └── mapper/             # Data mapping utilities
│   ├── domain/                 # Business logic layer
│   │   ├── model/              # Domain models
│   │   ├── repository/         # Repository interfaces
│   │   └── usecase/            # Business use cases
│   ├── presentation/           # UI layer
│   │   ├── ui/                 # Compose screens
│   │   ├── movielist/          # Movie list feature
│   │   ├── moviedetail/        # Movie detail feature
│   │   └── navigation/         # App navigation
│   ├── di/                     # Dependency injection (Hilt)
│   └── util/                   # Utility classes
├── src/test/                   # Unit tests
└── src/androidTest/            # UI/Integration tests

shared/                         # Kotlin Multiplatform Module
├── commonMain/                 # Shared business logic
├── androidMain/                # Android-specific implementations
└── iosMain/                    # iOS-specific implementations (future)
```

## 💾 Caching System

The app implements a sophisticated **3-tier caching strategy** for optimal performance and offline support:

### Cache Storage Locations

#### 1. **Room Database** (SQLite)
- **Location**: `Android/data/com.example.movielist/databases/`
- **Tables**:
  - `trending_cache` - Cached trending movies by page
  - `search_cache` - Cached search results by query and page
  - `movie_detail` - Cached detailed movie information

#### 2. **Cache Validation**
- **Cache Duration**: 24 hours (configurable)
- **Validation Logic**: `CacheValidator.isValid(lastFetched)`
- **Fallback Strategy**: Use stale cache when network fails

### How Caching Works

```
User Request → Check Cache → Cache Valid? → Return Cached Data
                    ↓              ↓
              Cache Invalid    Network Request → Update Cache → Return Fresh Data
                    ↓              ↓
              Network Fails   Return Stale Cache (if available)
```

#### Example Cache Flow:

1. **Cache Hit (Valid)**:
   ```
   Request Trending Movies Page 1
   → Check trending_cache table
   → Cache found & < 24 hours old
   → Return cached movies (Fast! 🚀)
   ```

2. **Cache Miss or Expired**:
   ```
   Request Trending Movies Page 1
   → Check trending_cache table
   → No cache OR > 24 hours old
   → Fetch from TMDB API
   → Save to trending_cache table
   → Return fresh movies
   ```

3. **Network Error (Offline)**:
   ```
   Request Trending Movies Page 1
   → Check trending_cache table
   → Cache expired but network fails
   → Return stale cached movies
   → Show offline indicator
   ```

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Hilt
- **Async**: Coroutines + Flow

### Data Layer
- **Local Database**: Room
- **Network**: Retrofit + OkHttp
- **JSON Parsing**: Moshi
- **Image Loading**: Coil

### Testing
- **Unit Tests**: JUnit + MockK + Coroutines Test
- **UI Tests**: Compose Testing + Hilt Testing

### Future: Kotlin Multiplatform
- **Shared Module**: Business logic shared between platforms
- **Platform Modules**: Android/iOS specific implementations

## 🔧 Key Components

### Data Flow

1. **UI Layer** (Compose Screens) triggers actions
2. **ViewModel** calls appropriate **Use Case**
3. **Use Case** calls **Repository** interface
4. **Repository Implementation** checks **Local Cache** first
5. If cache invalid/missing, fetches from **Network API**
6. Fresh data is cached and returned up the chain

### Error Handling

The app gracefully handles various error scenarios:

- **No Internet**: Shows cached data + offline indicator
- **API Timeout**: Shows timeout message with retry option
- **API Errors**: Shows user-friendly error messages
- **Empty Results**: Shows appropriate empty state

### Offline Support

- **Offline-First**: Always check cache before network
- **Stale-While-Revalidate**: Show cached data immediately, update in background
- **Graceful Degradation**: App remains functional without internet

## 🧪 Testing Strategy

### Unit Tests
- **Repository Tests**: Cache validation, network error handling
- **Use Case Tests**: Business logic validation
- **ViewModel Tests**: UI state management

### UI Tests
- **Navigation Tests**: Screen transitions
- **User Interaction Tests**: Search, movie selection
- **Error State Tests**: Error message display

## 🚀 Getting Started

1. Clone the repository
2. Add your TMDB API key to `local.properties`:
   ```
   TMDB_BEARER=your_api_key_here
   ```
3. Build and run the app

## 📝 Future Enhancements

- **iOS App**: Using Kotlin Multiplatform shared module

## 🏆 Architecture Benefits

- **Testability**: Clean separation enables easy unit testing
- **Maintainability**: Clear boundaries between layers
- **Scalability**: Easy to add new features
- **Offline-First**: Excellent user experience without internet
- **Performance**: Smart caching reduces API calls
- **Cross-Platform Ready**: Shared business logic for KMM

---

This architecture ensures a robust, maintainable, and user-friendly movie browsing experience with excellent offline capabilities and performance optimization.
