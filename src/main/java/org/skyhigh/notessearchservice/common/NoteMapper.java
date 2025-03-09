package org.skyhigh.notessearchservice.common;

import org.shyhigh.grpc.notes.*;
import org.skyhigh.notessearchservice.model.mapped.*;
import org.skyhigh.notessearchservice.validation.exception.GrpcMessagesMappingException;

import java.time.format.DateTimeParseException;

public interface NoteMapper {
    NoteBodyObject map(NoteBody noteBody) throws DateTimeParseException;
    NoteTagObject map(NoteTag noteTag);
    NoteCategoryObject map(NoteCategory noteCategory) throws DateTimeParseException;
    NoteObject map(Note note) throws GrpcMessagesMappingException;
    NoteCreateRequestObject map(NoteCreateRequest noteCreateRequest) throws GrpcMessagesMappingException;
    NoteUpdateRequestObject map(NoteUpdateRequest noteUpdateRequest) throws GrpcMessagesMappingException;
    NoteDeleteRequestObject map(NoteDeleteRequest noteDeleteRequest) throws GrpcMessagesMappingException;
    NoteSearchRequestObject map(NoteSearchRequest noteSearchRequest) throws GrpcMessagesMappingException;
    NoteSearchResponseObject map(NoteSearchResponse noteSearchResponse) throws GrpcMessagesMappingException;

    NoteBody map(NoteBodyObject noteBodyObject);
    NoteTag map(NoteTagObject noteTagObject);
    NoteCategory map(NoteCategoryObject noteCategoryObject);
    Note map(NoteObject noteObject);
    NoteCreateRequest map(NoteCreateRequestObject noteCreateRequestObject);
    NoteUpdateRequest map(NoteUpdateRequestObject noteUpdateRequestObject);
    NoteDeleteRequest map(NoteDeleteRequestObject noteDeleteRequestObject);
    NoteSearchRequest map(NoteSearchRequestObject noteSearchRequestObject);
    NoteSearchResponse map(NoteSearchResponseObject noteSearchResponseObject);
}
