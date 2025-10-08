package ws.beauty.salon.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appointment_notes")
public class AppointmentNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idNote")
    @JsonProperty("idNote")
    private Integer id;

    // Relación con Appointment
    @ManyToOne
    @JoinColumn(name = "idAppointment", referencedColumnName = "idAppointment", nullable = false)
    @JsonProperty("appointment")
    @JsonBackReference
    private Appointment appointment;

    @Column(name = "noteText", columnDefinition = "TEXT")
    @JsonProperty("noteText")
    private String noteText;
}
