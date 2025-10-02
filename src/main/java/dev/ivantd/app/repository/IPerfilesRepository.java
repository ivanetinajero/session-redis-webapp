package dev.ivantd.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.ivantd.app.model.Perfil;

public interface IPerfilesRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByPerfil(String perfil);

}
