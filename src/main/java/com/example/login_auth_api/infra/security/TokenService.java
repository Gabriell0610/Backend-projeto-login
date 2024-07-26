package com.example.login_auth_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.login_auth_api.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    public String createToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); // chave privada responsável por criptografar e descriptografar os dados e para ter certeza sobre quem emitiu o token
            String token = JWT.create()
                    .withIssuer("login-auth-api") // verifica se o token foi emitido pelo emissor, que nesse caso é a API
                    .withSubject(user.getEmail()) // Quem está sendo o sujeito que está ganhando esse token
                    .withExpiresAt(this.generateExpirationDate())  // Data que o token expira
                    .sign(algorithm); // Gerando algoritimo
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while authenticating");
        }


    }

    public String validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret); //Usada para assinar o token quando ele é criado e é necessária para verificar a integridade do token. Sem a chave correta, não é possível validar o token.
            return JWT.require(algorithm)
                    //Montando o objeto para fazer a verificação
                    .withIssuer("login-auth-api")
                    .build()
                    //Verificando o token que foi criado
                    .verify(token)
                    .getSubject();
        }
        catch(JWTVerificationException e ){
            return null; // Retornando nulo se o token vier faltando algo ou algo do tipo

        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-3"));
    }
}
