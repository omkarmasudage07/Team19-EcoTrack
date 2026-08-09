package com.ecotrack.material.config;

import com.ecotrack.material.entity.EcoPointRule;
import com.ecotrack.material.entity.Reward;
import com.ecotrack.material.enums.RewardCategory;
import com.ecotrack.material.repository.EcoPointRuleRepository;
import com.ecotrack.material.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RewardSeeder implements CommandLineRunner {

    private final RewardRepository rewardRepository;
    private final EcoPointRuleRepository ruleRepository;

    @Override
    public void run(String... args) {
        seedEcoPointRules();
        seedRewards();
    }

    private void seedEcoPointRules() {
        if (ruleRepository.count() > 0) {
            return;
        }

        log.info("Seeding default EcoPoint Rules per waste category...");
        List<EcoPointRule> rules = List.of(
                EcoPointRule.builder().categoryName("Mobile Phones").pointsPerUnit(100).ruleType("FLAT").description("100 points per recycled phone").active(true).build(),
                EcoPointRule.builder().categoryName("Laptops").pointsPerUnit(250).ruleType("FLAT").description("250 points per recycled laptop").active(true).build(),
                EcoPointRule.builder().categoryName("Batteries").pointsPerUnit(50).ruleType("FLAT").description("50 points per recycled battery pack").active(true).build(),
                EcoPointRule.builder().categoryName("Plastic E-Waste").pointsPerUnit(10).ruleType("PER_KG").description("10 points per Kg of plastic body scrap").active(true).build(),
                EcoPointRule.builder().categoryName("Scrap Metals").pointsPerUnit(20).ruleType("PER_KG").description("20 points per Kg of metal e-waste scrap").active(true).build(),
                EcoPointRule.builder().categoryName("Home Appliances").pointsPerUnit(150).ruleType("FLAT").description("150 points per appliance").active(true).build()
        );

        ruleRepository.saveAll(rules);
        log.info("Seeded {} EcoPoint rules successfully.", rules.size());
    }

    private void seedRewards() {
        if (rewardRepository.count() > 0) {
            return;
        }

        log.info("Seeding default Citizen Rewards catalog...");
        List<Reward> rewards = List.of(
                Reward.builder()
                        .title("Organic Cotton Eco Tote Bag")
                        .description("Durable 100% organic cotton reusable tote bag for sustainable daily shopping.")
                        .category(RewardCategory.ECO_PRODUCT)
                        .pointsRequired(150)
                        .stockQuantity(50)
                        .imageUrl("https://images.unsplash.com/photo-1544816155-12df9643f363?w=500&auto=format&fit=crop&q=80")
                        .active(true)
                        .build(),
                Reward.builder()
                        .title("Stainless Steel Insulated Eco Bottle")
                        .description("750ml thermal double-wall leakproof water bottle keeping drinks hot/cold for 24 hours.")
                        .category(RewardCategory.ECO_PRODUCT)
                        .pointsRequired(250)
                        .stockQuantity(30)
                        .imageUrl("https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500&auto=format&fit=crop&q=80")
                        .active(true)
                        .build(),
                Reward.builder()
                        .title("Plant 1 Native Forest Tree")
                        .description("Fund the planting of 1 native tree in your name with GPS location & green certificate.")
                        .category(RewardCategory.TREE_PLANTATION)
                        .pointsRequired(50)
                        .stockQuantity(500)
                        .imageUrl("https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=500&auto=format&fit=crop&q=80")
                        .active(true)
                        .build(),
                Reward.builder()
                        .title("Solar-Powered Rechargeable LED Light")
                        .description("Eco-friendly solar garden/emergency LED light with dual solar panel charging.")
                        .category(RewardCategory.ECO_PRODUCT)
                        .pointsRequired(300)
                        .stockQuantity(20)
                        .imageUrl("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=500&auto=format&fit=crop&q=80")
                        .active(true)
                        .build(),
                Reward.builder()
                        .title("₹100 Amazon / Flipkart Eco Voucher")
                        .description("Digital shopping voucher redeemable instantly across major e-commerce platforms.")
                        .category(RewardCategory.GIFT_CARD)
                        .pointsRequired(100)
                        .stockQuantity(100)
                        .imageUrl("https://images.unsplash.com/photo-1556742049-0a67daf64f22?w=500&auto=format&fit=crop&q=80")
                        .active(true)
                        .build(),
                Reward.builder()
                        .title("Green Citizen Recycling Certificate")
                        .description("Official verifiable PDF eco-warrior certificate signed by EcoTrack CDAC Board.")
                        .category(RewardCategory.CERTIFICATE)
                        .pointsRequired(20)
                        .stockQuantity(1000)
                        .imageUrl("https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=500&auto=format&fit=crop&q=80")
                        .active(true)
                        .build()
        );

        rewardRepository.saveAll(rewards);
        log.info("Seeded {} default rewards items.", rewards.size());
    }
}
