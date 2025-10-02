package dev.ivantd.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.ivantd.app.model.Usuario;

public interface IUsuariosRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

}
