package dev.ivantd.app.service;

import dev.ivantd.app.model.Usuario;

public interface IUsuariosService {
	Usuario autenticar(String username, String password);
}
