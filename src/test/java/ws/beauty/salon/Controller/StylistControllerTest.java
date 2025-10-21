package ws.beauty.salon.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ws.beauty.salon.controller.StylistController;
import ws.beauty.salon.dto.StylistRequest;
import ws.beauty.salon.dto.StylistResponse;
import ws.beauty.salon.service.StylistService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StylistController.class)
class StylistControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private StylistService service;

    private static final String BASE = "/api/stylists";

    @BeforeEach
    void setUp() {
        reset(service); // resetea el mock antes de cada test
    }

    // ==========================
    // TestConfiguration para mock
    // ==========================
    @TestConfiguration
    static class TestConfig {
        @Bean
        StylistService stylistService() {
            return Mockito.mock(StylistService.class);
        }
    }

    // ==========================
    // Helpers
    // ==========================
    private StylistResponse resp(int id, String name, String specialty, boolean available) {
        StylistResponse r = new StylistResponse();
        r.setId(id);
        r.setFirstName(name);
        r.setSpecialty(specialty);
        r.setAvailable(available);
        return r;
    }

    private StylistRequest req(String name, String specialty, boolean available) {
        StylistRequest r = new StylistRequest();
        r.setFirstName(name);
        r.setSpecialty(specialty);
        r.setAvailable(available);
        return r;
    }

    // ==========================
    // GET /api/stylists
    // ==========================
    @Test
    @DisplayName("GET /api/stylists → 200 con lista")
    void getAll_ok() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                resp(1, "Ana", "Colorista", true),
                resp(2, "Luis", "Corte", false)
        ));

        mvc.perform(get(BASE).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Ana"))
                .andExpect(jsonPath("$[1].specialty").value("Corte"));
    }

    @Test
    @DisplayName("GET /api/stylists → 200 con lista vacía")
    void getAll_empty() throws Exception {
        when(service.findAll()).thenReturn(Collections.emptyList());

        mvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    // ==========================
    // GET /api/stylists/pagination
    // ==========================
    @ParameterizedTest
    @CsvSource({
            "0,5",
            "1,10"
    })
    @DisplayName("GET paginado válido → 200")
    void getAllPaginated_ok(int page, int size) throws Exception {
        when(service.findAllPaginated(page, size))
                .thenReturn(List.of(resp(10, "Marta", "Peinados", true)));

        mvc.perform(get(BASE + "/pagination")
                        .queryParam("page", String.valueOf(page))
                        .queryParam("pageSize", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Marta"));
    }

    @ParameterizedTest
    @CsvSource({
            "-1,10",
            "1,-5"
    })
    @DisplayName("GET paginado inválido → 400")
    void getAllPaginated_badRequest(int page, int size) throws Exception {
        when(service.findAllPaginated(page, size))
                .thenThrow(new IllegalArgumentException("Invalid paging"));

        mvc.perform(get(BASE + "/pagination")
                        .queryParam("page", String.valueOf(page))
                        .queryParam("pageSize", String.valueOf(size)))
                .andExpect(status().isBadRequest());
    }

    // ==========================
    // GET /api/stylists/{id}
    // ==========================
    @Test
    @DisplayName("GET /{id} existente → 200")
    void getById_ok() throws Exception {
        when(service.findById(5)).thenReturn(resp(5, "María", "Tinte", true));

        mvc.perform(get(BASE + "/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("María"))
                .andExpect(jsonPath("$.specialty").value("Tinte"));
    }

    @Test
    @DisplayName("GET /{id} no encontrado → 404")
    void getById_notFound() throws Exception {
        when(service.findById(999))
                .thenThrow(new EntityNotFoundException("Stylist not found"));

        mvc.perform(get(BASE + "/{id}", 999))
                .andExpect(status().isNotFound());
    }

    // ==========================
    // POST /api/stylists
    // ==========================
    @Test
    @DisplayName("POST válido → 201")
    void create_ok() throws Exception {
        StylistRequest rq = req("Pedro", "Barbería", true);
        StylistResponse created = resp(101, "Pedro", "Barbería", true);
        when(service.create(any(StylistRequest.class))).thenReturn(created);

        mvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.firstName").value("Pedro"));
    }

    @Test
    @DisplayName("POST inválido → 400 por body vacío")
    void create_invalid() throws Exception {
        mvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ==========================
    // PUT /api/stylists/{id}
    // ==========================
    @Test
    @DisplayName("PUT válido → 200 con body actualizado")
    void update_ok() throws Exception {
        StylistRequest rq = req("María", "Colorista", true);
        StylistResponse updated = resp(2, "María", "Colorista", true);
        when(service.update(eq(2), any(StylistRequest.class))).thenReturn(updated);

        mvc.perform(put(BASE + "/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(rq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("María"))
                .andExpect(jsonPath("$.specialty").value("Colorista"));
    }

    @Test
    @DisplayName("PUT no existente → 404")
    void update_notFound() throws Exception {
        when(service.update(eq(999), any(StylistRequest.class)))
                .thenThrow(new EntityNotFoundException("Not found"));

        mvc.perform(put(BASE + "/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req("X", "Y", false))))
                .andExpect(status().isNotFound());
    }

    // ==========================
    // DELETE /api/stylists/{id}
    // ==========================
    @Test
    @DisplayName("DELETE existente → 204")
    void delete_ok() throws Exception {
        doNothing().when(service).delete(7);

        mvc.perform(delete(BASE + "/{id}", 7))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE no existente → 404")
    void delete_notFound() throws Exception {
        doThrow(new EntityNotFoundException("Not found"))
                .when(service).delete(500);

        mvc.perform(delete(BASE + "/{id}", 500))
                .andExpect(status().isNotFound());
    }

    // ==========================
    // GET /api/stylists/specialty/{s}
    // ==========================
    @Test
    @DisplayName("GET por especialidad → 200 con lista")
    void getBySpecialty_ok() throws Exception {
        when(service.getBySpecialty("colorista"))
                .thenReturn(List.of(resp(1, "Ana", "Colorista", true)));

        mvc.perform(get(BASE + "/specialty/{specialty}", "colorista"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specialty",
                        org.hamcrest.Matchers.containsStringIgnoringCase("colorista")));
    }

    @Test
    @DisplayName("GET por especialidad sin resultados → 200 []")
    void getBySpecialty_empty() throws Exception {
        when(service.getBySpecialty("xxx")).thenReturn(Collections.emptyList());

        mvc.perform(get(BASE + "/specialty/{specialty}", "xxx"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    // ==========================
    // GET /api/stylists/available
    // ==========================
    @Test
    @DisplayName("GET stylists disponibles → 200 con lista")
    void getAvailable_ok() throws Exception {
        when(service.getAvailableStylists())
                .thenReturn(List.of(resp(1, "Luz", "Maquillaje", true)));

        mvc.perform(get(BASE + "/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("GET stylists disponibles → 200 sin resultados")
    void getAvailable_empty() throws Exception {
        when(service.getAvailableStylists()).thenReturn(Collections.emptyList());

        mvc.perform(get(BASE + "/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }
}
