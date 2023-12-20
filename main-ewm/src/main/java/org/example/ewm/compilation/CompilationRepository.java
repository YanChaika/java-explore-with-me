package org.example.ewm.compilation;

import org.example.ewm.compilation.model.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findAllByPinned(Boolean pinned);
}
