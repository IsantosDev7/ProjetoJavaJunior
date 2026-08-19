package com.example.portalaluno.cargo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CargoRepository  extends JpaRepository<Cargo, UUID> {

    Optional<Cargo> findByName(String name);

}
