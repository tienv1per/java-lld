package com.tien.lld.filesharing.enums;

public enum AccessLevel {
    VIEW,
    EDIT;

    public boolean allows(AccessLevel requiredAccess) {
        if (this == EDIT) {
            return true;
        }
        return this == requiredAccess;
    }
}

