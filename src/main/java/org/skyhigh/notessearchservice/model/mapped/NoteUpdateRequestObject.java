package org.skyhigh.notessearchservice.model.mapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.shyhigh.grpc.notes.NoteDataUpdateType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteUpdateRequestObject {
    private NoteDataUpdateType noteDataUpdateType;
    private NoteBodyObject noteBodyObject;
    private List<NoteTagObject> noteTagObjects;
    private String textExtraction;
    private UUID mediaId;
    private List<UUID> imageIds;
    private ZonedDateTime createdDate;
    private ZonedDateTime lastChangeDate;
    private byte[] content;
    private Long tagId;
    private String tagName;
}
