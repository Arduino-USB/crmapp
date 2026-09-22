package com.crm.insurance;

import com.crm.repos.UserRepo;
import com.crm.entites.User;
import com.crm.webscrape.WebScrape;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InsuranceBreakdown {

	private final UserRepo userRepo;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public InsuranceBreakdown(UserRepo userRepo) {
		this.userRepo = userRepo;

		System.out.println("[InsuranceBreakdown] Starting insurance breakdown process");

		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(10000);
				} catch (Exception e) {
					System.out.println("[InsuranceBreakdown] Sleep error: " + e);
				}

				List<User> users = userRepo.findUsersWithCompleteInsurance();

				for (User user : users) {

					// Process Primary Insurance
					if (user.getPrimaryInsuranceName() != null
							&& !user.getPrimaryInsuranceName().isBlank()
							&& user.getPrimaryInsuranceId() != null
							&& user.getPrimarySubFirstName() != null
							&& !user.getPrimarySubFirstName().isBlank()
							&& user.getPrimarySubLastName() != null
							&& !user.getPrimarySubLastName().isBlank()
							&& user.getPrimarySubDob() != null
							&& user.getPrimarySubMemberId() != null) {

						processInsurance(
								user.getPrimaryInsuranceName(),
								user.getPrimarySubMemberId(),
								user.getPrimarySubFirstName(),
								user.getPrimarySubLastName(),
								user.getPrimarySubDob(),
								user,
								"PRIMARY"
						);
					}

					// Process Secondary Insurance
					if (user.getSecondaryInsuranceName() != null
							&& !user.getSecondaryInsuranceName().isBlank()
							&& user.getSecondaryInsuranceId() != null
							&& user.getSecondarySubFirstName() != null
							&& !user.getSecondarySubFirstName().isBlank()
							&& user.getSecondarySubLastName() != null
							&& !user.getSecondarySubLastName().isBlank()
							&& user.getSecondarySubDob() != null
							&& user.getSecondarySubMemberId() != null) {

						processInsurance(
								user.getSecondaryInsuranceName(),
								user.getSecondarySubMemberId(),
								user.getSecondarySubFirstName(),
								user.getSecondarySubLastName(),
								user.getSecondarySubDob(),
								user,
								"SECONDARY"
						);
					}
				}
			}
		}).start();
	}

	private void processInsurance(
			String insuranceName,
			Long insuranceId,
			String subscriberFirstName,
			String subscriberLastName,
			java.time.LocalDate subscriberDob,
			User user,
			String insuranceType) {

		try {
			ApplicationHome home = new ApplicationHome(InsuranceBreakdown.class);

			if (home.getSource() == null) {
				throw new IllegalStateException(
						"Unable to locate application home source path."
				);
			}

			Path jarPath = home.getSource().toPath();
			Path jarDir = jarPath.getParent();
			Path supportedInsuranceDir = jarDir.resolve("Supported Insurance");

			if (!Files.exists(supportedInsuranceDir)
					|| !Files.isDirectory(supportedInsuranceDir)) {
				return;
			}

			String targetInsLower = insuranceName.toLowerCase().trim();

			// Loop through folders inside "Supported Insurance"
			try (DirectoryStream<Path> stream =
					Files.newDirectoryStream(supportedInsuranceDir, Files::isDirectory)) {

				for (Path companyFolder : stream) {
					String folderNameLower =
							companyFolder.getFileName().toString().toLowerCase().trim();

					// Check if insurance name contains the folder name
					if (targetInsLower.contains(folderNameLower)) {

						// Look for JSON files inside the matched company folder
						try (DirectoryStream<Path> fileStream =
								Files.newDirectoryStream(companyFolder, path -> {

									String fileName =
											path.getFileName().toString().toLowerCase();

									return fileName.endsWith(".json")
											&& !fileName.endsWith(".vars");
								})) {

							for (Path jsonFile : fileStream) {

								Path varsFile =
										companyFolder.resolve(
												jsonFile.getFileName().toString() + ".vars"
										);

								Map<String, String> loadedVars = new HashMap<>();

								if (Files.exists(varsFile)) {
									try {
										loadedVars = objectMapper.readValue(
												varsFile.toFile(),
												new TypeReference<Map<String, String>>() {}
										);
									} catch (Exception e) {
										System.out.println(
												"[InsuranceBreakdown] Failed to parse vars file: "
												+ varsFile.getFileName()
												+ " -> "
												+ e.getMessage()
										);
										continue;
									}
								}

								DateTimeFormatter formatter =
										DateTimeFormatter.ofPattern("MM/dd/yyyy");

								Map<String, String> mergedParams =
										new HashMap<>(loadedVars);

								// Patient information
								mergedParams.put(
										"ptFirstName",
										user.getFirstName() != null
												? user.getFirstName()
												: ""
								);

								mergedParams.put(
										"ptLastName",
										user.getLastName() != null
												? user.getLastName()
												: ""
								);

								mergedParams.put(
										"ptDob",
										user.getDob() != null
												? user.getDob().format(formatter)
												: ""
								);

								// Insurance subscriber information
								mergedParams.put(
										"piFirstName",
										subscriberFirstName != null
												? subscriberFirstName
												: ""
								);

								mergedParams.put(
										"piLastName",
										subscriberLastName != null
												? subscriberLastName
												: ""
								);

								mergedParams.put(
										"piDob",
										subscriberDob != null
												? subscriberDob.format(formatter)
												: ""
								);

								mergedParams.put(
										"piMemberId",
										String.valueOf(insuranceId)
								);

								try {
									WebScrape ws =
											new WebScrape(jsonFile, mergedParams);

									Object result = ws.getReturnData();

									System.out.println(
											"[InsuranceBreakdown] "
											+ insuranceType
											+ " success for user ID "
											+ user.getId()
											+ " using script "
											+ jsonFile.getFileName()
											+ ". Result: "
											+ result
									);

									// Successfully executed and got data,
									// stop trying other files for this pair.
									return;

								} catch (Exception scrapeException) {
									// Failed execution, try the next file.
									System.out.println(
											"[InsuranceBreakdown] "
											+ insuranceType
											+ " script "
											+ jsonFile.getFileName()
											+ " failed, trying next. Error: "
											+ scrapeException.getMessage()
									);
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println(
					"[InsuranceBreakdown] Error processing "
					+ insuranceType
					+ " insurance for user ID "
					+ user.getId()
					+ ": "
					+ e.getMessage()
			);
		}
	}
}
