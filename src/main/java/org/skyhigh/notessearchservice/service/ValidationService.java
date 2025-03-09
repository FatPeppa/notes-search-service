package org.skyhigh.notessearchservice.service;

import org.shyhigh.grpc.notes.*;
import org.skyhigh.notessearchservice.validation.exception.ValidationException;

public interface ValidationService {
    void validateNoteCreation(NoteCreateRequest noteCreateRequest) throws ValidationException;
    void validateNoteUpdate(NoteUpdateRequest noteUpdateRequest) throws ValidationException;
    void validateNoteDelete(NoteDeleteRequest noteDeleteRequest) throws ValidationException;
    void validateNoteSearch(NoteSearchRequest noteSearchRequest) throws ValidationException;
}
