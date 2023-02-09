package uce.edu.ec.migracion.models.repository;

import org.springframework.data.repository.CrudRepository;
import uce.edu.ec.migracion.models.entity.Docente;

public interface IDocenteRepository extends CrudRepository<Docente, Long> {
}
