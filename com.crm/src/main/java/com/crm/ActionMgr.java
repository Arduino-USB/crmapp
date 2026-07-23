package com.crm;

import com.crm.entites.Appointment;
import com.crm.enums.ResponseAction;
import com.crm.repos.AppointmentRepo;

import org.springframework.stereotype.Component;

@Component
public class ActionMgr {

	private AppointmentRepo appointmentRepo;

	public ActionMgr(
			AppointmentRepo appointmentRepo) {

		this.appointmentRepo = appointmentRepo;
	}

	public void doAction(
			ResponseAction action,
			Appointment appointment) {

		switch (action) {

		case NONE:
			break;

		case CONFIRM_APPOINTMENT:
			//appointment.setConfirmed(true);
			//appointmentRepo.save(appointment);
			break;

		case CANCEL_APPOINTMENT:
			//appointment.setCancelled(true);
			//appointmentRepo.save(appointment);
			break;

		case STOP_TEXTING:
			//appointment.setStopTexting(true);
			//appointmentRepo.save(appointment);
			break;
		}
	}
}
