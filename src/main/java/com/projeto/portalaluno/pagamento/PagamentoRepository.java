package com.projeto.portalaluno.pagamento;

import com.projeto.portalaluno.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
    List<Pagamento> findByUsuario(User usuario);
}
