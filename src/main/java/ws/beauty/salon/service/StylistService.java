package ws.beauty.salon.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ws.beauty.salon.model.Stylist;
import ws.beauty.salon.repository.StylistRepository;

@Service
@Transactional
public class StylistService {

    @Autowired
    private StylistRepository repository;

    // Obtener todos los estilistas
    public List<Stylist> getAll() {
        return repository.findAll();
    }

    // Obtener todos los estilistas con paginación
    public List<Stylist> getAll(int page, int pageSize) {
        PageRequest pageReq = PageRequest.of(page, pageSize);
        Page<Stylist> stylists = repository.findAll(pageReq);
        return stylists.getContent();
    }

    // Obtener todos los estilistas ordenados por apellido
    public List<Stylist> getAllOrderByLastName() {
        return repository.findAll(Sort.by("lastName"));
    }

    // Guardar o actualizar un estilista
    public Stylist save(Stylist stylist) {
        return repository.save(stylist);
    }

    // Buscar un estilista por ID
    public Optional<Stylist> getById(Integer idStylist) {
        return repository.findById(idStylist);
    }

    // Eliminar un estilista por ID
    public void delete(Integer idStylist) {
        repository.deleteById(idStylist);
    }

    // Buscar estilistas por especialidad
    public List<Stylist> getStylistsBySpecialty(String specialty) {
        return repository.getStylistsBySpecialtyJPQL(specialty);
    }

    //  Obtener estilistas disponibles
    public List<Stylist> getAvailableStylists() {
        return repository.getAvailableStylists();
    }
}
