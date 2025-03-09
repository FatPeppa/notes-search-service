package org.skyhigh.notessearchservice.model.mapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.shyhigh.grpc.notes.NoteSearchResponseDetailType;
import org.shyhigh.grpc.notes.SearchType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoteSearchRequestObject {
    private SearchType searchType;
    private String query;
    private Long userId;
    private NoteSearchResponseDetailType detailType;
}
