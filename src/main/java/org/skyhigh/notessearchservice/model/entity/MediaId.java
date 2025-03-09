package org.skyhigh.notessearchservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "media_ids", schema = "public")
public class MediaId {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "public.media_ids_seq")
    @SequenceGenerator(name = "public.media_ids_seq", sequenceName = "public.media_ids_seq", allocationSize = 1)
    private Long id;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Column(name = "media_id", nullable = false)
    private UUID mediaId;
}
