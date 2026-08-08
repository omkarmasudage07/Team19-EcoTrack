package com.ecotrack.pickup.config;

import com.ecotrack.pickup.entity.WasteCategory;
import com.ecotrack.pickup.repository.WasteCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Pre-populates the common e-waste categories so the app is usable the
 * moment it starts, instead of forcing the Admin to create every category
 * by hand before a single pickup can be scheduled. Runs once - if
 * categories already exist, it does nothing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WasteCategorySeeder implements CommandLineRunner {

    private final WasteCategoryRepository wasteCategoryRepository;

    private static final List<WasteCategory> DEFAULT_CATEGORIES = List.of(
            WasteCategory.builder().name("Mobile Phones").description("Smartphones and feature phones").active(true).build(),
            WasteCategory.builder().name("Laptops & Computers").description("Laptops, desktops, monitors, peripherals").active(true).build(),
            WasteCategory.builder().name("Batteries").description("Li-ion, lead-acid and other battery cells").active(true).build(),
            WasteCategory.builder().name("PCB Boards").description("Printed circuit boards from any device").active(true).build(),
            WasteCategory.builder().name("Home Appliances").description("Small appliances - mixers, irons, kettles").active(true).build(),
            WasteCategory.builder().name("Large Appliances").description("Refrigerators, washing machines, ACs").active(true).build(),
            WasteCategory.builder().name("Cables & Wires").description("Copper wiring, chargers, cables").active(true).build(),
            WasteCategory.builder().name("Other Electronics").description("Any other electronic waste not listed above").active(true).build()
    );

    @Override
    public void run(String... args) {
        if (wasteCategoryRepository.count() > 0) {
            log.info("Waste categories already seeded, skipping");
            return;
        }
        wasteCategoryRepository.saveAll(DEFAULT_CATEGORIES);
        log.info("Seeded {} default waste categories", DEFAULT_CATEGORIES.size());
    }
}
