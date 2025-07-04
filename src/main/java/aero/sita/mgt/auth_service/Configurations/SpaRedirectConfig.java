package aero.sita.mgt.auth_service.Configurations;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaRedirectConfig {

    // Rota para URLs simples sem ponto (não arquivos)
    @RequestMapping("/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }

    // Rota para URLs mais profundas (caminhos aninhados) sem ponto
    @RequestMapping("/{path:^(?!api$).*$}/{subpath:[^\\.]*}")
    public String redirectDeep() {
        return "forward:/index.html";
    }
}
