# MSA-Rapid-Fetch API Documentation

The `FletchLinkManager` class is the main entry point for the MSA-Rapid-Fetch library, providing methods for managing authentication sessions, user information, and session persistence in an Android application. It leverages the `MinecraftAuth` library for Bedrock authentication with Microsoft accounts.

Below is a detailed description of the API, including its classes, methods, and usage.

## Table of Contents
- [FletchLinkManager](#fletchlinkmanager)
    - [Constructor](#constructor)
    - [Companion Object](#companion-object)
    - [Methods](#methods)
- [UserInfo](#userinfo)
- [SessionManager](#sessionmanager)
    - [Constructor](#sessionmanager-constructor)
    - [Methods](#sessionmanager-methods)
- [AuthSession](#authsession)
    - [Constructor](#authsession-constructor)
    - [Methods](#authsession-methods)
- [AuthCallback](#authcallback)
    - [Methods](#authcallback-methods)

---

## FletchLinkManager

The `FletchLinkManager` class is a singleton that serves as the primary interface for managing authentication and session-related operations.

### Constructor
```kotlin
private constructor(private val context: Context)
```
- **Description**: Private constructor to enforce singleton pattern. Use `getInstance(context)` to obtain an instance.
- **Parameters**:
    - `context` (`Context`): The Android application context.

### Companion Object
```kotlin
companion object {
    fun getInstance(context: Context): FletchLinkManager
}
```
- **Description**: Retrieves the singleton instance of `FletchLinkManager`. Creates a new instance if none exists, using the provided application context.
- **Parameters**:
    - `context` (`Context`): The Android application context.
- **Returns**: `FletchLinkManager` instance.

### Methods

#### `hasValidSession`
```kotlin
suspend fun hasValidSession(): Boolean
```
- **Description**: Checks if there is a valid, non-expired session stored.
- **Returns**: `Boolean` indicating whether a valid session exists.
- **Coroutine Scope**: Executes on `Dispatchers.IO`.

#### `getCurrentSession`
```kotlin
suspend fun getCurrentSession(): StepFullBedrockSession.FullBedrockSession?
```
- **Description**: Retrieves the current session if it is valid and not expired; otherwise, returns `null`.
- **Returns**: `StepFullBedrockSession.FullBedrockSession?` representing the current session or `null`.
- **Coroutine Scope**: Executes on `Dispatchers.IO`.

#### `startAuthFlow`
```kotlin
fun startAuthFlow(callback: AuthCallback): AuthSession
```
- **Description**: Initiates the authentication flow, returning an `AuthSession` to manage the process.
- **Parameters**:
    - `callback` (`AuthCallback`): Callback to handle authentication events (e.g., device code received, success, or error).
- **Returns**: `AuthSession` instance to control the authentication process.

#### `clearSession`
```kotlin
fun clearSession()
```
- **Description**: Deletes the stored session data, effectively logging out the user.

#### `getUserInfo`
```kotlin
suspend fun getUserInfo(): UserInfo?
```
- **Description**: Retrieves user information from the current valid session.
- **Returns**: `UserInfo?` containing the user's display name, UUID, and Realms access status, or `null` if no valid session exists.
- **Coroutine Scope**: Executes on `Dispatchers.IO`.

---

## UserInfo

The `UserInfo` class is a data class that holds user information retrieved from a valid session.

```kotlin
data class UserInfo(
    val displayName: String,
    val uuid: String,
    val hasRealmsAccess: Boolean
)
```
- **Fields**:
    - `displayName` (`String`): The user's display name.
    - `uuid` (`String`): The user's unique identifier.
    - `hasRealmsAccess` (`Boolean`): Indicates whether the user has access to Minecraft Realms.

---

## SessionManager

The `SessionManager` class handles the persistence and management of authentication sessions, storing them as JSON files in the app's internal storage.

### Constructor
```kotlin
internal class SessionManager(private val context: Context)
```
- **Description**: Internal constructor for `SessionManager`.
- **Parameters**:
    - `context` (`Context`): The Android application context.

### Methods

#### `loadSavedSession`
```kotlin
fun loadSavedSession(httpClient: HttpClient): StepFullBedrockSession.FullBedrockSession?
```
- **Description**: Loads a saved session from storage, attempting to refresh it if expired or outdated. Returns `null` if the session is invalid or cannot be loaded.
- **Parameters**:
    - `httpClient` (`HttpClient`): The HTTP client used for refreshing sessions.
- **Returns**: `StepFullBedrockSession.FullBedrockSession?` representing the loaded session or `null`.

#### `saveSession`
```kotlin
fun saveSession(session: StepFullBedrockSession.FullBedrockSession)
```
- **Description**: Saves the provided session to a JSON file in the app's internal storage.
- **Parameters**:
    - `session` (`StepFullBedrockSession.FullBedrockSession`): The session to save.
- **Throws**: Exception if saving fails.

#### `deleteSession`
```kotlin
fun deleteSession()
```
- **Description**: Deletes the stored session file, effectively clearing the session.

---

## AuthSession

The `AuthSession` class manages the authentication flow, interacting with the `AuthCallback` to report progress and results.

### Constructor
```kotlin
internal constructor(
    private val httpClient: HttpClient,
    private val sessionManager: SessionManager,
    private val callback: AuthCallback
)
```
- **Description**: Internal constructor for `AuthSession`.
- **Parameters**:
    - `httpClient` (`HttpClient`): The HTTP client for authentication requests.
    - `sessionManager` (`SessionManager`): The session manager for saving sessions.
    - `callback` (`AuthCallback`): Callback for authentication events.

### Methods

#### `start`
```kotlin
fun start()
```
- **Description**: Starts the authentication flow, invoking the `AuthCallback` for events such as receiving a device code, successful authentication, or errors.

#### `cancel`
```kotlin
fun cancel()
```
- **Description**: Cancels the ongoing authentication process.

---

## AuthCallback

The `AuthCallback` interface defines methods for handling authentication events.

### Methods

#### `onDeviceCodeReceived`
```kotlin
fun onDeviceCodeReceived(userCode: String, verificationUri: String)
```
- **Description**: Called when a device code and verification URI are received during the authentication flow.
- **Parameters**:
    - `userCode` (`String`): The code the user must enter at the verification URI.
    - `verificationUri` (`String`): The URL where the user must enter the code.

#### `onAuthSuccess`
```kotlin
fun onAuthSuccess(session: StepFullBedrockSession.FullBedrockSession)
```
- **Description**: Called when authentication succeeds.
- **Parameters**:
    - `session` (`StepFullBedrockSession.FullBedrockSession`): The authenticated session.

#### `onAuthError`
```kotlin
fun onAuthError(error: String)
```
- **Description**: Called when an error occurs during authentication.
- **Parameters**:
    - `error` (`String`): The error message.

---

## Usage Example

```kotlin
// Obtain the FletchLinkManager instance
val manager = FletchLinkManager.getInstance(context)

// Check for a valid session
if (manager.hasValidSession()) {
    val userInfo = manager.getUserInfo()
    if (userInfo != null) {
        println("User: ${userInfo.displayName}, UUID: ${userInfo.uuid}")
    }
} else {
    // Start authentication flow
    val authSession = manager.startAuthFlow(object : AuthCallback {
        override fun onDeviceCodeReceived(userCode: String, verificationUri: String) {
            println("Please visit $verificationUri and enter code: $userCode")
        }

        override fun onAuthSuccess(session: StepFullBedrockSession.FullBedrockSession) {
            println("Authentication successful!")
        }

        override fun onAuthError(error: String) {
            println("Authentication failed: $error")
        }
    })
    authSession.start()
}

// Clear session if needed
manager.clearSession()
```

---

## Notes
- The MSA-Rapid-Fetch library uses Kotlin coroutines for asynchronous operations, ensuring non-blocking I/O tasks.
- Sessions are stored in the app's internal storage as `bedrock_session.json`.
- The `MinecraftAuth` library handles the underlying Microsoft authentication flow for Bedrock edition.
- Ensure proper error handling in production code, especially for network-related operations.
- The `AuthCallback` interface allows for flexible handling of authentication events, suitable for UI integration.