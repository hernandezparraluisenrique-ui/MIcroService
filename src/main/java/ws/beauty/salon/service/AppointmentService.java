package ws.beauty.salon.service;

import java.util.List;

import ws.beauty.salon.dto.AppointmentRequest;
import ws.beauty.salon.dto.AppointmentResponse;

public interface AppointmentService {
    List<AppointmentResponse> findAll();

    AppointmentResponse findById(Integer idAppointment);

    AppointmentResponse create(AppointmentRequest req);

    AppointmentResponse update(Integer idAppointment, AppointmentRequest req);

    void delete(Integer idAppointment);
}
