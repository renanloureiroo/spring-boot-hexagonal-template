package com.renanloureiroo.stepbystep.modules.example.infra.database.jpa.repositories;

import com.renanloureiroo.stepbystep.modules.example.infra.database.jpa.entities.NoteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteJpaRepository extends JpaRepository<NoteJpaEntity, String> {}
