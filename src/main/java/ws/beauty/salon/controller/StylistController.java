package ws.beauty.salon.controller;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ws.beauty.salon.dto.StylistRequestDTO;
import ws.beauty.salon.model.Stylist;
import ws.beauty.salon.service.StylistService;

@RestController
@RequestMapping("stylists")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@Tag(name = "Stylists", description = "Provides methods for managing stylists")
public class StylistController {

    @Autowired
    private StylistService service;

    @Autowired
    private ModelMapper modelMapper;

    // Obtener todos los estilistas
    @Operation(summary = "Get all stylists")
    @ApiResponse(responseCode = "200", description = "Found stylists",
        content = @Content(mediaType = "application/json",
        array = @ArraySchema(schema = @Schema(implementation = Stylist.class))))
    @GetMapping
    public List<Stylist> getAll() {
        return service.getAll();
    }

    // Obtener todos con paginación
    @Operation(summary = "Get all stylists with pagination")
    @GetMapping(value = "pagination", params = { "page", "pageSize" })
    public List<Stylist> getAllPaginated(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return service.getAll(page, pageSize);
    }

    // Obtener estilistas ordenados por apellido
    @Operation(summary = "Get all stylists ordered by last name")
    @ApiResponse(responseCode = "200", description = "Found stylists",
        content = @Content(mediaType = "application/json",
        array = @ArraySchema(schema = @Schema(implementation = Stylist.class))))
    @GetMapping("orderByLastName")
    public List<Stylist> getAllOrderByLastName() {
        return service.getAllOrderByLastName();
    }

    // Obtener estilista por ID
    @Operation(summary = "Get a stylist by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stylist found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Stylist.class))),
        @ApiResponse(responseCode = "404", description = "Stylist not found", content = @Content)
    })
    @GetMapping("{idStylist}")
    public ResponseEntity<Stylist> getById(@PathVariable Integer idStylist) {
        Optional<Stylist> stylist = service.getById(idStylist);
        return stylist.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Buscar estilistas por especialidad
    @Operation(summary = "Get all stylists by specialty")
    @ApiResponse(responseCode = "200", description = "Found stylists",
        content = @Content(mediaType = "application/json",
        array = @ArraySchema(schema = @Schema(implementation = Stylist.class))))
    @GetMapping("/search/{specialty}")
    public List<Stylist> getStylistsBySpecialty(@PathVariable String specialty) {
        return service.getStylistsBySpecialty(specialty);
    }

    // Obtener estilistas disponibles
    @Operation(summary = "Get available stylists")
    @ApiResponse(responseCode = "200", description = "Found available stylists",
        content = @Content(mediaType = "application/json",
        array = @ArraySchema(schema = @Schema(implementation = Stylist.class))))
    @GetMapping("/available")
    public List<Stylist> getAvailableStylists() {
        return service.getAvailableStylists();
    }

    // Registrar o actualizar estilista
    @Operation(summary = "Register or update a stylist")
    @ApiResponse(responseCode = "201", description = "Stylist registered",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = StylistRequestDTO.class)))
    @PostMapping
    public ResponseEntity<StylistRequestDTO> add(@RequestBody StylistRequestDTO stylistDTO) {
        Stylist savedStylist = service.save(convertToEntity(stylistDTO));
        StylistRequestDTO response = convertToDTO(savedStylist);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Eliminar estilista por ID
    @Operation(summary = "Delete a stylist by its ID")
    @ApiResponse(responseCode = "204", description = "Stylist deleted")
    @DeleteMapping("{idStylist}")
    public ResponseEntity<Void> delete(@PathVariable Integer idStylist) {
        service.delete(idStylist);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Conversiones DTO <-> Entity
    private StylistRequestDTO convertToDTO(Stylist stylist) {
        return modelMapper.map(stylist, StylistRequestDTO.class);
    }

    private Stylist convertToEntity(StylistRequestDTO stylistDTO) {
        return modelMapper.map(stylistDTO, Stylist.class);
    }
}

