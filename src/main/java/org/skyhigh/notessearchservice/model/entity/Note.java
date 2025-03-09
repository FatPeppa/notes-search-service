package org.skyhigh.notessearchservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "notes", schema = "public")
public class Note {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "note_category_id")
    private Long noteCategoryId;

    @Column(name = "note_category_name")
    private String noteCategoryName;

    @Column(name = "text_extraction")
    private String textExtraction;

    @Column(name = "media_id")
    private UUID mediaId;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "last_change_date")
    private ZonedDateTime lastChangeDate;

    @Column(name = "note_content")
    private String noteContent;
}
