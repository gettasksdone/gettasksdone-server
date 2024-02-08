package service;

import org.springframework.stereotype.Service;

import com.gettasksdone.auth.LoginRequest;
import com.gettasksdone.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository userRepository;

}
