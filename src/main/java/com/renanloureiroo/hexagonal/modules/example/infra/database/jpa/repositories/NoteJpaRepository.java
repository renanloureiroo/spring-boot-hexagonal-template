package com.renanloureiroo.hexagonal.modules.example.infra.database.jpa.repositories;

import com.renanloureiroo.hexagonal.modules.example.infra.database.jpa.entities.NoteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteJpaRepository extends JpaRepository<NoteJpaEntity, String> {}
