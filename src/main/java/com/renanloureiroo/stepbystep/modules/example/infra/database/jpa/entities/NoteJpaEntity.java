package com.renanloureiroo.stepbystep.modules.example.infra.database.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "notes")
public class NoteJpaEntity {

  @Id
  @Column(length = 36, nullable = false, updatable = false)
  private String id;

  @Column(length = 120, nullable = false)
  private String title;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  protected NoteJpaEntity() {}

  public NoteJpaEntity(String id, String title, Instant createdAt) {
    this.id = id;
    this.title = title;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
