package org.skyhigh.notessearchservice.model.mapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteObject {
    private Long noteId;
    private Long userId;
    private String name;
    private NoteCategoryObject noteCategoryObject;
    private List<NoteTagObject> noteTagObjects;
    private String textExtraction;
    private UUID mediaId;
    private List<UUID> imageIds;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastChangeDate;
    private byte[] content;
}
