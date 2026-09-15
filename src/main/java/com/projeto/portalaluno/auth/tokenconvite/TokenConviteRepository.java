package com.projeto.portalaluno.auth.tokenconvite;

import com.projeto.portalaluno.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TokenConviteRepository extends JpaRepository<TokenConvite, UUID> {
    Optional<TokenConvite> findByToken(String token);
    Optional<TokenConvite> findByUser(User user);
}