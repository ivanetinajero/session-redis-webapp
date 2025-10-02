package dev.ivantd.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import dev.ivantd.app.model.Producto;

public interface IProductosRepository extends JpaRepository<Producto, Long> {

}
