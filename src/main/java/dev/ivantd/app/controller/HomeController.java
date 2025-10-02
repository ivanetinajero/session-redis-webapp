package dev.ivantd.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import dev.ivantd.app.model.Usuario;
import dev.ivantd.app.service.IUsuariosService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	Logger logger = LogManager.getLogger(HomeController.class);

	@Autowired
	private IUsuariosService usuariosService;

	@GetMapping("/")
	public String home(HttpSession session, Authentication auth) {
		logger.info("Accessed home page");

		// Recuperamos el username que inicio sesión
		String username = auth.getName();
		Usuario usuarioSesion = null;
		if (session.getAttribute("userLogin") == null) {
			// Recuperamos todos los datos del usuario de la bd
			usuarioSesion = usuariosService.buscarPorUsername(username);
			session.setAttribute("userLogin", usuarioSesion);
		} else {
			// Ya esta en session el objeto usuario. Solo lo recuperamos y no vamos a la BD
			usuarioSesion = (Usuario) session.getAttribute("userLogin");
		}
		return "home";
	}

	@GetMapping("/formLogin")
	public String formLogin() {
		return "formLogin";
	}

	@GetMapping("/salir")
	public String salir(HttpServletRequest request) {
		SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(request, null, null);
		return "redirect:/";
	}

}
