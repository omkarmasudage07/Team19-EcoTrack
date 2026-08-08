package com.ecotrack.pickup.config;

import com.ecotrack.pickup.entity.Pickup;
import com.ecotrack.pickup.entity.WasteCategory;
import com.ecotrack.pickup.enums.PickupStatus;
import com.ecotrack.pickup.repository.PickupRepository;
import com.ecotrack.pickup.repository.WasteCategoryRepository;
import com.ecotrack.pickup.util.PickupNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PickupSeeder implements CommandLineRunner {

    private final PickupRepository pickupRepository;
    private final WasteCategoryRepository wasteCategoryRepository;

    @Override
    public void run(String... args) {
        if (pickupRepository.count() > 0) {
            return;
        }

        log.info("Seeding demo pickups across all 6 regions...");
        List<WasteCategory> categories = wasteCategoryRepository.findAll();
        WasteCategory defaultCat = !categories.isEmpty() ? categories.get(0) : null;

        List<Pickup> demoPickups = List.of(
                Pickup.builder()
                        .pickupNumber(PickupNumberGenerator.generate())
                        .citizenId(1L)
                        .pickupAddress("FC Road, Deccan Gymkhana, Pune")
                        .pickupCity("Pune")
                        .pickupPincode("411004")
                        .regionName("Pune Region")
                        .latitude(18.5204)
                        .longitude(73.8567)
                        .pickupDate(LocalDate.now().plusDays(1))
                        .pickupTimeSlot("10:00 AM - 01:00 PM")
                        .wasteCategory(defaultCat)
                        .notes("Old laptops and broken mobile chargers")
                        .status(PickupStatus.PENDING)
                        .build(),
                Pickup.builder()
                        .pickupNumber(PickupNumberGenerator.generate())
                        .citizenId(2L)
                        .pickupAddress("Marine Drive, Nariman Point, Mumbai")
                        .pickupCity("Mumbai")
                        .pickupPincode("400021")
                        .regionName("Mumbai Region")
                        .latitude(18.9438)
                        .longitude(72.8232)
                        .pickupDate(LocalDate.now().plusDays(2))
                        .pickupTimeSlot("02:00 PM - 05:00 PM")
                        .wasteCategory(defaultCat)
                        .notes("Scrap printer and desktop CRT monitors")
                        .status(PickupStatus.PENDING)
                        .build(),
                Pickup.builder()
                        .pickupNumber(PickupNumberGenerator.generate())
                        .citizenId(3L)
                        .pickupAddress("Tarabai Park, Kolhapur")
                        .pickupCity("Kolhapur")
                        .pickupPincode("416003")
                        .regionName("Kolhapur Region")
                        .latitude(16.7050)
                        .longitude(74.2433)
                        .pickupDate(LocalDate.now().plusDays(1))
                        .pickupTimeSlot("09:00 AM - 12:00 PM")
                        .wasteCategory(defaultCat)
                        .notes("Battery packs and electronic toys")
                        .status(PickupStatus.PENDING)
                        .build(),
                Pickup.builder()
                        .pickupNumber(PickupNumberGenerator.generate())
                        .citizenId(4L)
                        .pickupAddress("Dharampeth, Nagpur")
                        .pickupCity("Nagpur")
                        .pickupPincode("440010")
                        .regionName("Nagpur Region")
                        .latitude(21.1458)
                        .longitude(79.0882)
                        .pickupDate(LocalDate.now().plusDays(3))
                        .pickupTimeSlot("11:00 AM - 02:00 PM")
                        .wasteCategory(defaultCat)
                        .notes("Defective kitchen electronic appliances")
                        .status(PickupStatus.PENDING)
                        .build(),
                Pickup.builder()
                        .pickupNumber(PickupNumberGenerator.generate())
                        .citizenId(5L)
                        .pickupAddress("College Road, Nashik")
                        .pickupCity("Nashik")
                        .pickupPincode("422005")
                        .regionName("Nashik Region")
                        .latitude(19.9975)
                        .longitude(73.7898)
                        .pickupDate(LocalDate.now().plusDays(2))
                        .pickupTimeSlot("10:00 AM - 01:00 PM")
                        .wasteCategory(defaultCat)
                        .notes("Old telecom modems and copper wiring")
                        .status(PickupStatus.PENDING)
                        .build(),
                Pickup.builder()
                        .pickupNumber(PickupNumberGenerator.generate())
                        .citizenId(6L)
                        .pickupAddress("Powai Naka, Satara")
                        .pickupCity("Satara")
                        .pickupPincode("415001")
                        .regionName("Satara Region")
                        .latitude(17.6805)
                        .longitude(74.0183)
                        .pickupDate(LocalDate.now().plusDays(1))
                        .pickupTimeSlot("03:00 PM - 06:00 PM")
                        .wasteCategory(defaultCat)
                        .notes("UPS batteries and solar scrap components")
                        .status(PickupStatus.PENDING)
                        .build()
        );

        pickupRepository.saveAll(demoPickups);
        log.info("Seeded {} demo region pickups.", demoPickups.size());
    }
}
