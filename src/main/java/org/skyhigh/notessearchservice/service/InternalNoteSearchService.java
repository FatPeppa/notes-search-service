package org.skyhigh.notessearchservice.service;

import org.skyhigh.notessearchservice.model.mapped.NoteSearchRequestObject;
import org.skyhigh.notessearchservice.model.mapped.NoteSearchResponseObject;

public interface InternalNoteSearchService {
    NoteSearchResponseObject searchNotes(NoteSearchRequestObject noteSearchRequestObject);
}
