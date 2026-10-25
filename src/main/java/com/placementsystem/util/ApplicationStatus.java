package com.placementsystem.util;

public enum ApplicationStatus {
    APPLIED,
    SHORTLISTED,
    INTERVIEW_SCHEDULED,
    SELECTED,
    REJECTED;

    public static boolean isValid(String status) {
        for (ApplicationStatus s : ApplicationStatus.values()) {
            if (s.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
}