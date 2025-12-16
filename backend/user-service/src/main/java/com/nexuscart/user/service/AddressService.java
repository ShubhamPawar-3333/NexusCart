package com.nexuscart.user.service;

import com.nexuscart.user.entity.Address;
import com.nexuscart.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public List<Address> getUserAddresses(UUID userId) {
        return addressRepository.findByUserId(userId);
    }

    public Address getAddress(UUID addressId, UUID userId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    public Address getDefaultAddress(UUID userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElse(null);
    }

    @Transactional
    public Address createAddress(Address address) {
        // If this is the first address or marked as default, set it as default
        List<Address> existingAddresses = addressRepository.findByUserId(address.getUserId());

        if (existingAddresses.isEmpty() || address.isDefault()) {
            addressRepository.clearDefaultAddress(address.getUserId());
            address.setDefault(true);
        }

        log.info("Creating address for user: {}", address.getUserId());
        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(UUID addressId, UUID userId, Address updates) {
        Address existing = getAddress(addressId, userId);

        if (updates.getLabel() != null)
            existing.setLabel(updates.getLabel());
        if (updates.getFullName() != null)
            existing.setFullName(updates.getFullName());
        if (updates.getPhoneNumber() != null)
            existing.setPhoneNumber(updates.getPhoneNumber());
        if (updates.getAddressLine1() != null)
            existing.setAddressLine1(updates.getAddressLine1());
        if (updates.getAddressLine2() != null)
            existing.setAddressLine2(updates.getAddressLine2());
        if (updates.getCity() != null)
            existing.setCity(updates.getCity());
        if (updates.getState() != null)
            existing.setState(updates.getState());
        if (updates.getPostalCode() != null)
            existing.setPostalCode(updates.getPostalCode());
        if (updates.getCountry() != null)
            existing.setCountry(updates.getCountry());
        if (updates.getLandmark() != null)
            existing.setLandmark(updates.getLandmark());
        if (updates.getType() != null)
            existing.setType(updates.getType());

        if (updates.isDefault() && !existing.isDefault()) {
            addressRepository.clearDefaultAddress(userId);
            existing.setDefault(true);
        }

        log.info("Updated address {} for user: {}", addressId, userId);
        return addressRepository.save(existing);
    }

    @Transactional
    public void setDefaultAddress(UUID addressId, UUID userId) {
        Address address = getAddress(addressId, userId);
        addressRepository.clearDefaultAddress(userId);
        address.setDefault(true);
        addressRepository.save(address);
        log.info("Set default address {} for user: {}", addressId, userId);
    }

    @Transactional
    public void deleteAddress(UUID addressId, UUID userId) {
        Address address = getAddress(addressId, userId);
        addressRepository.delete(address);
        log.info("Deleted address {} for user: {}", addressId, userId);
    }
}
