package uce.edu.ec.migracion.models.repository;

import org.springframework.data.repository.CrudRepository;
import uce.edu.ec.migracion.models.entity.Role;

public interface IRoleRepository extends CrudRepository<Role, Long> {
}
