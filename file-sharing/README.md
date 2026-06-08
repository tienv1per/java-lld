# File Sharing System

Plain Java implementation for a Low Level Design / Machine Coding interview exercise.

## Scope

- In-memory only.
- File content is a `String`.
- Users are identified by `userId`; no login/auth.
- Supports folders and files.
- `parentFolderId = null` means root.
- Supports `VIEW` and `EDIT` permissions.
- Folder sharing applies to the folder subtree.
- Folder revoke removes access from the folder subtree.
- Folder delete recursively deletes all children.
- Public links are active/inactive only, without expiry.

## Design

```text
com.tien.lld.filesharing
├── Main.java
├── FileSharingService.java
├── AccessManager.java
├── LinkSharingService.java
├── FileStorage.java
├── entities/
│   ├── User.java
│   ├── FileSystemItem.java
│   ├── Folder.java
│   ├── SharedFile.java
│   ├── FilePermission.java
│   └── ShareLink.java
├── enums/
│   ├── AccessLevel.java
│   └── ItemStatus.java
└── exceptions/
    ├── FileSharingException.java
    ├── InvalidRequestException.java
    ├── UserNotFoundException.java
    ├── ItemNotFoundException.java
    └── AccessDeniedException.java
```

## Patterns Used

- Facade: `FileSharingService` exposes the public API.
- Composite: `FileSystemItem` is the base type for `Folder` and `SharedFile`.
- Builder: `User`, `Folder`, `SharedFile`, and `ShareLink`.

## Run

From repository root:

```bash
javac -d /private/tmp/java-lld-file-sharing-out \
  $(find file-sharing/src -name "*.java")

java -cp /private/tmp/java-lld-file-sharing-out com.tien.lld.filesharing.Main
```

