package com.tien.lld.filesharing;

import com.tien.lld.filesharing.entities.Folder;
import com.tien.lld.filesharing.entities.ShareLink;
import com.tien.lld.filesharing.entities.SharedFile;
import com.tien.lld.filesharing.entities.User;
import com.tien.lld.filesharing.enums.AccessLevel;
import com.tien.lld.filesharing.exceptions.FileSharingException;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        FileSharingService service = new FileSharingService();

        User alice = service.createUser("Alice", "alice@example.com");
        User bob = service.createUser("Bob", "bob@example.com");
        User charlie = service.createUser("Charlie", "charlie@example.com");

        System.out.println("Users:");
        System.out.println(alice);
        System.out.println(bob);
        System.out.println(charlie);

        Folder projects = service.createFolder(alice.getId(), null, "Projects");
        SharedFile designFile = service.uploadFile(
                alice.getId(),
                projects.getId(),
                "design.txt",
                "Initial design document"
        );
        Folder specs = service.createFolder(alice.getId(), projects.getId(), "Specs");
        SharedFile architectureFile = service.uploadFile(
                alice.getId(),
                specs.getId(),
                "architecture.txt",
                "Architecture notes"
        );

        System.out.println();
        System.out.println("Alice root: " + service.listFolder(alice.getId(), null));
        System.out.println("Alice Projects: " + service.listFolder(alice.getId(), projects.getId()));
        System.out.println("Alice Specs: " + service.listFolder(alice.getId(), specs.getId()));

        run("Bob downloads Alice file before sharing", () ->
                System.out.println(service.downloadFile(bob.getId(), designFile.getId()))
        );

        run("Alice shares Projects with Bob as VIEW", () -> {
            service.shareItem(alice.getId(), projects.getId(), bob.getId(), AccessLevel.VIEW);
            System.out.println("Bob Projects: " + service.listFolder(bob.getId(), projects.getId()));
            System.out.println("Bob Specs: " + service.listFolder(bob.getId(), specs.getId()));
            System.out.println("Bob downloads: " + service.downloadFile(bob.getId(), designFile.getId()));
            System.out.println("Bob downloads nested file: "
                    + service.downloadFile(bob.getId(), architectureFile.getId()));
        });

        run("Bob updates design.txt with VIEW permission", () ->
                System.out.println(service.updateFile(bob.getId(), designFile.getId(), "Bob edit attempt"))
        );

        run("Alice upgrades Projects share for Bob to EDIT", () -> {
            service.shareItem(alice.getId(), projects.getId(), bob.getId(), AccessLevel.EDIT);
            System.out.println(service.updateFile(bob.getId(), designFile.getId(), "Updated by Bob"));
            System.out.println("Alice downloads after Bob update: "
                    + service.downloadFile(alice.getId(), designFile.getId()));
        });

        run("Charlie downloads without permission", () ->
                System.out.println(service.downloadFile(charlie.getId(), designFile.getId()))
        );

        final ShareLink[] linkHolder = new ShareLink[1];

        run("Alice creates public link", () -> {
            linkHolder[0] = service.createShareLink(alice.getId(), designFile.getId(), AccessLevel.VIEW);
            System.out.println(linkHolder[0]);
            System.out.println("Download by link: " + service.downloadByLink(linkHolder[0].getId()));
        });

        run("Alice deactivates public link", () -> {
            service.deactivateShareLink(alice.getId(), linkHolder[0].getId());
            System.out.println("Link deactivated");
        });

        run("Download by inactive link", () ->
                System.out.println(service.downloadByLink(linkHolder[0].getId()))
        );

        run("Bob tries to share Alice file to Charlie", () ->
                service.shareItem(bob.getId(), designFile.getId(), charlie.getId(), AccessLevel.VIEW)
        );

        run("Alice revokes Bob access on Projects", () -> {
            service.revokeAccess(alice.getId(), projects.getId(), bob.getId());
            System.out.println("Bob access revoked");
        });

        run("Bob downloads after revoke", () ->
                System.out.println(service.downloadFile(bob.getId(), designFile.getId()))
        );

        run("Alice deletes Projects recursively", () -> {
            service.deleteItem(alice.getId(), projects.getId());
            System.out.println("Projects deleted recursively");
        });

        run("Alice lists deleted Projects", () ->
                System.out.println(service.listFolder(alice.getId(), projects.getId()))
        );

        run("Alice downloads deleted file", () ->
                System.out.println(service.downloadFile(alice.getId(), designFile.getId()))
        );

        run("Alice downloads deleted nested file", () ->
                System.out.println(service.downloadFile(alice.getId(), architectureFile.getId()))
        );

        run("Create folder with blank name", () ->
                System.out.println(service.createFolder(alice.getId(), null, " "))
        );

        run("Unknown user lists root", () ->
                System.out.println(service.listFolder("missing-user", null))
        );

        run("Invalid file id", () ->
                System.out.println(service.downloadFile(alice.getId(), "missing-file"))
        );
    }

    private static void run(String title, DemoAction action) {
        System.out.println();
        System.out.println("Testcase: " + title);
        try {
            action.run();
        } catch (FileSharingException exception) {
            System.out.println("Failed: " + exception.getMessage());
        }
    }

    @FunctionalInterface
    private interface DemoAction {
        void run();
    }
}
