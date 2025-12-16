package com.nexuscart.user.controller;

import com.nexuscart.dto.common.ApiResponse;
import com.nexuscart.user.entity.Address;
import com.nexuscart.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "User address management")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get all addresses for current user")
    public ResponseEntity<ApiResponse<List<Address>>> getAddresses(
            @RequestHeader("X-User-Id") String userId) {
        List<Address> addresses = addressService.getUserAddresses(UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @GetMapping("/{addressId}")
    @Operation(summary = "Get specific address")
    public ResponseEntity<ApiResponse<Address>> getAddress(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID addressId) {
        Address address = addressService.getAddress(addressId, UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    @GetMapping("/default")
    @Operation(summary = "Get default address")
    public ResponseEntity<ApiResponse<Address>> getDefaultAddress(
            @RequestHeader("X-User-Id") String userId) {
        Address address = addressService.getDefaultAddress(UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    @PostMapping
    @Operation(summary = "Create new address")
    public ResponseEntity<ApiResponse<Address>> createAddress(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Address address) {
        address.setUserId(UUID.fromString(userId));
        Address created = addressService.createAddress(address);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Address created"));
    }

    @PutMapping("/{addressId}")
    @Operation(summary = "Update address")
    public ResponseEntity<ApiResponse<Address>> updateAddress(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID addressId,
            @RequestBody Address updates) {
        Address updated = addressService.updateAddress(addressId, UUID.fromString(userId), updates);
        return ResponseEntity.ok(ApiResponse.success(updated, "Address updated"));
    }

    @PatchMapping("/{addressId}/default")
    @Operation(summary = "Set address as default")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID addressId) {
        addressService.setDefaultAddress(addressId, UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.success(null, "Default address set"));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID addressId) {
        addressService.deleteAddress(addressId, UUID.fromString(userId));
        return ResponseEntity.ok(ApiResponse.success(null, "Address deleted"));
    }
}
