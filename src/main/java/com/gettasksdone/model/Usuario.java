package com.gettasksdone.model;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Recurso para la gestión de las credenciales de acceso en el sistema.")
public class Usuario implements UserDetails {
    public enum Rol{USUARIO, ADMINISTRADOR};
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(required = true, example = "1")
    private Long id;
    @Basic
    @Column(nullable = false)
    @Schema(required = true, example = "usuarioPrueba")
    String username;
    @Column(nullable = false)
    @Schema(required = true, example = "prueba@gettasksdone.com")
    private String email;
    @Column(nullable = false)
    @Schema(required = true, example = "contraseñaMuySegura1234")
    private String password;
    @Column(nullable = false)
    @Schema(required = true, description = "Nivel de privilegios del usuario.")
    private Rol rol;
    @Override
    @Schema(description = "Obtiene el nivel de privilegios del usuario.")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority((rol.name())));
    }
    @Override
    @Schema(description = "Comprueba si la cuenta está caducada.")
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    @Schema(description = "Comprueba si la cuenta está bloqueada.")
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    @Schema(description = "Comprueba si las credenciales del usuario han caducado.")
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    @Schema(description = "Comprueba si el usuario está habilitado.")
    public boolean isEnabled() {
        return true;
    }
}
