package com.crm.components;

import com.crm.entites.Insurance;
import com.crm.entites.User;
import com.crm.repos.InsuranceRepo;
import com.crm.DentrixAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InsuranceUploader {

    private final InsuranceRepo insuranceRepo;

    @Autowired
    public InsuranceUploader(InsuranceRepo insuranceRepo) {
        this.insuranceRepo = insuranceRepo;

        System.out.println("[InsuranceUploader] Started insurance uploader sync process");
        
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("[InsuranceUploader] Sleep error, " + e);
                }

                System.out.println("[InsuranceUploader] Checking for unsynced insurance records...");
                
                List<Insurance> unsyncedInsurances = insuranceRepo.findByStatus(Insurance.InsuranceStatus.UPLOAD);

                System.out.println("[InsuranceUploader] Found " + unsyncedInsurances.size() 
                        + " insurance record(s) requiring Dentrix sync.");

                for (int i = 0; i < unsyncedInsurances.size(); i++) {
                    Insurance insurance = unsyncedInsurances.get(i);
                    Long insuranceId = insurance.getId();

                    System.out.println("[InsuranceUploader] Processing insurance " 
                            + (i + 1) + "/" + unsyncedInsurances.size() 
                            + ", ID: " + insuranceId);

                    Path tempFile = null;
                    try {
                        // 1. Get associated User for first and last name
                        System.out.println("[InsuranceUploader] Finding associated user for insurance ID " + insuranceId + "...");
                        Optional<User> optionalUser = insuranceRepo.findUserByInsuranceId(insuranceId);
                        
                        if (optionalUser.isEmpty()) {
                            System.err.println("[InsuranceUploader] ERROR: Skipping insurance ID " + insuranceId + ": No associated user found.");
                            continue;
                        }
                        User user = optionalUser.get();
                        System.out.println("[InsuranceUploader] Associated user found: " + user.getFirstName() + " " + user.getLastName());

                        // 2. Save the PDF bytes to a temp file and note the path
                        byte[] pdfBytes = insurance.getPdfFile();
                        if (pdfBytes == null || pdfBytes.length == 0) {
                            System.err.println("[InsuranceUploader] ERROR: Skipping insurance ID " + insuranceId + ": PDF file is null or empty.");
                            continue;
                        }

                        tempFile = Files.createTempFile("insurance_", ".pdf");
                        Files.write(tempFile, pdfBytes);
                        String filePath = tempFile.toAbsolutePath().toString();
                        System.out.println("[InsuranceUploader] Saved PDF to temporary file: " + filePath);

                        // 3. Call DentrixAction.run
                        Map<String, String> params = Map.of(
                            "ptFirstName", user.getFirstName(),
                            "ptLastName", user.getLastName(),
                            "filePath", filePath,
                            "docType", "Patient Insurance",
                            "docName", "Automated Insurance Breakdown",
                            "docNotes", "Generated and uploaded by auto insurance bot"
                        );

                        System.out.println("[InsuranceUploader] Calling DentrixAction.run('uploadToChart')...");
                        DentrixAction.ReturnData result = DentrixAction.run("uploadToChart", params);

                        // 4. Handle return code
                        if (result.returnCode != 0) {
                            System.err.println("[InsuranceUploader] ERROR: Failed to upload insurance ID " + insuranceId 
                                + " to Dentrix. Return code: " + result.returnCode 
                                + " | Stderr: " + result.stderr);
                        } else {
                            // Success: update timestamp and save
                            insurance.markAsComplete();
                            insuranceRepo.save(insurance);
                            System.out.println("[InsuranceUploader] Successfully synced insurance ID " + insuranceId + " with Dentrix.");
                        }

                    } catch (Exception e) {
                        System.err.println("[InsuranceUploader] ERROR processing insurance ID: " + insuranceId + ": " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        // Clean up temp file
                        if (tempFile != null) {
                            try {
                                Files.deleteIfExists(tempFile);
                            } catch (Exception ignored) {}
                        }
                    }

                    System.out.println("[InsuranceUploader] Finished processing insurance ID: " + insuranceId);
                }
            }
        }).start();
    }
}
