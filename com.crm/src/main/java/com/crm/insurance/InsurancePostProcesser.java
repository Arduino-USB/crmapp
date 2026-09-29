package com.crm.insurance;

import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;

import com.crm.repos.UserRepo;
import com.crm.repos.InsuranceRepo;
import com.crm.repos.AppointmentRepo;
import com.crm.entites.User;
import com.crm.entites.Appointment; 
import com.crm.entites.Insurance;
import com.crm.webscrape.WebReturns;
import com.crm.DentrixAction;

import java.time.Instant;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.Period;

import java.util.Optional;
import java.time.ZoneId;

@Component
public class InsurancePostProcesser {
	private InsuranceRepo insuranceRepo;
	private UserRepo userRepo;
	private AppointmentRepo appointmentRepo;

	private List<EligibilyData> elgList = new ArrayList<>();

	public static class EligibilyData {

		private String dentalCode;
		private FreqParser.Freq freq;
		private List<String> history;

		public EligibilyData(String dentalCode, FreqParser.Freq freq, List<String> history) {
			
			this.dentalCode =dentalCode;
			this.freq = freq;
			this.history =	history;
			
	
		}
	}

	private static List<String> extractVariables(WordprocessingMLPackage document) {
		try {
		    // 1. Normalize split XML runs so variables are contiguous
		    VariablePrepare.prepare(document);
		    
		    // 2. Convert the main document part to a raw XML string
		    String xml = XmlUtils.marshaltoString(document.getMainDocumentPart().getJaxbElement());
		    
		    // 3. Strip out all XML tags leaving just the plain text
		    String plainText = xml.replaceAll("<[^>]*>", " ");
		    
		    // 4. Match all ${variableName} patterns
		    Matcher matcher = Pattern.compile("\\$\\{([^}]+)\\}").matcher(plainText);
		    Set<String> variables = new LinkedHashSet<>(); // Preserves order and filters duplicates
		    
		    while (matcher.find()) {
		        variables.add(matcher.group(1).trim());
		    }
		    
		    return new ArrayList<>(variables);
		} catch (Exception e) {
		    throw new RuntimeException("Failed to extract template variables", e);
		}
	}


	private Map<String, String> convertToMap(WebReturns webReturns) {
        Map<String, String>    finalMap = new HashMap<>();    
        Map<String, List<String>> lists = webReturns.getLists();
        
        String[] listsKeys = lists.keySet().toArray(new String[0]);

        for (int i = 0; i < listsKeys.length; i++) {
            String k = listsKeys[i];
            if (k != null) {
                finalMap.put(k.trim(), String.join(", ", lists.get(k)).trim());
            }
        }
        
        // I think its safe it ignore the keyName we dont need it at leats for delta we dont. We can always update this part
        
        Map<String, Map<String, String>> maps = webReturns.getMaps();

        String[] mapsKeys = maps.keySet().toArray(new String[0]);

        for (int i = 0; i < mapsKeys.length; i++) {
            String key = mapsKeys[i];
            Map<String, String> map = maps.get(key);

            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String subKey = entry.getKey();
                    if (subKey != null) {
                        finalMap.put(subKey.trim(), entry.getValue());
                    }
                }
            }
        }        
    
        Map<String, String> strings = webReturns.getStrings();
        if (strings != null) {
            for (Map.Entry<String, String> entry : strings.entrySet()) {
                String subKey = entry.getKey();
                if (subKey != null) {
                    finalMap.put(subKey.trim(), entry.getValue());
                }
            }
        }
    
        return finalMap;    
    }
	
	
private Map<String, String> postProcessVars(Map<String, String> vMap) {
    Map<String, String> varsMap = new HashMap<>(vMap);
    
    // First loop: handle prefixed keys (like freqD1110,D4910 or hxD0330)
    String[] varsKeys = varsMap.keySet().toArray(new String[0]);
    for (int i = 0; i < varsKeys.length; i++) {
        String currentKey = varsKeys[i];
        
        if (!currentKey.contains("D")) {
            continue;
        }
        
        int dIndex = currentKey.indexOf("D");
        
        // Skip keys that start directly with 'D' so we don't delete them here
        if (dIndex == 0) {
            continue;
        }
        
        // Ensure there are at least 4 characters after 'D' and they are all numbers (e.g., D0274)
        String afterD = currentKey.substring(dIndex + 1);
        if (afterD.length() < 4 || 
            !Character.isDigit(afterD.charAt(0)) || 
            !Character.isDigit(afterD.charAt(1)) || 
            !Character.isDigit(afterD.charAt(2)) || 
            !Character.isDigit(afterD.charAt(3))) {
            continue; // Safely ignores effectiveDate, maxiumDeductible, etc.
        }
        
        String baseName = currentKey.substring(0, dIndex);
        String[] dentalCodes = currentKey.substring(dIndex).split(",");
    
        for (int k = 0; k < dentalCodes.length; k++) {
            varsMap.put(baseName + dentalCodes[k], varsMap.get(currentKey));    
        }
        
        varsMap.remove(currentKey);
    }
    
    // Second loop: prepend "cov" to standalone dental codes and clean non-integer values
    varsKeys = varsMap.keySet().toArray(new String[0]);
    
    for (int i = 0; i < varsKeys.length; i++) {
        String currentKey = varsKeys[i];
        
        if (currentKey.length() > 0 && currentKey.charAt(0) == 'D') {
            String newKey = "cov" + currentKey;
            String val = varsMap.get(currentKey);
            
            if (val != null) {
                boolean isInt = true;
                try {
                    Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    isInt = false;
                }
                
                if (!isInt) {
                    StringBuilder digitsOnly = new StringBuilder();
                    for (int c = 0; c < val.length(); c++) {
                        char ch = val.charAt(c);
                        if (Character.isDigit(ch)) {
                            digitsOnly.append(ch);
                        }
                    }
                    val = digitsOnly.length() > 0 ? digitsOnly.toString() : "0";
                }
            } else {
                val = "0";
            }
            
            varsMap.put(newKey, val);
            varsMap.remove(currentKey);
        }
    }
    
    return varsMap;
}

	public Map<String, String> shortenFreq(Map<String, String> varsMap) {
        Map<String, String> result = new HashMap<>(varsMap);
        String[] varsKeys = result.keySet().toArray(new String[0]);

        for (int i = 0; i < varsKeys.length; i++) {
            String currentKey = varsKeys[i];

            if (!currentKey.startsWith("freq")) {
                continue;
            }

            String currentValue = result.get(currentKey);
            FreqParser.Freq freq = FreqParser.parse(currentValue);

            // If freq is null, skip adding it entirely
            if (freq == null) {
                continue;
            }

            // Shorten the map value
            result.put(currentKey, freq.toString());

            // Extract dental code (e.g., "freqD0380" -> "D0380")
            String dentalCode = currentKey.substring(4); 

            // Find matching history key (e.g., "hxD0380")
            String hxKey = "hx" + dentalCode;
            List<String> historyList = new ArrayList<>();
            
            if (result.containsKey(hxKey)) {
                String hxValue = result.get(hxKey);
                if (hxValue != null && !hxValue.isEmpty() && !hxValue.equals("N/A")) {
                    for (String item : hxValue.split(",")) {
                        historyList.add(item.trim());
                    }
                }
            }

            // Populate the class-level elgList
            this.elgList.add(new EligibilyData(dentalCode, freq, historyList));
        }

        return result;
    }

	private static Map<String, String> populateCombinedVariables(Map<String, String> vMap, List<String> docVariables) {
		Map<String, String> resolvedMap = new HashMap<>(vMap);

		for (String docVar : docVariables) {
		    // 1. If the exact variable is already in the map, skip
		    if (resolvedMap.containsKey(docVar)) {
		        continue;
		    }

		    // 2. Handle combined/comma-separated variables (e.g., covD4341,D4342 or covD4341,4342)
		    if (docVar.contains(",")) {
		        int dIndex = docVar.indexOf("D");
		        if (dIndex > 0) {
		            String baseName = docVar.substring(0, dIndex); // e.g., "cov"
		            String[] parts = docVar.substring(dIndex).split(",");
		            
		            String resolvedValue = null;

		            for (String part : parts) {
		                part = part.trim();
		                // Ensure the sub-key correctly includes 'D' if omitted in the second segment
		                String subKey = part.startsWith("D") ? baseName + part : baseName + "D" + part;
		                
		                if (resolvedMap.containsKey(subKey)) {
		                    resolvedValue = resolvedMap.get(subKey);
		                    break; // Pick the first available value and stop searching
		                }
		            }

		            if (resolvedValue != null) {
		                resolvedMap.put(docVar, resolvedValue);
		                continue;
		            }
		        }
		    }

		    // 3. If it still can't be resolved, default it to "N/A"
		    resolvedMap.put(docVar, "N/A");
		}

		return resolvedMap;
	}

	private static void checkForUndefinedVars(Map<String, String> resolvedMap, List<String> docVariables) {
		List<String> missingVars = new ArrayList<>();
		
		for (String docVar : docVariables) {
		    if (!resolvedMap.containsKey(docVar) || "N/A".equals(resolvedMap.get(docVar))) {
		        missingVars.add(docVar);
		    }
		}
		
		if (!missingVars.isEmpty()) {
		    System.out.println("Warning: the following variables were not defined and defaulted to 'N/A': \n" + missingVars);
		}
	}
/*
Warning: the following variables were not defined and defaulted to 'N/A': [apptDate, effectiveDate, ptFirstName, ptLastName, ptDob, piFirstName, piLastName, piDob, ptMemberId, maxBenefit, deductible, deductibleMet, compDate, remainingBenefit, insName, freqD0380, hxD0380, elgD0380, elgD4341,D4342, freq4346, hxD4346, elgD4346, hxD4910, elgD4910, freqD4355, hxD4355, elgD4355, hxD0180, elgD0180, hxD9310, elgD9310, freqD0274, hxD0274, elgD0274, elgD0330,D0210, benefitsPayedTo, missingToothClause, waitingPeriods, waitingPeriodsMet, cliamsAddress, phoneNumber]
*/


	public Map<String, String> createElgKeys(Map<String, String> varsMap) {
		Map<String, String> result = new HashMap<>(varsMap);
		
		// 1. Get the date format, default to MM/dd/yyyy if not provided
		String dateFormatStr = result.getOrDefault("__hxDateFormat", "MM/dd/yyyy");
		DateTimeFormatter formatter;
		try {
		    formatter = DateTimeFormatter.ofPattern(dateFormatStr);
		} catch (Exception e) {
		    formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		}
		
		LocalDate today = LocalDate.now();

		// 2. Loop through each item in the populated elgList
		for (EligibilyData data : this.elgList) {
		    String code = data.dentalCode;
		    FreqParser.Freq freq = data.freq;
		    List<String> history = data.history;

		    if (freq == null || history == null || history.isEmpty()) {
		        result.put("elg" + code, "N/A");
		        continue;
		    }

		    // Count how many history dates fall within the allowed period
		    int validCount = 0;
		    Period period = freq.getPeriod();

		    for (String dateStr : history) {
		        try {
		            LocalDate hxDate = LocalDate.parse(dateStr.trim(), formatter);
		            
		            if (period == null) {
		                // Lifetime frequency restriction
		                validCount++;
		            } else {
		                LocalDate cutoffDate = today.minus(period);
		                if (!hxDate.isBefore(cutoffDate)) {
		                    validCount++;
		                }
		            }
		        } catch (Exception e) {
		            // Skip unparseable date strings gracefully
		        }
		    }

		    // Determine eligibility (Adjust condition based on whether hitting the limit allows or blocks)
		    boolean isEligible = validCount < freq.getTimes(); 
		    
		    // Put "Yes" or "No" into the map instead of true/false
		    result.put("elg" + code, isEligible ? "Yes" : "No");
		}

		result.put("compDate", LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		
		if (result.containsKey("maxiumDeductible") && result.containsKey("remainingDeductible")) {
			Integer maxiumDeductible = Integer.parseInt(result.get("maxiumDeductible").replaceAll("\\D", ""));	
			Integer remainingDeductible = Integer.parseInt(result.get("remainingDeductible").replaceAll("\\D", ""));				
	
			if (remainingDeductible <= 0) {
				result.put("deductibleMet", "Yes");
			} else {
				result.put("deductibleMet", "No ( $" + remainingDeductible + " left");
			}
		}	

		if (result.containsKey("inNetwork")) {		
	
			if (result.get("inNetwork").toLowerCase().equals("yes")) {
			
				result.put("benefitsPayedTo", "Provider");
			
			} else if (result.get("inNetwork").toLowerCase().equals("no")) {
				result.put("benefitsPayedTo", "Subscriber");
			}

		}

		//benefitsPayedTo
		return result;
	}

	public Map<String, String> addPtSlotInfo(Long insId, Map<String, String> varsMap) {
		Map<String, String> result = new HashMap<>(varsMap);

		// 1. Retrieve the Insurance record
		Insurance insurance = insuranceRepo.findById(insId).orElse(null);
		if (insurance == null) {
		    return result;
		}

		// 2. Find the patient (User) linked to this insurance file (Primary or Secondary)
		User user = userRepo.findByPrimaryInsuranceFileId(insId)
		        .orElseGet(() -> userRepo.findBySecondaryInsuranceFileId(insId).orElse(null));

		if (user == null) {
		    return result;
		}

		// 3. Populate Patient (pt*) details
		result.put("ptFirstName", user.getFirstName() != null ? user.getFirstName() : "N/A");
		result.put("ptLastName", user.getLastName() != null ? user.getLastName() : "N/A");
		result.put("ptDob", user.getDob() != null ? user.getDob().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "N/A");

		// 4. Determine the correct slot (Primary vs Secondary Insured pi*)
		boolean isPrimarySlot = user.getPrimaryInsuranceFile() != null 
		                        && user.getPrimaryInsuranceFile().getId().equals(insId);

		if (isPrimarySlot) {
            result.put("piFirstName", user.getPrimarySubFirstName() != null ? user.getPrimarySubFirstName() : "N/A");
            result.put("piLastName", user.getPrimarySubLastName() != null ? user.getPrimarySubLastName() : "N/A");
            result.put("piDob", user.getPrimarySubDob() != null ? user.getPrimarySubDob().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "N/A");
            result.put("piMemberId", user.getPrimarySubMemberId() != null ? user.getPrimarySubMemberId() : "N/A");
        } else {
            result.put("piFirstName", user.getSecondarySubFirstName() != null ? user.getSecondarySubFirstName() : "N/A");
            result.put("piLastName", user.getSecondarySubLastName() != null ? user.getSecondarySubLastName() : "N/A");
            result.put("piDob", user.getSecondarySubDob() != null ? user.getSecondarySubDob().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "N/A");
            result.put("piMemberId", user.getSecondarySubMemberId() != null ? user.getSecondarySubMemberId() : "N/A");
        }

		// 6. Populate Appointment Date (apptDate) from AppointmentRepo based on patient ID
		Appointment appointment = appointmentRepo.findFirstByUserIdOrderByTimeDesc(user.getId()).orElse(null);
		if (appointment != null && appointment.getTime() != null) {
		    LocalDate apptLocalDate = LocalDate.ofInstant(appointment.getTime(), ZoneId.systemDefault());
		    result.put("apptDate", apptLocalDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
		} else {
		    result.put("apptDate", "N/A");
		}

		return result;
	}
	public InsurancePostProcesser(InsuranceRepo insuranceRepo, UserRepo userRepo, AppointmentRepo appointmentRepo){
		this.insuranceRepo = insuranceRepo;
		this.userRepo = userRepo;
		this.appointmentRepo = appointmentRepo;		

		System.out.println("[InsurancePostProcesser] Started insurace PDF proccess");
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					System.out.println("[InsurancePostProcesser] Sleep error, " + e);
				}

				System.out.println("[InsurancePostProcesser] Checking for unsynced insurance records...");

				List<Insurance> insurances = insuranceRepo.findByStatus(Insurance.InsuranceStatus.POSTPROCESS);

				System.out.println("[InsurancePostProcesser] Found " + insurances.size()
						+ " insurance record(s) requiring PDF processing.");

				for (int i = 0; i < insurances.size(); i++) {
					Insurance insurance = insurances.get(i);

					System.out.println("[InsurancePostProcesser] Processing insurance "
							+ (i + 1) + "/" + insurances.size()
							+ ", ID: " + insurance.getId());

					try {

						System.out.println("[InsurancePostProcesser] Getting raw insurance data...");
						WebReturns webReturns = insurance.getRawInsuranceData();
						System.out.println("[InsurancePostProcesser] Raw insurance data retrieved.");
						ApplicationHome home = new ApplicationHome(DentrixAction.class);

						if (home.getSource() == null) {
							System.out.println("[InsurancePostProcesser] ERROR: Unable to locate "
									+ "application home source path.");

							throw new IllegalStateException(
								"Unable to locate application home source path."
							);
						}

						Path jarPath = home.getSource().toPath();
						Path jarDir = jarPath.getParent();

						Path templatePath = jarDir.resolve("template.docx");

						System.out.println("[InsurancePostProcesser] Template path: "
								+ templatePath);

						if (!Files.exists(templatePath)) {
							System.out.println("[InsurancePostProcesser] ERROR: Template does not exist: "
									+ templatePath);

							throw new IllegalStateException(
								"Template does not exist: " + templatePath
							);
						}

						System.out.println("[InsurancePostProcesser] Loading DOCX template...");
						WordprocessingMLPackage document = WordprocessingMLPackage.load(templatePath.toFile());

						System.out.println("[InsurancePostProcesser] Converting insurance data to map...");
			
						List<String> docVars = extractVariables(document);	
	
						Map<String, String> processedVars = addPtSlotInfo(insurance.getId(), createElgKeys(shortenFreq(populateCombinedVariables(postProcessVars(convertToMap(webReturns)), docVars))));
						

						System.out.println("[InsurancePostProcesser] Preparing DOCX variables...");
						VariablePrepare.prepare(document);

						System.out.println("[InsurancePostProcesser] Replacing template variables...");
						document.getMainDocumentPart().variableReplace(processedVars);

						System.out.println("[InsurancePostProcesser] Creating PDF...");
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						Docx4J.toPDF(document, output);
						byte[] pdfFile = output.toByteArray();
						System.out.println("[InsurancePostProcesser] PDF generated successfully");

						insurance.setPdfFile(pdfFile);
						insuranceRepo.save(insurance);

						System.out.println("[InsurancePostProcesser] PDF assigned to insurance " + "record ID: " + insurance.getId());
						checkForUndefinedVars(processedVars, docVars);

					} catch (Exception e) {
						System.out.println("[InsurancePostProcesser] ERROR processing insurance " + "ID: " + insurance.getId() + ": " + e.getMessage());

						e.printStackTrace();
					}

					System.out.println("[InsurancePostProcesser] Finished processing insurance " + "ID: " + insurance.getId());
			
				}
			}
		}).start();
	}
}

