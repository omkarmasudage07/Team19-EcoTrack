package com.ecotrack.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Submitted by the public "Become a Recycler Partner" form. No login is
 * required to submit this - the applicant does not have an account yet.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecyclerApplicationRequest {

    @NotBlank(message = "Company name is required")
    @Size(min = 3, max = 100, message = "Company name must be between 3 and 100 characters")
    private String companyName;

    @NotBlank(message = "GST / Registration number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
            message = "Enter a valid 15-character GSTIN format (e.g. 27ABCDE1234F1Z5)"
    )
    private String registrationNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "Contact person is required")
    @Size(min = 3, max = 100, message = "Contact person must be between 3 and 100 characters")
    private String contactPerson;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10 digit mobile number starting with 6-9")
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 250, message = "Address must be between 10 and 250 characters")
    @Pattern(regexp = "^(?![0-9]+$)(?![^a-zA-Z0-9]+$)[a-zA-Z0-9\\s,#./\\-()]{10,250}$", message = "Address must contain meaningful text and cannot be numbers or special characters only")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^\\d{6}$", message = "Enter a valid 6 digit pincode")
    private String pincode;

    /** Comma-separated file paths, uploaded beforehand through a file upload endpoint. */
    private String documents;
}
