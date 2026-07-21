package com.crm;

import com.crm.repos.UserRepo;
import com.crm.entites.User;
import com.crm.repos.AppointmentRepo;
import com.crm.entites.Appointment;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.Locale;                        
import java.io.File;                             
import java.nio.file.*;
import java.io.InputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;       


@Component
public class DentrixSync {
	
	private Path csvFolder =  FileSystems.getDefault().getRootDirectories().iterator().next().resolve("Schedules"); // C:\Schedules or /Schedules
	private static boolean instance_created = false;
	
	private UserRepo userRepo;
	private AppointmentRepo appointmentRepo;
	private int daysAhead = 10;	
	
	private ArrayList<FileData> fileCheckSums = new ArrayList<>();	
	
	public class FileData {
		Path filepath;
		Long checksum;		
	}
	
	public static class CsvTableMap {
		static final String id = "c1";
		static final String name = "c2";
		static final String time = "c3";
		static final String amount = "c4";
		static final String prefName = "c5";
		static final String reason = "c6";
		static final String phone = "c7";
	}
		

	public void updateFileData(Path filePath, long checkSum) {
		for (FileData data : fileCheckSums) {
		    if (data.filepath != null && data.filepath.equals(filePath)) {
		        data.checksum = checkSum; // Update existing
		        return;
		    }
		}
		
		// Add new if it doesn't exist
		FileData newData = new FileData();
		newData.filepath = filePath;
		newData.checksum = checkSum;
		fileCheckSums.add(newData);
	}


	private void parseCsvToDb(Path csvFile, LocalDate currentDate) {
		CsvMapper csvMapper = new CsvMapper();

		CsvSchema schema = CsvSchema.builder()
				.addColumn("c0")
				.addColumn("c1")
				.addColumn("c2")
				.addColumn("c3")
				.addColumn("c4")
				.addColumn("c5")
				.addColumn("c6")
				.addColumn("c7")
				.setColumnSeparator('\t')
				.build();

		try (InputStream input = Files.newInputStream(csvFile)) {

			MappingIterator<Map<String, String>> iterator = csvMapper
					.readerFor(Map.class)
					.with(schema)
					.readValues(input);

			List<Map<String, String>> rows = new ArrayList<>();

			while (iterator.hasNext()) {

				Map<String, String> row = iterator.next();

				rows.add(row);

				System.out.println("[DentrixSync] Row data: " + row);

				// Skip blank rows
				boolean blank = true;
				for (String value : row.values()) {
					if (value != null && !value.trim().isEmpty()) {
						blank = false;
						break;
					}
				}

				if (blank)
					continue;

				long userId = Long.parseLong(row.get(CsvTableMap.id));

				Optional<User> user = userRepo.findById(userId);

				if (!user.isPresent()) {

					System.out.println("[DentrixSync] Adding user '"
							+ row.get(CsvTableMap.name)
							+ "' to database");

					User newUser = new User();

					newUser.setId(userId);
					newUser.setName(row.get(CsvTableMap.name));
					newUser.setPrefName(row.get(CsvTableMap.prefName));
					newUser.setPhone(row.get(CsvTableMap.phone));

					userRepo.save(newUser);
				}

				LocalTime localTime = LocalTime.parse(
						row.get(CsvTableMap.time),
						DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
				);

				Instant timeInstant = LocalDateTime
						.of(currentDate, localTime)
						.atZone(ZoneId.systemDefault())
						.toInstant();

				Optional<Appointment> existingAppointment =
						appointmentRepo.findByUserIdAndTime(userId, timeInstant);

				if (!existingAppointment.isPresent()) {

					System.out.println("[DentrixSync] Adding appointment"
							+ "\n\tfor user '" + row.get(CsvTableMap.name) + "'"
							+ "\n\tat time '" + row.get(CsvTableMap.time) + "'"
							+ "\n\tfor reason '" + row.get(CsvTableMap.reason) + "'"
					);

					Appointment newAppointment = new Appointment();

					newAppointment.setUserId(userId);
					newAppointment.setTime(timeInstant);
					newAppointment.setReason(row.get(CsvTableMap.reason));
					newAppointment.setCsvStatus(true);

					appointmentRepo.save(newAppointment);
				}
			}

			// Remove appointments that no longer exist in the CSV
			List<Appointment> appointments = appointmentRepo.findByDay(currentDate);

			for (Appointment dbAppointment : appointments) {

				if (!dbAppointment.fromCsvFile())
					continue;

				boolean found = false;

				for (Map<String, String> csvRow : rows) {

					// Skip blank rows
					boolean blank = true;
					for (String value : csvRow.values()) {
						if (value != null && !value.trim().isEmpty()) {
							blank = false;
							break;
						}
					}

					if (blank)
						continue;

					long csvUserId = Long.parseLong(csvRow.get(CsvTableMap.id));

					LocalTime csvLocalTime = LocalTime.parse(
							csvRow.get(CsvTableMap.time),
							DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
					);

					Instant csvTimeInstant = LocalDateTime
							.of(currentDate, csvLocalTime)
							.atZone(ZoneId.systemDefault())
							.toInstant();

					String csvReason = csvRow.get(CsvTableMap.reason);

					if (dbAppointment.getUserId() == csvUserId
							&& dbAppointment.getTime().equals(csvTimeInstant)
							&& dbAppointment.getReason().equals(csvReason)) {

						found = true;
						break;
					}
				}

				if (!found) {
					System.out.println("[DentrixSync] Removing cancelled appointment for user "
							+ dbAppointment.getUserId());

					appointmentRepo.delete(dbAppointment);
				}
			}

		} catch (Exception e) {

			System.out.println("[DentrixSync] Error parsing csv '"
					+ csvFile + "'");

			e.printStackTrace();
		}
	}

	private Long getStoredCheckSum(Path csvFile) {
		for (int i = 0; i < fileCheckSums.size(); i++) {
			if (fileCheckSums.get(i).filepath.equals(csvFile)) {
				return fileCheckSums.get(i).checksum;
			}						
		}
		
		return null;
			
	}

	public DentrixSync(UserRepo userRepo, AppointmentRepo appointmentRepo) {
		if (instance_created) {
			System.out.println("[DentrixSync] the DentrixSync module is meant to only have one instance and then be used as a static utility");
			return;
		} else {
			instance_created = true;
			this.userRepo = userRepo;
			this.appointmentRepo = appointmentRepo;
		}
		
		new Thread(() -> {
			System.out.println("[DentrixSync] Starting Dentrix schedule sync");
			
			while (true) {
				try {Thread.sleep(5000);} catch (Exception e) {
					System.out.println("[DentrixSync] Sleep error, " + e);					
				}
				
				for (int i = 0; i < daysAhead; i++) {
					LocalDate currentDate = LocalDate.now().plusDays(i);
					String currentDateFormatted = currentDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
					System.out.println("[DentrixSync] Getting schedule for " + currentDateFormatted + ", which is " + i + " day(s) ahead of today");
					try {Thread.sleep(1000);} catch (Exception e) {
						System.out.println("[DentrixSync] Sleep error, " + e);					
					}
					
										
					if(!Files.exists(csvFolder.resolve("getSchedule.exe"))) {
						System.out.println("[DentrixSync].Error getSchedule.exe not found in '" + csvFolder.toString() + "'");	
						return;					
					}									
	
					int returnCode = CmdExec.run("cmd", "/c", csvFolder.resolve("getSchedule.exe").toString(), currentDateFormatted).returnCode;
					
					if (returnCode == 0) {
						String csvFileName = currentDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")) + ".csv";
						Path csvFile = csvFolder.resolve(csvFileName);
						System.out.println("[DentrixSync] getSchedule.exe was successful, checking for CSV new or updated file '" + csvFile.toString() + "'");
						
						
						if (Files.exists(csvFile)) {
							try {
								Long newCheckSum = FileCheckSum.getCheckSum(csvFile);
								Long storedCheckSum = getStoredCheckSum(csvFile);
								if (storedCheckSum == null || storedCheckSum != newCheckSum) {
									System.out.println("[DentrixSync] File changed/added, parsing and adding to database");
									updateFileData(csvFile, newCheckSum);
									parseCsvToDb(csvFile, currentDate);
								} 
							} catch (Exception e) {
								System.out.println("[DentrixSync].Error Checksum reading issue, " + e);
							}

						} else {
							System.out.println("[DentrixSync].Error For some reason the file, " + csvFile.toString() + " doesn't exist even though it returned a status code of 0");
						}
					} else if (returnCode == 10) {
						System.out.println("[DentrixSync] It seems as the day, '" + currentDate.toString() + "' has no active schedule");
					} else {
						System.out.println("[DentrixSync].Error getSchedule.exe failed with code " + returnCode);				
					}
					
				}
							
			}
		}).start();
		
	}

}
