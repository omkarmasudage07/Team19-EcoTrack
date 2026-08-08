package com.ecotrack.pickup.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePickupRequest {

    @NotBlank(message = "Pickup address is required")
    @Size(min = 10, max = 250, message = "Address must be between 10 and 250 characters")
    private String pickupAddress;

    @NotBlank(message = "City is required")
    private String pickupCity;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^\\d{6}$", message = "Enter a valid 6 digit pincode")
    private String pickupPincode;

    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    @DecimalMax(value = "500.0", message = "Maximum allowed weight is 500 kg")
    private Double estimatedWeight;

    private String regionName;

    /** Optional - set when the Citizen drops a pin on the map instead of (or in addition to) typing an address. */
    private Double latitude;
    private Double longitude;

    @NotNull(message = "Pickup date is required")
    @FutureOrPresent(message = "Pickup date cannot be in the past")
    private LocalDate pickupDate;

    @NotBlank(message = "Pickup time slot is required")
    private String pickupTimeSlot;

    @NotNull(message = "Waste category is required")
    private Long wasteCategoryId;

    @Size(max = 500, message = "Notes must be under 500 characters")
    private String notes;

    /** Image URLs, already uploaded via a separate file upload endpoint. */
    private List<String> imageUrls;
}
