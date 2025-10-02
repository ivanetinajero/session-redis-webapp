package dev.ivantd.app.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

	Logger logger = LogManager.getLogger(HomeController.class);

	@GetMapping("/")
	public String home() {
		logger.info("Accessed home page");
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
