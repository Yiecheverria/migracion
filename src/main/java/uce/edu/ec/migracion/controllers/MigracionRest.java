package uce.edu.ec.migracion.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uce.edu.ec.migracion.models.servicio.MigracionService;

@RestController
public class MigracionRest {

    @Autowired
    private MigracionService migracionService;

    @GetMapping("/do")
    public String migrar() {
        migracionService.migrar();
        return "Todo Ok";
    }

}
