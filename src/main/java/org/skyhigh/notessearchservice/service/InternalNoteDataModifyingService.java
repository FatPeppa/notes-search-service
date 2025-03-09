package org.skyhigh.notessearchservice.service;

import org.skyhigh.notessearchservice.model.mapped.NoteCreateRequestObject;
import org.skyhigh.notessearchservice.model.mapped.NoteDeleteRequestObject;
import org.skyhigh.notessearchservice.model.mapped.NoteUpdateRequestObject;
import org.skyhigh.notessearchservice.validation.exception.DataInteractionException;

public interface InternalNoteDataModifyingService {
    void createNote(NoteCreateRequestObject noteCreateRequestObject) throws DataInteractionException;
    void updateNote(NoteUpdateRequestObject noteUpdateRequestObject) throws DataInteractionException;
    void deleteNote(NoteDeleteRequestObject noteDeleteRequestObject) throws DataInteractionException;
}
