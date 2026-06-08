package com.tien.lld.filesharing;

import com.tien.lld.filesharing.entities.ShareLink;
import com.tien.lld.filesharing.entities.SharedFile;
import com.tien.lld.filesharing.enums.AccessLevel;
import com.tien.lld.filesharing.exceptions.AccessDeniedException;
import com.tien.lld.filesharing.exceptions.InvalidRequestException;

import java.time.LocalDateTime;
import java.util.UUID;

public final class LinkSharingService {
    private final FileStorage storage;

    public LinkSharingService(FileStorage storage) {
        this.storage = storage;
    }

    public ShareLink createShareLink(SharedFile file, AccessLevel accessLevel) {
        if (file == null || !file.isActive()) {
            throw new InvalidRequestException("active file is required to create share link");
        }
        if (accessLevel == null) {
            throw new InvalidRequestException("accessLevel is required");
        }

        ShareLink link = ShareLink.builder()
                .id(UUID.randomUUID().toString())
                .fileId(file.getId())
                .accessLevel(accessLevel)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        storage.saveShareLink(link);
        return link;
    }

    public String downloadByLink(String linkId) {
        ShareLink link = storage.getActiveShareLink(linkId);
        SharedFile file = storage.getActiveFile(link.getFileId());
        return file.getContent();
    }

    public void deactivateShareLink(String ownerId, String linkId) {
        ShareLink link = storage.getActiveShareLink(linkId);
        SharedFile file = storage.getActiveFile(link.getFileId());
        if (!file.isOwnedBy(ownerId)) {
            throw new AccessDeniedException("only owner can deactivate link: " + linkId);
        }
        link.deactivate();
    }
}

