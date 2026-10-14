package com.rssb.assistance.entity;

public enum SyncStatus {
    PENDING,
    SEARCHING,
    COMPLETE,

    // Legacy values kept for backward compatibility with existing rows.
    SYNCED,
    FAILED
}
