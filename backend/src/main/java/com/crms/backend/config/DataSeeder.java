package com.crms.backend.config;

import com.crms.backend.model.Admin;
import com.crms.backend.model.CaseRecord;
import com.crms.backend.model.Criminal;
import com.crms.backend.model.Officer;
import com.crms.backend.repository.AdminRepository;
import com.crms.backend.repository.CaseRepository;
import com.crms.backend.repository.CriminalRepository;
import com.crms.backend.repository.OfficerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final OfficerRepository officerRepository;
    private final CaseRepository caseRepository;
    private final CriminalRepository criminalRepository;

    public DataSeeder(AdminRepository adminRepository,
                      OfficerRepository officerRepository,
                      CaseRepository caseRepository,
                      CriminalRepository criminalRepository) {
        this.adminRepository = adminRepository;
        this.officerRepository = officerRepository;
        this.caseRepository = caseRepository;
        this.criminalRepository = criminalRepository;
    }

    @Override
    public void run(String... args) {
        // Seed default admin if none exists
        if (adminRepository.count() == 0) {
            Admin admin = Admin.builder()
                    .name("Admin User")
                    .email("admin@crms.gov")
                    .username("admin")
                    .password("admin123")
                    .role("Admin")
                    .build();
            adminRepository.save(admin);
            System.out.println(" Default admin created: admin@crms.gov / admin123");
        }

        // Seed sample officers if none exist
        if (officerRepository.count() == 0) {
            List<Officer> sampleOfficers = List.of(
                    Officer.builder().id("OFC-001").name("Inspector Raj Sharma").email("raj.sharma@crms.gov").password("officer123").department("Homicide Division").status("Active").role("Officer").build(),
                    Officer.builder().id("OFC-002").name("SI Priya Patel").email("priya.patel@crms.gov").password("officer123").department("Cyber Crime Cell").status("Active").role("Officer").build(),
                    Officer.builder().id("OFC-003").name("ASI Vikram Singh").email("vikram.singh@crms.gov").password("officer123").department("Anti-Narcotics").status("Inactive").role("Officer").build(),
                    Officer.builder().id("OFC-004").name("Inspector Neha Kulkarni").email("neha.kulkarni@crms.gov").password("officer123").department("Forensic Department").status("Active").role("Officer").build()
            );
            officerRepository.saveAll(sampleOfficers);
            System.out.println(" Sample officers seeded");
        }

        // Seed sample cases if none exist
        if (caseRepository.count() == 0) {
            List<CaseRecord> sampleCases = List.of(
                    CaseRecord.builder().id("CR-2024-001").type("Robbery").complainer("Amit Deshmukh").criminal("Suresh Kale").dateReported(LocalDate.parse("2024-03-15")).status("Pending").build(),
                    CaseRecord.builder().id("CR-2024-002").type("Assault").complainer("Rekha Jadhav").criminal("Ramesh Patil").dateReported(LocalDate.parse("2024-04-20")).status("Solved").build(),
                    CaseRecord.builder().id("CR-2024-003").type("Theft").complainer("Vikas Mane").criminal("Unknown").dateReported(LocalDate.parse("2024-05-10")).status("Pending").build(),
                    CaseRecord.builder().id("CR-2024-004").type("Hacking").complainer("Sneha Rao").criminal("Ajay Verma").dateReported(LocalDate.parse("2024-06-01")).status("Solved").build(),
                    CaseRecord.builder().id("CR-2024-005").type("Burglary").complainer("Kiran Mehta").criminal("Unknown").dateReported(LocalDate.parse("2024-07-22")).status("Pending").build(),
                    CaseRecord.builder().id("CR-2024-006").type("Murder").complainer("Dept. of Police").criminal("Manoj Tiwari").dateReported(LocalDate.parse("2024-08-05")).status("Pending").build(),
                    CaseRecord.builder().id("CR-2024-007").type("Online Fraud").complainer("Pradeep Joshi").criminal("Unknown").dateReported(LocalDate.parse("2024-09-12")).status("Pending").build()
            );
            caseRepository.saveAll(sampleCases);
            System.out.println(" Sample cases seeded");
        }

        // Seed sample criminals if none exist
        if (criminalRepository.count() == 0) {
            List<Criminal> sampleCriminals = List.of(
                    Criminal.builder().id("CRIM-2024-001").name("Suresh Kale").crime("Robbery").status("Wanted").sentenceEnd(null).age(32).gender("Male").occupation("Unemployed").address("Pune, Maharashtra").officer("Inspector Raj Sharma").build(),
                    Criminal.builder().id("CRIM-2024-002").name("Ramesh Patil").crime("Assault").status("Active").sentenceEnd(null).age(28).gender("Male").occupation("Daily Laborer").address("Nashik, Maharashtra").officer("SI Priya Patel").build(),
                    Criminal.builder().id("CRIM-2024-003").name("Ajay Verma").crime("Hacking").status("Released").sentenceEnd(LocalDate.parse("2025-01-15")).age(24).gender("Male").occupation("IT Professional").address("Mumbai, Maharashtra").officer("SI Priya Patel").build(),
                    Criminal.builder().id("CRIM-2024-004").name("Manoj Tiwari").crime("Murder").status("Wanted").sentenceEnd(null).age(45).gender("Male").occupation("Business").address("Thane, Maharashtra").officer("Inspector Raj Sharma").build()
            );
            criminalRepository.saveAll(sampleCriminals);
            System.out.println(" Sample criminals seeded");
        }

        System.out.println(" Database initialized and seeded successfully");
    }
}
