package com.ecotrack.user.config;

import com.ecotrack.user.entity.Citizen;
import com.ecotrack.user.entity.Industry;
import com.ecotrack.user.entity.Recycler;
import com.ecotrack.user.enums.ApprovalStatus;
import com.ecotrack.user.repository.CitizenRepository;
import com.ecotrack.user.repository.IndustryRepository;
import com.ecotrack.user.repository.RecyclerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final RecyclerRepository recyclerRepository;
    private final CitizenRepository citizenRepository;
    private final IndustryRepository industryRepository;

    @Override
    public void run(String... args) {
        seedRecyclers();
        seedCitizens();
        seedIndustries();
    }

    private void seedRecyclers() {
        if (recyclerRepository.count() > 0) {
            return;
        }

        log.info("Seeding demo recyclers across all 6 administrative regions...");
        List<String> regions = List.of(
                "Pune Region", "Mumbai Region", "Kolhapur Region",
                "Nagpur Region", "Nashik Region", "Satara Region"
        );

        long userIdCounter = 200L;
        for (String regionName : regions) {
            String prefix = regionName.split(" ")[0].toLowerCase();

            for (int i = 1; i <= 2; i++) {
                userIdCounter++;
                String compName = regionName.split(" ")[0] + " EcoRecycle Partner " + i;
                String phoneNum = "98" + String.format("%08d", userIdCounter);

                Recycler recycler = Recycler.builder()
                        .userId(userIdCounter)
                        .companyName(compName)
                        .companyRegistrationNumber("27AAAC" + (1000 + userIdCounter) + "1Z5")
                        .contactPerson(prefix.toUpperCase() + " Operations Manager " + i)
                        .phone(phoneNum)
                        .address("100 Industrial Estate, " + regionName.split(" ")[0])
                        .city(regionName.split(" ")[0])
                        .state("Maharashtra")
                        .pincode("400001")
                        .regionName(regionName)
                        .approvalStatus(ApprovalStatus.APPROVED)
                        .approvedBy(1L)
                        .approvedDate(LocalDateTime.now())
                        .suspended(false)
                        .build();

                recyclerRepository.save(recycler);
            }
        }
        log.info("Seeded 12 demo recyclers across 6 regions.");
    }

    private void seedCitizens() {
        if (citizenRepository.count() > 0) {
            return;
        }

        log.info("Seeding demo citizen profiles...");
        Citizen citizen = Citizen.builder()
                .userId(2L)
                .fullName("Demo Citizen Warrior")
                .phone("9876543210")
                .address("123 Green Avenue, Deccan Gymkhana")
                .city("Pune")
                .state("Maharashtra")
                .pincode("411004")
                .build();

        citizenRepository.save(citizen);
    }

    private void seedIndustries() {
        if (industryRepository.count() > 0) {
            return;
        }

        log.info("Seeding demo industry buyer profile...");
        Industry industry = Industry.builder()
                .userId(300L)
                .companyName("EcoMetal Refiners Pvt Ltd")
                .companyRegistrationNumber("27AABC1234561Z9")
                .contactPerson("Procurement Manager")
                .phone("9123456789")
                .address("MIDC Industrial Area, Phase II")
                .city("Pune")
                .state("Maharashtra")
                .pincode("411026")
                .approvalStatus(ApprovalStatus.APPROVED)
                .approvedBy(1L)
                .approvedDate(LocalDateTime.now())
                .suspended(false)
                .build();

        industryRepository.save(industry);
    }
}
