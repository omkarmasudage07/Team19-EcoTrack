package com.ecotrack.pickup.enums;

/**
 * The exact lifecycle a Pickup moves through. A pickup can only move
 * forward one step at a time (enforced in PickupServiceImpl), except for
 * cancellation, which is only allowed while still PENDING.
 */
public enum PickupStatus {
    PENDING,
    ACCEPTED,
    ON_THE_WAY,
    COLLECTED,
    PROCESSING,
    COMPLETED,
    CANCELLED
}
