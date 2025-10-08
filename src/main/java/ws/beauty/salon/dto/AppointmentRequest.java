package ws.beauty.salon.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AppointmentRequest {

    @NotNull
    private LocalDateTime appointmentDatetime;
    @Size(max = 20)
    private String status = "pending";
    @NotNull
    private Integer idClient;

    private Integer idStylist;

    private Integer idService;
}