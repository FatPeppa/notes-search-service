package org.skyhigh.notessearchservice.service;

import lombok.RequiredArgsConstructor;
import org.shyhigh.grpc.notes.*;
import org.skyhigh.notessearchservice.common.NoteMapper;
import org.skyhigh.notessearchservice.model.mapped.NoteCreateRequestObject;
import org.skyhigh.notessearchservice.model.mapped.NoteDeleteRequestObject;
import org.skyhigh.notessearchservice.model.mapped.NoteSearchRequestObject;
import org.skyhigh.notessearchservice.model.mapped.NoteUpdateRequestObject;
import org.skyhigh.notessearchservice.validation.exception.GrpcMessagesMappingException;
import org.skyhigh.notessearchservice.validation.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {
    private final NoteMapper noteMapper;

    @Override
    public void validateNoteCreation(NoteCreateRequest noteCreateRequest) throws ValidationException {
        NoteCreateRequestObject noteCreateRequestObject;

        try {
            noteCreateRequestObject = noteMapper.map(noteCreateRequest);
        } catch (GrpcMessagesMappingException e) {
            throw new ValidationException(e.getResponseResultCode());
        }

        if (noteCreateRequestObject == null)
            throw new ValidationException(ResponseResultCode.CREATE_FAILURE_UNREADABLE_MESSAGE);
        if (noteCreateRequestObject.getNoteBodyObject() == null
                || noteCreateRequestObject.getNoteBodyObject().getName() == null
                || noteCreateRequestObject.getNoteBodyObject().getName().isBlank())
            throw new ValidationException(ResponseResultCode.CREATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
        if (noteCreateRequestObject.getNoteBodyObject().getNoteCategoryObject() != null
                && (noteCreateRequestObject.getNoteBodyObject().getNoteCategoryObject().getCategoryId() == null
                || noteCreateRequestObject.getNoteBodyObject().getNoteCategoryObject().getCategoryName() == null
                || noteCreateRequestObject.getNoteBodyObject().getNoteCategoryObject().getCategoryName().isBlank()))
            throw new ValidationException(ResponseResultCode.CREATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
    }

    @Override
    public void validateNoteUpdate(
            NoteUpdateRequest noteUpdateRequest
    ) throws ValidationException {
        if (noteUpdateRequest.getNoteDataUpdateType() == NoteDataUpdateType.UNRECOGNIZED)
            throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_UNREADABLE_MESSAGE);

        NoteUpdateRequestObject noteUpdateRequestObject;

        try {
            noteUpdateRequestObject = noteMapper.map(noteUpdateRequest);
        } catch (GrpcMessagesMappingException e) {
            throw new ValidationException(e.getResponseResultCode());
        }

        if (noteUpdateRequestObject == null || noteUpdateRequestObject.getNoteDataUpdateType() == null)
            throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_UNREADABLE_MESSAGE);

        switch (noteUpdateRequestObject.getNoteDataUpdateType()) {
            case NOTE_NAME_AND_CATEGORY_UPDATE -> {
                if (noteUpdateRequestObject.getNoteBodyObject() == null
                        || noteUpdateRequestObject.getNoteBodyObject().getName() == null
                        || noteUpdateRequestObject.getNoteBodyObject().getName().isBlank()
                ) throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
                if (noteUpdateRequestObject.getNoteBodyObject().getNoteCategoryObject() != null
                        && (noteUpdateRequestObject.getNoteBodyObject().getNoteCategoryObject().getCategoryId() == null
                        || noteUpdateRequestObject.getNoteBodyObject().getNoteCategoryObject().getCategoryName() == null
                        || noteUpdateRequestObject.getNoteBodyObject().getNoteCategoryObject().getCategoryName().isBlank()))
                    throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
            }
            case CATEGORY_NAME_UPDATE -> {
                if (noteUpdateRequestObject.getNoteBodyObject() == null
                        || noteUpdateRequestObject.getNoteBodyObject().getNoteCategoryObject().getCategoryName() == null
                        || noteUpdateRequestObject.getNoteBodyObject().getNoteCategoryObject().getCategoryName().isBlank()
                ) throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
            }
            case NOTE_TAGS_UPDATE -> {
                if (noteUpdateRequestObject.getNoteBodyObject() == null
                        //|| noteUpdateRequestObject.getNoteTagObjects() == null
                ) throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
            }
            case NOTE_IMAGES_UPDATE -> {
                if (noteUpdateRequestObject.getNoteBodyObject() == null
                        //|| noteUpdateRequestObject.getImageIds() == null
                ) throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
            }
            case NOTE_TAG_NAME_UPDATE -> {
                if (noteUpdateRequestObject.getNoteBodyObject().getNoteId() == null
                        || noteUpdateRequestObject.getTagId() == null
                        || noteUpdateRequestObject.getTagName() == null
                ) throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
            }
            case NOTE_CONTENT_UPDATE -> {
                if (noteUpdateRequestObject.getNoteBodyObject() == null
                        || noteUpdateRequestObject.getContent() == null
                ) throw new ValidationException(ResponseResultCode.UPDATE_FAILURE_INCORRECT_FIELDS_FILLED_MESSAGE);
            }
        }
    }

    @Override
    public void validateNoteDelete(NoteDeleteRequest noteDeleteRequest) throws ValidationException {
        NoteDeleteRequestObject noteDeleteRequestObject;

        try {
            noteDeleteRequestObject = noteMapper.map(noteDeleteRequest);
        } catch (GrpcMessagesMappingException e) {
            throw new ValidationException(e.getResponseResultCode());
        }

        if (noteDeleteRequestObject == null)
            throw new ValidationException(ResponseResultCode.CREATE_FAILURE_UNREADABLE_MESSAGE);
    }

    @Override
    public void validateNoteSearch(NoteSearchRequest noteSearchRequest) throws ValidationException {
        if (!noteSearchRequest.hasQuery()
                || noteSearchRequest.getDetailType() == NoteSearchResponseDetailType.UNRECOGNIZED
                || noteSearchRequest.getSearchType() == SearchType.UNRECOGNIZED)
            throw new ValidationException(ResponseResultCode.SEARCH_UNREADABLE_MESSAGE);

        NoteSearchRequestObject noteSearchRequestObject;

        try {
            noteSearchRequestObject = noteMapper.map(noteSearchRequest);
        } catch (GrpcMessagesMappingException e) {
            throw new ValidationException(e.getResponseResultCode());
        }

        if (noteSearchRequestObject == null)
            throw new ValidationException(ResponseResultCode.SEARCH_UNREADABLE_MESSAGE);
    }
}
