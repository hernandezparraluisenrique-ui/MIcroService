package ws.beauty.salon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AppointmentNoteRequestDTO {
    @JsonProperty("idNote")
    private Integer idNote;

    @JsonProperty("appointmentId")
    private Integer appointmentId;

    @JsonProperty("noteText")
    private String noteText;
}
