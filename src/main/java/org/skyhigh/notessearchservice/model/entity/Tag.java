package org.skyhigh.notessearchservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tags", schema = "public")
public class Tag {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "public.tags_seq")
    @SequenceGenerator(name = "public.tags_seq", sequenceName = "public.tags_seq", allocationSize = 1)
    private Long id;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "tag_name", nullable = false)
    private String tagName;
}
