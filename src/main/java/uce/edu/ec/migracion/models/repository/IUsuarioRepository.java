package uce.edu.ec.migracion.models.repository;

import org.springframework.data.repository.CrudRepository;
import uce.edu.ec.migracion.models.entity.Usuario;

public interface IUsuarioRepository extends CrudRepository<Usuario, Long> {

    Usuario findUsuarioByUsername(String nickName);

}
