package com.crm;

import com.crm.repos.UserRepo;
import com.crm.repos.AppointmentRepo;
import com.crm.repos.ReminderRepo;

import com.crm.entites.Appointment;
import com.crm.entites.Reminder;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.Duration;
import java.util.List;


@Component
public class AutomatedTextMgr {

	private UserRepo userRepo;
	private AppointmentRepo appointmentRepo;
	private ReminderRepo reminderRepo;


	private boolean hasSentReminder(Appointment appointment, int milestone) {

		List<Integer> milestones = appointment.getMileStoneList();

		if (milestones == null) {
			return false;
		}

		return milestones.contains(milestone);
	}


	private void markReminderSent(Appointment appointment, int milestone) {

		appointment.addMileStoneToList(milestone);

		appointment.setTextSentNow();

		appointmentRepo.save(appointment);
	}


	public AutomatedTextMgr(
			UserRepo userRepo,
			AppointmentRepo appointmentRepo,
			ReminderRepo reminderRepo) {


		this.userRepo = userRepo;
		this.appointmentRepo = appointmentRepo;
		this.reminderRepo = reminderRepo;


		List<Appointment> appointments =
				appointmentRepo.findAppointmentPhoneNotBlank();


		List<Reminder> reminders =
				reminderRepo.findAll();


		Instant currentTime = Instant.now();


		for (int i = 0; i < appointments.size(); i++) {

			Appointment currentAppointment = appointments.get(i);


			Duration remainingTime =
					Duration.between(
						currentTime,
						currentAppointment.getTime()
					);


			// Appointment already happened
			if (remainingTime.compareTo(Duration.ZERO) < 0) {
				continue;
			}


			for (int j = 0; j < reminders.size(); j++) {

				Reminder currentReminder = reminders.get(j);


				int milestone =
						currentReminder.getDayMileStone();


				Duration milestoneDuration =
						Duration.ofDays(milestone);


				// Has appointment reached this milestone?
				if (remainingTime.compareTo(milestoneDuration) <= 0) {


					// Did we already send this milestone?
					if (!hasSentReminder(currentAppointment, milestone)) {


						System.out.println(
							"[AutomatedTextMgr] Sending "
							+ milestone
							+ " day reminder"
						);


						// send text here
						// sendText(currentAppointment,
						//          currentReminder.getReminder());


						markReminderSent(
							currentAppointment,
							milestone
						);


						break;
					}
				}
			}
		}
	}
}
