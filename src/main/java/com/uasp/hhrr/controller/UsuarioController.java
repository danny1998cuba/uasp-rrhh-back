/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/RestController.java to edit this template
 */
package com.uasp.hhrr.controller;

import com.google.gson.Gson;
import com.uasp.hhrr.MessageResponse;
import com.uasp.hhrr.model.Rol;
import com.uasp.hhrr.model.Usuario;
import com.uasp.hhrr.service.RolService;
import com.uasp.hhrr.service.UsuarioService;
import com.uasp.hhrr.service.UsuarioService.ChangeState;
import static com.uasp.hhrr.service.UsuarioService.ChangeState.CHANGED;
import static com.uasp.hhrr.service.UsuarioService.ChangeState.USER_NOT_FOUND;
import static com.uasp.hhrr.service.UsuarioService.ChangeState.WRONG_OLD;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 *
 * @author Tapanes
 */
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService service;

    @Autowired
    RolService roleService;

    @Autowired
    Gson g;

    @GetMapping("")
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable int id) {
        return ResponseEntity.of(service.findById(id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('CONT', 'ADMIN', 'USER')")
    public ResponseEntity<?> put(@PathVariable int id, @RequestBody Usuario input) {
        try {
            if (!service.existentUsername(input.getUsername(), id)) {
                if (!input.getRolList().isEmpty()) {
                    for (Rol rol : input.getRolList()) {
                        if (!roleService.findById(rol.getId()).isPresent()) {
                            MessageResponse m = new MessageResponse("Rol no existente " + rol.getNombre());
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(g.toJson(m));
                        }
                    }
                    int idRes = service.update(input, id);

                    if (idRes != -1) {
                        MessageResponse m = new MessageResponse("Elemento con id " + idRes + " modificado correctamente");
                        return ResponseEntity.ok(g.toJson(m));
                    } else {
                        MessageResponse m = new MessageResponse("No se encuentra el elemento con id " + id);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(g.toJson(m));
                    }
                } else {
                    MessageResponse m = new MessageResponse("El usuario debe tener al menos un rol");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(g.toJson(m));
                }
            } else {
                MessageResponse m = new MessageResponse("El nombre de usuario ya existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(g.toJson(m));
            }
        } catch (Exception e) {
            MessageResponse m = new MessageResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(g.toJson(m));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> post(@RequestBody Usuario input) {
        try {
            if (!service.existentUsername(input.getUsername())) {
                if (!input.getRolList().isEmpty()) {
                    for (Rol rol : input.getRolList()) {
                        if (!roleService.findById(rol.getId()).isPresent()) {
                            MessageResponse m = new MessageResponse("Rol no existente " + rol.getNombre());
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(g.toJson(m));
                        }
                    }
                    int idRes = service.save(input);
                    MessageResponse m = new MessageResponse("Elemento creado con id " + idRes);
                    return ResponseEntity.status(HttpStatus.CREATED).body(g.toJson(m));
                } else {
                    MessageResponse m = new MessageResponse("El usuario debe tener al menos un rol");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(g.toJson(m));
                }
            } else {
                MessageResponse m = new MessageResponse("El nombre de usuario ya existe");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(g.toJson(m));
            }

        } catch (Exception e) {
            MessageResponse m = new MessageResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(g.toJson(m));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        try {
            boolean deleted = service.deleteById(id);

            if (deleted) {
                MessageResponse m = new MessageResponse("Elemento con id " + id + " eliminado correctamente");
                return ResponseEntity.ok(g.toJson(m));
            } else {
                MessageResponse m = new MessageResponse("No se encuentra el elemento con id " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(g.toJson(m));
            }

        } catch (Exception e) {
            MessageResponse m = new MessageResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(g.toJson(m));
        }
    }

    @PostMapping("/pass/{id}")
//    @PreAuthorize("hasAnyAuthority('CONT', 'ADMIN', 'USER')")
    public ResponseEntity<?> changePassword(@PathVariable int id, @RequestBody PassObj input) {
        ChangeState result = service.changePassword(id, input.getOldPassword(), input.getNewPassword());

        switch (result) {
            case CHANGED:
                return ResponseEntity.ok(new MessageResponse("Contrase??a cambiada"));
            case WRONG_OLD:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("La contrase??a anterior no es correcta"));
            case USER_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Usuario no encontrado"));
            default:
                throw new AssertionError();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PassObj {

        private String oldPassword;
        private String newPassword;
    }

}
