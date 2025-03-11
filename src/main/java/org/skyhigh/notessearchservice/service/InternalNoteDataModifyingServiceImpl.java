package org.skyhigh.notessearchservice.service;

import lombok.RequiredArgsConstructor;
import org.shyhigh.grpc.notes.ResponseResultCode;
import org.skyhigh.notessearchservice.common.TextFileDeserializer;
import org.skyhigh.notessearchservice.model.entity.MediaId;
import org.skyhigh.notessearchservice.model.entity.Tag;
import org.skyhigh.notessearchservice.model.mapped.NoteCreateRequestObject;
import org.skyhigh.notessearchservice.model.mapped.NoteDeleteRequestObject;
import org.skyhigh.notessearchservice.model.mapped.NoteUpdateRequestObject;
import org.skyhigh.notessearchservice.repository.MediaIdRepository;
import org.skyhigh.notessearchservice.repository.NormalizedNoteSearchInfoRepository;
import org.skyhigh.notessearchservice.repository.NoteRepository;
import org.skyhigh.notessearchservice.repository.TagRepository;
import org.skyhigh.notessearchservice.validation.exception.DataInteractionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternalNoteDataModifyingServiceImpl implements InternalNoteDataModifyingService {
    private final TextFileDeserializer textFileDeserializer;
    private final NoteRepository noteRepository;
    private final MediaIdRepository mediaIdRepository;
    private final TagRepository tagRepository;
    private final NormalizedNoteSearchInfoRepository normalizedNoteSearchInfoRepository;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createNote(NoteCreateRequestObject noteCreateRequestObject) throws DataInteractionException {
        if (noteRepository.findNoteById(noteCreateRequestObject
                .getNoteBodyObject().getNoteId()) != null)
            throw new DataInteractionException(ResponseResultCode.CREATE_FAILURE_DATA_ALREADY_EXISTS);

        try {
            var noteBody = noteCreateRequestObject.getNoteBodyObject();
            var noteCategory = noteBody.getNoteCategoryObject();
            noteRepository.saveEntity(
                    noteBody.getNoteId(),
                    noteBody.getUserId(),
                    noteBody.getName(),
                    noteCategory != null ? noteCategory.getCategoryId() : null,
                    noteCategory != null ? noteCategory.getCategoryName() : null,
                    null,
                    null,
                    noteCreateRequestObject.getCreatedDate(),
                    noteCreateRequestObject.getLastChangeDate(),
                    null
            );
        } catch (Exception e) {
            throw new DataInteractionException(ResponseResultCode.UNEXPECTED_ERROR);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateNote(NoteUpdateRequestObject noteUpdateRequestObject) throws DataInteractionException {
        var noteOptional = noteRepository.findNoteById(noteUpdateRequestObject
                .getNoteBodyObject().getNoteId());
        if (noteOptional == null)
            throw new DataInteractionException(ResponseResultCode.UPDATE_FAILURE_DATA_NOT_EXIST);
        if (!noteOptional.getUserId().equals(noteUpdateRequestObject
                .getNoteBodyObject().getUserId()))
            throw new DataInteractionException(ResponseResultCode.UPDATE_FAILURE_INCORRECT_USER);

        var noteBody = noteUpdateRequestObject.getNoteBodyObject();
        try {
            switch (noteUpdateRequestObject.getNoteDataUpdateType()) {
                case NOTE_NAME_AND_CATEGORY_UPDATE -> {
                    noteRepository.updateNoteNameAndCategoryAndLastChangeDate(
                            noteBody.getNoteId(),
                            noteBody.getName(),
                            noteBody.getNoteCategoryObject() == null ? null : noteBody.getNoteCategoryObject().getCategoryId(),
                            noteBody.getNoteCategoryObject() == null ? null : noteBody.getNoteCategoryObject().getCategoryName(),
                            noteUpdateRequestObject.getLastChangeDate()
                    );
                }
                case CATEGORY_NAME_UPDATE -> {
                    noteRepository.updateNoteCategoryName(
                            noteBody.getNoteId(),
                            noteBody.getNoteCategoryObject().getCategoryName()
                    );
                }
                case NOTE_TAG_NAME_UPDATE -> {
                    noteRepository.updateNoteTagName(
                            noteBody.getNoteId(),
                            noteUpdateRequestObject.getTagId(),
                            noteUpdateRequestObject.getTagName()
                    );
                }
                case NOTE_TAGS_UPDATE -> {
                    tagRepository.deleteByNoteId(noteBody.getNoteId());
                    if (noteUpdateRequestObject.getNoteTagObjects() != null)
                        noteUpdateRequestObject.getNoteTagObjects().forEach(x -> tagRepository
                                .save(new Tag(null, noteBody.getNoteId(), x.getTagId(), x.getTagName())));
                    noteRepository.updateNoteLastChangeDate(noteBody.getNoteId(), noteUpdateRequestObject
                            .getLastChangeDate());
                }
                case NOTE_IMAGES_UPDATE -> {
                    mediaIdRepository.deleteByNoteId(noteBody.getNoteId());
                    if (noteUpdateRequestObject.getImageIds() != null)
                        noteUpdateRequestObject.getImageIds().forEach(x -> mediaIdRepository
                                .save(new MediaId(null, noteBody.getNoteId(), x)));
                    noteRepository.updateNoteLastChangeDate(noteBody.getNoteId(), noteUpdateRequestObject
                            .getLastChangeDate());
                }
                case NOTE_CONTENT_UPDATE -> {
                    noteRepository.updateNoteTextExtractionAndMediaIdAndNoteContentAndLastChangeDate(
                            noteBody.getNoteId(),
                            noteUpdateRequestObject.getTextExtraction(),
                            noteUpdateRequestObject.getMediaId(),
                            textFileDeserializer.deserializeByteArrayOfStringToString(noteUpdateRequestObject
                                    .getContent()),
                            noteUpdateRequestObject.getLastChangeDate()
                    );
                }
                default -> throw new DataInteractionException(ResponseResultCode
                        .UNEXPECTED_ERROR);
            }
        } catch (Exception e) {
            throw new DataInteractionException(e.getMessage(), ResponseResultCode.UNEXPECTED_ERROR);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteNote(NoteDeleteRequestObject noteDeleteRequestObject) throws DataInteractionException {
        if (noteRepository.findNoteById(noteDeleteRequestObject
                .getNoteId()) == null)
            throw new DataInteractionException(ResponseResultCode.DELETE_FAILURE_DATA_NOT_EXIST);
        try {
            tagRepository.deleteByNoteId(noteDeleteRequestObject.getNoteId());
            mediaIdRepository.deleteByNoteId(noteDeleteRequestObject.getNoteId());
            normalizedNoteSearchInfoRepository.deleteByNoteId(noteDeleteRequestObject.getNoteId());
            noteRepository.deleteById(noteDeleteRequestObject.getNoteId());
        } catch (Exception e) {
            throw new DataInteractionException(e.getMessage(), ResponseResultCode.UNEXPECTED_ERROR);
        }
    }
}
