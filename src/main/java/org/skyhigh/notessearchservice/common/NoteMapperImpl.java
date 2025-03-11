package org.skyhigh.notessearchservice.common;

import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.shyhigh.grpc.notes.*;
import org.skyhigh.notessearchservice.model.mapped.*;
import org.skyhigh.notessearchservice.validation.exception.GrpcMessagesMappingException;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class NoteMapperImpl implements NoteMapper {

    @Override
    public NoteBodyObject map(NoteBody noteBody) throws DateTimeParseException {
        log.debug(String.format("Mapping NoteBody {%s} to NoteBodyObject started", noteBody == null ? "NULL" : noteBody.toString()));
        if (noteBody == null) {
            log.debug(String.format("Mapping NoteBody {%s} to NoteBodyObject finished successfully", "NULL"));
            return null;
        }
        try {
            var result = NoteBodyObject.builder()
                    .noteId(noteBody.getId())
                    .userId(noteBody.getUserId())
                    .name(noteBody.hasName()
                            ? noteBody.getName().getValue()
                            : null)
                    .noteCategoryObject(map(noteBody.hasCategory()
                            ? noteBody.getCategory()
                            : null))
                    .build();
            log.debug(String.format("Mapping NoteBody {%s} to NoteBodyObject finished successfully", noteBody.toString()));
            return result;
        } catch (DateTimeParseException e) {
            log.debug(String.format("Mapping NoteBody {%s} to NoteBodyObject finished with error: %s", noteBody.toString(), e.getMessage()));
            throw e;
        }
    }

    @Override
    public NoteTagObject map(NoteTag noteTag) {
        log.debug(String.format("Mapping NoteTag {%s} to NoteTagObject started", noteTag == null ? "NULL" : noteTag.toString()));
        if (noteTag == null) {
            log.debug(String.format("Mapping NoteTag {%s} to NoteTagObject finished successfully", "NULL"));
            return null;
        }
        var result = NoteTagObject.builder()
                .tagId(noteTag.getId())
                .tagName(noteTag.getName())
                .build();
        log.debug(String.format("Mapping NoteTag {%s} to NoteTagObject finished successfully", noteTag.toString()));
        return result;
    }

    @Override
    public NoteCategoryObject map(NoteCategory noteCategory) throws DateTimeParseException {
        log.debug(String.format("Mapping NoteCategory {%s} to NoteCategoryObject started", noteCategory == null ? "NULL" : noteCategory.toString()));
        if (noteCategory == null) {
            log.debug(String.format("Mapping NoteCategory {%s} to NoteCategoryObject finished successfully", "NULL"));
            return null;
        }
        try {
            var result = NoteCategoryObject.builder()
                    .categoryId(noteCategory.getId())
                    .categoryName(noteCategory.getName())
                    .build();
            log.debug(String.format("Mapping NoteCategory {%s} to NoteCategoryObject finished successfully", noteCategory.toString()));
            return result;
        } catch (DateTimeParseException e) {
            log.debug(String.format("Mapping NoteCategory {%s} to NoteCategoryObject finished with error: %s", noteCategory.toString(), e.getMessage()));
            throw e;
        }
    }

    @Override
    public NoteObject map(Note note) throws GrpcMessagesMappingException {
        log.debug(String.format("Mapping Note {%s} to NoteObject started", note == null ? "NULL" : note.toString()));
        if (note == null) {
            log.debug(String.format("Mapping Note {%s} to NoteObject finished successfully", "NULL"));
            return null;
        }
        try {
            var result = NoteObject.builder()
                    .noteId(note.getId())
                    .userId(note.getUserId())
                    .name(note.getName())
                    .noteCategoryObject(map(note.hasCategory()
                            ? note.getCategory()
                            : null))
                    .noteTagObjects(note.getNoteTagsList().isEmpty()
                            ? null
                            : note.getNoteTagsList().stream()
                            .map(this::map)
                            .toList())
                    .textExtraction(note.hasTextExtraction()
                            ? note.getTextExtraction().getValue()
                            : null)
                    .mediaId(note.hasMediaId()
                            ? UUID.fromString(note.getMediaId().getValue())
                            : null)
                    .imageIds(note.getImageIdsList().isEmpty()
                            ? null
                            : note.getImageIdsList().stream()
                            .map(x -> UUID.fromString(x.getValue()))
                            .toList())
                    .createdDate(ZonedDateTime.parse(note.getCreatedDate()))
                    .lastChangeDate(ZonedDateTime.parse(note.getLastChangeDate()))
                    .content(note.hasContent()
                            ? note.getContent().getValue().toByteArray()
                            : null)
                    .build();
            log.debug(String.format("Mapping Note {%s} to NoteObject finished successfully", note.toString()));
            return result;
        } catch (Exception e) {
            log.debug(String.format("Mapping Note {%s} to NoteObject finished with error: %s", note.toString(), e.getMessage()));
            throw new GrpcMessagesMappingException(
                    ResponseResultCode.UNEXPECTED_ERROR
            );
        }
    }

    @Override
    public NoteCreateRequestObject map(NoteCreateRequest noteCreateRequest) throws GrpcMessagesMappingException {
        log.debug(String.format("Mapping NoteCreateRequest {%s} to NoteCreateRequestObject started", noteCreateRequest == null ? "NULL" : noteCreateRequest.toString()));
        if (noteCreateRequest == null) {
            var exception = new GrpcMessagesMappingException(ResponseResultCode.UNEXPECTED_ERROR);
            log.debug(String.format("Mapping NoteCreateRequest {%s} to NoteCreateRequestObject finished with error: %s", "NULL", exception.getMessage()));
            throw exception;
        }
        try {
            var result = NoteCreateRequestObject.builder()
                    .noteBodyObject(noteCreateRequest.hasNoteBody()
                            ? map(noteCreateRequest.getNoteBody())
                            : null)
                    .createdDate(ZonedDateTime.parse(noteCreateRequest.getCreatedDate()))
                    .lastChangeDate(ZonedDateTime.parse(noteCreateRequest.getLastChangeDate()))
                    .build();
            log.debug(String.format("Mapping NoteCreateRequest {%s} to NoteCreateRequestObject finished successfully", noteCreateRequest.toString()));
            return result;
        } catch (Exception e) {
            var validationException = new GrpcMessagesMappingException(
                    ResponseResultCode.CREATE_FAILURE_UNREADABLE_MESSAGE
            );
            log.debug(String.format("Mapping NoteCreateRequest {%s} to NoteCreateRequestObject finished with error: %s; validation error: %s", noteCreateRequest.toString(), e.getMessage(), validationException.getMessage()));
            throw validationException;
        }
    }

    @Override
    public NoteUpdateRequestObject map(NoteUpdateRequest noteUpdateRequest) throws GrpcMessagesMappingException {
        log.debug(String.format("Mapping NoteUpdateRequest {%s} to NoteUpdateRequestObject started", noteUpdateRequest == null ? "NULL" : noteUpdateRequest.toString()));
        if (noteUpdateRequest == null) {
            var exception = new GrpcMessagesMappingException(ResponseResultCode.UNEXPECTED_ERROR);
            log.debug(String.format("Mapping NoteUpdateRequest {%s} to NoteUpdateRequestObject finished with error: %s", "NULL", exception.getMessage()));
            throw exception;
        }
        try {
            var result = NoteUpdateRequestObject.builder()
                    .noteDataUpdateType(noteUpdateRequest.getNoteDataUpdateType())
                    .noteBodyObject(noteUpdateRequest.hasNoteBody()
                            ? map(noteUpdateRequest.getNoteBody())
                            : null)
                    .noteTagObjects(noteUpdateRequest.getNoteTagsList().isEmpty()
                            ? null
                            : noteUpdateRequest.getNoteTagsList().stream()
                                .map(this::map)
                                .toList())
                    .textExtraction(noteUpdateRequest.hasTextExtraction()
                            ? noteUpdateRequest.getTextExtraction().getValue()
                            : null)
                    .mediaId(noteUpdateRequest.hasMediaId()
                            ? UUID.fromString(noteUpdateRequest.getMediaId().getValue())
                            : null)
                    .imageIds(noteUpdateRequest.getImageIdsList().isEmpty()
                            ? null
                            : noteUpdateRequest.getImageIdsList().stream()
                            .map(x -> UUID.fromString(x.getValue()))
                            .toList())
                    .createdDate(noteUpdateRequest.hasCreatedDate()
                            ? ZonedDateTime.parse(noteUpdateRequest.getCreatedDate().getValue())
                            : null)
                    .lastChangeDate(ZonedDateTime.parse(noteUpdateRequest.getLastChangeDate()))
                    .content(noteUpdateRequest.hasContent()
                            ? noteUpdateRequest.getContent().getValue().toByteArray()
                            : null)
                    .tagId(noteUpdateRequest.hasTagId()
                            ? noteUpdateRequest.getTagId().getValue()
                            : null)
                    .tagName(noteUpdateRequest.hasTagName()
                            ? noteUpdateRequest.getTagName().getValue()
                            : null)
                    .build();
            log.debug(String.format("Mapping NoteUpdateRequest {%s} to NoteUpdateRequestObject finished successfully", noteUpdateRequest.toString()));
            return result;
        } catch (Exception e) {
            log.debug(String.format("Mapping NoteUpdateRequest {%s} to NoteUpdateRequestObject finished with error: %s", noteUpdateRequest.toString(), e.getMessage()));
            throw new GrpcMessagesMappingException(
                    ResponseResultCode.UPDATE_FAILURE_UNREADABLE_MESSAGE
            );
        }
    }

    @Override
    public NoteDeleteRequestObject map(NoteDeleteRequest noteDeleteRequest) throws GrpcMessagesMappingException {
        log.debug(String.format("Mapping NoteDeleteRequest {%s} to NoteDeleteRequestObject started", noteDeleteRequest == null ? "NULL" : noteDeleteRequest.toString()));
        if (noteDeleteRequest == null) {
            var exception = new GrpcMessagesMappingException(ResponseResultCode.UNEXPECTED_ERROR);
            log.debug(String.format("Mapping NoteDeleteRequest {%s} to NoteDeleteRequestObject finished with error: %s", "NULL", exception.getMessage()));
            throw exception;
        }
        var result = NoteDeleteRequestObject.builder()
                .noteId(noteDeleteRequest.getId())
                .build();
        log.debug(String.format("Mapping NoteDeleteRequest {%s} to NoteDeleteRequestObject finished successfully", noteDeleteRequest.toString()));
        return result;
    }

    @Override
    public NoteSearchRequestObject map(NoteSearchRequest noteSearchRequest) throws GrpcMessagesMappingException {
        log.debug(String.format("Mapping NoteSearchRequest {%s} to NoteSearchRequestObject started", noteSearchRequest == null ? "NULL" : noteSearchRequest.toString()));
        if (noteSearchRequest == null) {
            var exception = new GrpcMessagesMappingException(ResponseResultCode.UNEXPECTED_ERROR);
            log.debug(String.format("Mapping NoteSearchRequest {%s} to NoteSearchRequestObject finished with error: %s", "NULL", exception.getMessage()));
            throw exception;
        }
        var result = NoteSearchRequestObject.builder()
                .query(noteSearchRequest.hasQuery()
                        ? noteSearchRequest.getQuery().getValue()
                        : null)
                .userId(noteSearchRequest.getUserId())
                .detailType(noteSearchRequest.getDetailType())
                .searchType(noteSearchRequest.getSearchType())
                .build();
        log.debug(String.format("Mapping NoteSearchRequest {%s} to NoteSearchRequestObject finished successfully", noteSearchRequest.toString()));
        return result;
    }

    @Override
    public NoteSearchResponseObject map(NoteSearchResponse noteSearchResponse) throws GrpcMessagesMappingException {
        log.debug(String.format("Mapping NoteSearchResponse {%s} to NoteSearchResponseObject started", noteSearchResponse == null ? "NULL" : noteSearchResponse.toString()));
        if (noteSearchResponse == null) {
            var exception = new GrpcMessagesMappingException(ResponseResultCode.UNEXPECTED_ERROR);
            log.debug(String.format("Mapping NoteSearchResponse {%s} to NoteSearchResponseObject finished with error: %s", "NULL", exception.getMessage()));
            throw exception;
        }
        try {
            var result = NoteSearchResponseObject.builder()
                    .responseResultCode(noteSearchResponse.getResponseResultCode())
                    .noteObjects(noteSearchResponse.getNotesList().isEmpty()
                            ? null
                            : noteSearchResponse.getNotesList().stream()
                            .map(x -> {
                                try {
                                    return map(x);
                                } catch (GrpcMessagesMappingException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .toList())
                    .build();
            log.debug(String.format("Mapping NoteSearchResponse {%s} to NoteSearchResponseObject finished successfully", noteSearchResponse.toString()));
            return result;
        } catch (Exception e) {
            log.debug(String.format("Mapping NoteSearchResponse {%s} to NoteSearchResponseObject finished with error: %s", noteSearchResponse.toString(), e.getMessage()));
            throw new GrpcMessagesMappingException(
                    ResponseResultCode.UNEXPECTED_ERROR
            );
        }
    }

    @Override
    public NoteBody map(NoteBodyObject noteBodyObject) {
        log.debug(String.format("Mapping NoteBodyObject {%s} to NoteBody started", noteBodyObject == null ? "NULL" : noteBodyObject.toString()));
        if (noteBodyObject == null) {
            log.debug(String.format("Mapping NoteBodyObject {%s} to NoteBody finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = NoteBody.newBuilder()
                .setId(noteBodyObject.getNoteId())
                .setUserId(noteBodyObject.getUserId())
                .setName(StringValue.of(noteBodyObject.getName()));
        if (noteBodyObject.getNoteCategoryObject() != null)
            resultBuilder.setCategory(map(noteBodyObject
                    .getNoteCategoryObject()));
        log.debug(String.format("Mapping NoteBodyObject {%s} to NoteBody finished successfully", noteBodyObject.toString()));
        return resultBuilder.build();
    }

    @Override
    public NoteTag map(NoteTagObject noteTagObject) {
        log.debug(String.format("Mapping NoteTagObject {%s} to NoteTag started", noteTagObject == null ? "NULL" : noteTagObject.toString()));
        if (noteTagObject == null) {
            log.debug(String.format("Mapping NoteTagObject {%s} to NoteTag finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = NoteTag.newBuilder()
                .setId(noteTagObject.getTagId())
                .setName(noteTagObject.getTagName());
        log.debug(String.format("Mapping NoteTagObject {%s} to NoteTag finished successfully", noteTagObject.toString()));
        return resultBuilder.build();
    }

    @Override
    public NoteCategory map(NoteCategoryObject noteCategoryObject) {
        log.debug(String.format("Mapping NoteCategoryObject {%s} to NoteCategory started", noteCategoryObject == null ? "NULL" : noteCategoryObject.toString()));
        if (noteCategoryObject == null) {
            log.debug(String.format("Mapping NoteCategoryObject {%s} to NoteCategory finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = NoteCategory.newBuilder()
                .setId(noteCategoryObject.getCategoryId())
                .setName(noteCategoryObject.getCategoryName());
        log.debug(String.format("Mapping NoteCategoryObject {%s} to NoteCategory finished successfully", noteCategoryObject.toString()));
        return resultBuilder.build();
    }

    @Override
    public Note map(NoteObject noteObject) {
        log.debug(String.format("Mapping NoteObject {%s} to Note started", noteObject == null ? "NULL" : noteObject.toString()));
        if (noteObject == null) {
            log.debug(String.format("Mapping NoteObject {%s} to Note finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = Note.newBuilder()
                .setId(noteObject.getNoteId())
                .setUserId(noteObject.getUserId())
                .setName(noteObject.getName())
                .setCreatedDate(noteObject.getCreatedDate().toString())
                .setLastChangeDate(noteObject.getLastChangeDate().toString());
        if (noteObject.getNoteCategoryObject() != null)
            resultBuilder.setCategory(map(noteObject.getNoteCategoryObject()));
        if (noteObject.getNoteTagObjects() != null
                && !noteObject.getNoteTagObjects().isEmpty())
            noteObject.getNoteTagObjects().forEach(x -> resultBuilder
                    .addNoteTags(map(x)));
        if (noteObject.getTextExtraction() != null
                && !noteObject.getTextExtraction().isBlank())
            resultBuilder.setTextExtraction(StringValue.of(noteObject
                    .getTextExtraction()));
        if (noteObject.getMediaId() != null)
            resultBuilder.setMediaId(StringValue.of(noteObject
                    .getMediaId().toString()));
        if (noteObject.getImageIds() != null
                && !noteObject.getImageIds().isEmpty())
            noteObject.getImageIds().forEach(x -> resultBuilder
                    .addImageIds(StringValue.of(x.toString())));
        if (noteObject.getContent() != null)
            resultBuilder.setContent(BytesValue.of(ByteString
                    .copyFrom(noteObject.getContent())));
        log.debug(String.format("Mapping NoteObject {%s} to Note finished successfully", noteObject.toString()));
        return resultBuilder.build();
    }

    @Override
    public NoteCreateRequest map(NoteCreateRequestObject noteCreateRequestObject) {
        log.debug(String.format("Mapping NoteCreateRequestObject {%s} to NoteCreateRequest started", noteCreateRequestObject == null ? "NULL" : noteCreateRequestObject.toString()));
        if (noteCreateRequestObject == null) {
            log.debug(String.format("Mapping NoteCreateRequestObject {%s} to NoteCreateRequest finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = NoteCreateRequest.newBuilder()
                .setCreatedDate(noteCreateRequestObject.getCreatedDate().toString())
                .setLastChangeDate(noteCreateRequestObject.getLastChangeDate().toString());
        if (noteCreateRequestObject.getNoteBodyObject() != null)
            resultBuilder.setNoteBody(map(noteCreateRequestObject.getNoteBodyObject()));
        log.debug(String.format("Mapping NoteCreateRequestObject {%s} to NoteCreateRequest finished successfully", noteCreateRequestObject.toString()));
        return resultBuilder.build();
    }

    @Override
    public NoteUpdateRequest map(NoteUpdateRequestObject noteUpdateRequestObject) {
        log.debug(String.format("Mapping NoteUpdateRequestObject {%s} to NoteUpdateRequest started", noteUpdateRequestObject == null ? "NULL" : noteUpdateRequestObject.toString()));
        if (noteUpdateRequestObject == null) {
            log.debug(String.format("Mapping NoteUpdateRequestObject {%s} to NoteUpdateRequest finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = NoteUpdateRequest.newBuilder()
                .setNoteDataUpdateType(noteUpdateRequestObject.getNoteDataUpdateType())
                .setLastChangeDate(noteUpdateRequestObject.getLastChangeDate().toString());
        if (noteUpdateRequestObject.getNoteBodyObject() != null)
            resultBuilder.setNoteBody(map(noteUpdateRequestObject
                    .getNoteBodyObject()));
        if (noteUpdateRequestObject.getNoteTagObjects() != null
                && !noteUpdateRequestObject.getNoteTagObjects().isEmpty())
            noteUpdateRequestObject.getNoteTagObjects().forEach(x -> resultBuilder
                    .addNoteTags(map(x)));
        if (noteUpdateRequestObject.getTextExtraction() != null
                && !noteUpdateRequestObject.getTextExtraction().isBlank())
            resultBuilder.setTextExtraction(StringValue.of(noteUpdateRequestObject
                    .getTextExtraction()));
        if (noteUpdateRequestObject.getMediaId() != null)
            resultBuilder.setMediaId(StringValue.of(noteUpdateRequestObject
                    .getMediaId().toString()));
        if (noteUpdateRequestObject.getImageIds() != null
                && !noteUpdateRequestObject.getImageIds().isEmpty())
            noteUpdateRequestObject.getImageIds().forEach(x -> resultBuilder
                    .addImageIds(StringValue.of(x.toString())));
        if (noteUpdateRequestObject.getCreatedDate() != null)
            resultBuilder.setCreatedDate(StringValue.of(noteUpdateRequestObject
                    .getCreatedDate().toString()));
        if (noteUpdateRequestObject.getContent() != null)
            resultBuilder.setContent(BytesValue.of(ByteString
                    .copyFrom(noteUpdateRequestObject.getContent())));
        if (noteUpdateRequestObject.getTagId() != null)
            resultBuilder.setTagId(Int64Value.of(noteUpdateRequestObject
                    .getTagId()));
        if (noteUpdateRequestObject.getTagName() != null
                && !noteUpdateRequestObject.getTagName().isBlank())
            resultBuilder.setTagName(StringValue.of(noteUpdateRequestObject
                    .getTagName()));
        log.debug(String.format("Mapping NoteUpdateRequestObject {%s} to NoteUpdateRequest finished successfully", noteUpdateRequestObject.toString()));
        return resultBuilder.build();
    }

    @Override
    public NoteDeleteRequest map(NoteDeleteRequestObject noteDeleteRequestObject) {
        log.debug(String.format("Mapping NoteDeleteRequestObject {%s} to NoteDeleteRequest started", noteDeleteRequestObject == null ? "NULL" : noteDeleteRequestObject.toString()));
        if (noteDeleteRequestObject == null) {
            log.debug(String.format("Mapping NoteDeleteRequestObject {%s} to NoteDeleteRequest finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = NoteDeleteRequest.newBuilder()
                .setId(noteDeleteRequestObject.getNoteId());
        log.debug(String.format("Mapping NoteDeleteRequestObject {%s} to NoteDeleteRequest finished successfully", noteDeleteRequestObject.toString()));
        return resultBuilder.build();
    }

    @Override
    public NoteSearchRequest map(NoteSearchRequestObject noteSearchRequestObject) {
        log.debug(String.format("Mapping NoteSearchRequestObject {%s} to NoteSearchRequest started", noteSearchRequestObject == null ? "NULL" : noteSearchRequestObject.toString()));
        if (noteSearchRequestObject == null) {
            log.debug(String.format("Mapping NoteSearchRequestObject {%s} to NoteSearchRequest finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = NoteSearchRequest.newBuilder()
                .setUserId(noteSearchRequestObject.getUserId());
        if (noteSearchRequestObject.getQuery() != null)
            resultBuilder.setQuery(StringValue.of(noteSearchRequestObject.getQuery()));
        if (noteSearchRequestObject.getDetailType() != null)
            resultBuilder.setDetailType(noteSearchRequestObject.getDetailType());
        if (noteSearchRequestObject.getSearchType() != null)
            resultBuilder.setSearchType(noteSearchRequestObject.getSearchType());
        log.debug(String.format("Mapping NoteSearchRequestObject {%s} to NoteSearchRequest finished successfully", noteSearchRequestObject.toString()));
        return resultBuilder.build();
    }

    @Override
    public NoteSearchResponse map(NoteSearchResponseObject noteSearchResponseObject) {
        log.debug(String.format("Mapping NoteSearchResponseObject {%s} to NoteSearchResponse started", noteSearchResponseObject == null ? "NULL" : noteSearchResponseObject.toString()));
        if (noteSearchResponseObject == null) {
            log.debug(String.format("Mapping NoteSearchResponseObject {%s} to NoteSearchResponse finished successfully", "NULL"));
            return null;
        }
        var resultBuilder = NoteSearchResponse.newBuilder()
                .setResponseResultCode(noteSearchResponseObject
                        .getResponseResultCode());
        if (noteSearchResponseObject.getNoteObjects() != null
                && !noteSearchResponseObject.getNoteObjects().isEmpty())
            noteSearchResponseObject.getNoteObjects().forEach(x -> resultBuilder
                    .addNotes(map(x)));
        log.debug(String.format("Mapping NoteSearchResponseObject {%s} to NoteSearchResponse finished successfully", noteSearchResponseObject.toString()));
        return resultBuilder.build();
    }
}
