package com.ecotrack.material.config;

import com.ecotrack.material.entity.MaterialCategory;
import com.ecotrack.material.repository.MaterialCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialCategorySeeder implements CommandLineRunner {

    private final MaterialCategoryRepository categoryRepository;

    private static final List<MaterialCategory> DEFAULT_CATEGORIES = List.of(
            MaterialCategory.builder().name("Copper").description("Recovered copper wiring and components").active(true).build(),
            MaterialCategory.builder().name("Aluminium").description("Recovered aluminium casings and parts").active(true).build(),
            MaterialCategory.builder().name("Plastic").description("Recovered plastic housings and components").active(true).build(),
            MaterialCategory.builder().name("Lithium").description("Recovered lithium from batteries").active(true).build(),
            MaterialCategory.builder().name("Iron").description("Recovered ferrous scrap").active(true).build(),
            MaterialCategory.builder().name("Gold").description("Recovered gold from connectors and PCBs").active(true).build(),
            MaterialCategory.builder().name("Silver").description("Recovered silver from contacts and PCBs").active(true).build(),
            MaterialCategory.builder().name("PCB Boards").description("Whole or shredded printed circuit boards").active(true).build(),
            MaterialCategory.builder().name("Battery Cells").description("Sorted, discharged battery cells").active(true).build()
    );

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(DEFAULT_CATEGORIES);
            log.info("Seeded {} default material categories", DEFAULT_CATEGORIES.size());
        }

        // NOTE: sample marketplace materials are intentionally NOT seeded here.
        // Every Material must belong to a real, approved Recycler (recyclerId
        // references an actual users.id in auth_db). At application startup
        // no Recycler account is guaranteed to exist yet, so seeding a
        // material with a guessed id (e.g. 1L) creates an "orphaned" listing
        // that no real Recycler can see under "My Materials" and that no
        // real Recycler can fulfill if an Industry orders it. Once at least
        // one Recycler Partner is approved through the normal application
        // workflow, they can list real materials themselves.
    }
}
