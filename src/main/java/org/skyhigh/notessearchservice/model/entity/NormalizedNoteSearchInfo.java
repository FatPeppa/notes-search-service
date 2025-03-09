package org.skyhigh.notessearchservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "normalized_note_search_info", schema = "public")
public class NormalizedNoteSearchInfo {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "normalized_name", nullable = false)
    private String normalizedName;

    @Column(name = "normalized_note_content")
    private String normalizedNoteContent;
}
