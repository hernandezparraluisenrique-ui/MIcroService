package ws.beauty.salon.dto;

import lombok.Data;

@Data
public class StylistRequestDTO {
    private Integer idStylist;
    private String firstName;
    private String lastName;
    private String specialty;
    private String workSchedule;
    private Boolean available;
}
