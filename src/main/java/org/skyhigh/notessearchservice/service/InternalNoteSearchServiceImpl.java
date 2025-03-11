package org.skyhigh.notessearchservice.service;

import lombok.RequiredArgsConstructor;
import org.shyhigh.grpc.notes.ResponseResultCode;
import org.skyhigh.notessearchservice.common.TextFileSerializer;
import org.skyhigh.notessearchservice.model.entity.MediaId;
import org.skyhigh.notessearchservice.model.entity.Note;
import org.skyhigh.notessearchservice.model.mapped.*;
import org.skyhigh.notessearchservice.repository.MediaIdRepository;
import org.skyhigh.notessearchservice.repository.NormalizedNoteSearchInfoRepository;
import org.skyhigh.notessearchservice.repository.NoteRepository;
import org.skyhigh.notessearchservice.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalNoteSearchServiceImpl implements InternalNoteSearchService {
    private final NoteRepository noteRepository;
    private final TagRepository tagRepository;
    private final MediaIdRepository mediaIdRepository;
    private final TextFileSerializer textFileSerializer;
    private final NormalizedNoteSearchInfoRepository normalizedNoteSearchInfoRepository;

    @Override
    public NoteSearchResponseObject searchNotes(NoteSearchRequestObject noteSearchRequestObject) {
        List<Note> notes = null;
        switch (noteSearchRequestObject.getSearchType()) {
            case NORMALIZED -> {
                notes = noteRepository.findNoteByPartEqualityNormalized(
                        noteSearchRequestObject.getUserId(),
                        "%" + noteSearchRequestObject.getQuery() + "%"
                );
            }
            case NON_NORMALIZED -> {
                notes = noteRepository.findNoteByPartEqualityNonNormalized(
                        noteSearchRequestObject.getUserId(),
                        "%" + noteSearchRequestObject.getQuery() + "%"
                );
            }
            case NORMALIZED_FULL_TEXT -> {
                var normalizedNoteSearchInfo = normalizedNoteSearchInfoRepository.findNormalizedNoteSearchInfoByUserIdAndQuery(
                        noteSearchRequestObject.getUserId(),
                        noteSearchRequestObject.getQuery()
                );

                if (normalizedNoteSearchInfo == null || normalizedNoteSearchInfo.isEmpty())
                    normalizedNoteSearchInfo = normalizedNoteSearchInfoRepository.findNormalizedNoteSearchInfoByPartEquality(
                            noteSearchRequestObject.getUserId(),
                            "%" + noteSearchRequestObject.getQuery() + "%"
                    );

                if (normalizedNoteSearchInfo == null || normalizedNoteSearchInfo.isEmpty())
                    return NoteSearchResponseObject.builder()
                            .responseResultCode(ResponseResultCode.SEARCH_NOTES_NOT_FOUND)
                            .build();

                notes = normalizedNoteSearchInfo.stream().map(x -> noteRepository
                        .findNoteById(x.getNoteId())).toList();
            }
            case NON_NORMALIZED_FULL_TEXT -> {
                var normalizedNoteSearchInfo = normalizedNoteSearchInfoRepository.findNormalizedNoteSearchInfoByUserIdAndQuery(
                        noteSearchRequestObject.getUserId(),
                        noteSearchRequestObject.getQuery()
                );

                notes = normalizedNoteSearchInfo == null || normalizedNoteSearchInfo.isEmpty()
                        ? noteRepository.findNoteByPartEqualityNonNormalized(
                                noteSearchRequestObject.getUserId(),
                        "%" + noteSearchRequestObject.getQuery() + "%")
                        : normalizedNoteSearchInfo.stream().map(x -> noteRepository
                            .findNoteById(x.getNoteId())).toList();
            }
            default -> {
                return NoteSearchResponseObject.builder()
                        .responseResultCode(ResponseResultCode.UNEXPECTED_ERROR)
                        .build();
            }
        }

        if (notes == null || notes.isEmpty())
            return NoteSearchResponseObject.builder()
                    .responseResultCode(ResponseResultCode.SEARCH_NOTES_NOT_FOUND)
                    .build();

        var resultBuilder = NoteSearchResponseObject.builder()
                .responseResultCode(ResponseResultCode.SEARCH_SUCCESS);

        try {
            return switch (noteSearchRequestObject.getDetailType()) {
                case FULL -> {
                    resultBuilder.noteObjects(notes.stream().map(x -> {
                        var noteTags = tagRepository.findTagsByNoteId(x.getId());
                        var noteMediaIds = mediaIdRepository.findByNoteId(x.getId());
                        try {
                            return NoteObject.builder()
                                    .noteId(x.getId())
                                    .userId(x.getUserId())
                                    .name(x.getName())
                                    .noteCategoryObject(x.getNoteCategoryId() != null
                                            ? new NoteCategoryObject(x.getNoteCategoryId(), x.getNoteCategoryName())
                                            : null)
                                    .noteTagObjects(noteTags == null || noteTags.isEmpty() ? null : noteTags.stream()
                                            .map(y -> new NoteTagObject(y.getTagId(), y.getTagName())).toList())
                                    .textExtraction(x.getTextExtraction())
                                    .mediaId(x.getMediaId())
                                    .imageIds(noteMediaIds == null || noteMediaIds.isEmpty() ? null : noteMediaIds.stream()
                                            .map(MediaId::getMediaId).toList())
                                    .createdDate(x.getCreatedDate())
                                    .lastChangeDate(x.getLastChangeDate())
                                    .content(textFileSerializer.serializeToByteArrayFromString(x.getNoteContent()))
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList());
                    yield resultBuilder.build();
                }
                case WITHOUT_TEXT_EXTRACTION -> {
                    resultBuilder.noteObjects(notes.stream().map(x -> {
                        var noteTags = tagRepository.findTagsByNoteId(x.getId());
                        var noteMediaIds = mediaIdRepository.findByNoteId(x.getId());
                        try {
                            return NoteObject.builder()
                                    .noteId(x.getId())
                                    .userId(x.getUserId())
                                    .name(x.getName())
                                    .noteCategoryObject(x.getNoteCategoryId() != null
                                            ? new NoteCategoryObject(x.getNoteCategoryId(), x.getNoteCategoryName())
                                            : null)                                    .noteTagObjects(noteTags == null || noteTags.isEmpty() ? null : noteTags.stream()
                                            .map(y -> new NoteTagObject(y.getTagId(), y.getTagName())).toList())
                                    .mediaId(x.getMediaId())
                                    .imageIds(noteMediaIds == null || noteMediaIds.isEmpty() ? null : noteMediaIds.stream()
                                            .map(MediaId::getMediaId).toList())
                                    .createdDate(x.getCreatedDate())
                                    .lastChangeDate(x.getLastChangeDate())
                                    .content(textFileSerializer.serializeToByteArrayFromString(x.getNoteContent()))
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList());
                    yield resultBuilder.build();
                }
                case WITHOUT_CONTENT -> {
                    resultBuilder.noteObjects(notes.stream().map(x -> {
                        var noteTags = tagRepository.findTagsByNoteId(x.getId());
                        var noteMediaIds = mediaIdRepository.findByNoteId(x.getId());
                        return NoteObject.builder()
                                .noteId(x.getId())
                                .userId(x.getUserId())
                                .name(x.getName())
                                .noteCategoryObject(x.getNoteCategoryId() != null
                                        ? new NoteCategoryObject(x.getNoteCategoryId(), x.getNoteCategoryName())
                                        : null)                                .noteTagObjects(noteTags == null || noteTags.isEmpty() ? null : noteTags.stream()
                                        .map(y -> new NoteTagObject(y.getTagId(), y.getTagName())).toList())
                                .mediaId(x.getMediaId())
                                .imageIds(noteMediaIds == null || noteMediaIds.isEmpty() ? null : noteMediaIds.stream()
                                        .map(MediaId::getMediaId).toList())
                                .createdDate(x.getCreatedDate())
                                .lastChangeDate(x.getLastChangeDate())
                                .build();
                    }).toList());
                    yield resultBuilder.build();
                }
                case WITHOUT_TAGS -> {
                    resultBuilder.noteObjects(notes.stream().map(x -> {
                        var noteMediaIds = mediaIdRepository.findByNoteId(x.getId());
                        return NoteObject.builder()
                                .noteId(x.getId())
                                .userId(x.getUserId())
                                .name(x.getName())
                                .noteCategoryObject(x.getNoteCategoryId() != null
                                        ? new NoteCategoryObject(x.getNoteCategoryId(), x.getNoteCategoryName())
                                        : null)                                .mediaId(x.getMediaId())
                                .imageIds(noteMediaIds == null || noteMediaIds.isEmpty() ? null : noteMediaIds.stream()
                                        .map(MediaId::getMediaId).toList())
                                .createdDate(x.getCreatedDate())
                                .lastChangeDate(x.getLastChangeDate())
                                .build();
                    }).toList());
                    yield resultBuilder.build();
                }
                case WITHOUT_MEDIA_IDS -> {
                    resultBuilder.noteObjects(notes.stream().map(x -> {
                        var noteTags = tagRepository.findTagsByNoteId(x.getId());
                        return NoteObject.builder()
                                .noteId(x.getId())
                                .userId(x.getUserId())
                                .name(x.getName())
                                .noteCategoryObject(x.getNoteCategoryId() != null
                                        ? new NoteCategoryObject(x.getNoteCategoryId(), x.getNoteCategoryName())
                                        : null)                                .noteTagObjects(noteTags == null || noteTags.isEmpty() ? null : noteTags.stream()
                                        .map(y -> new NoteTagObject(y.getTagId(), y.getTagName())).toList())
                                .mediaId(x.getMediaId())
                                .createdDate(x.getCreatedDate())
                                .lastChangeDate(x.getLastChangeDate())
                                .build();
                    }).toList());
                    yield resultBuilder.build();
                }
                case WITHOUT_TAGS_AND_MEDIA_IDS -> {
                    resultBuilder.noteObjects(notes.stream().map(x ->
                        NoteObject.builder()
                                .noteId(x.getId())
                                .userId(x.getUserId())
                                .name(x.getName())
                                .noteCategoryObject(x.getNoteCategoryId() != null
                                        ? new NoteCategoryObject(x.getNoteCategoryId(), x.getNoteCategoryName())
                                        : null)                                .mediaId(x.getMediaId())
                                .createdDate(x.getCreatedDate())
                                .lastChangeDate(x.getLastChangeDate())
                                .build()
                    ).toList());
                    yield resultBuilder.build();
                }
                default -> NoteSearchResponseObject.builder()
                        .responseResultCode(ResponseResultCode.UNEXPECTED_ERROR)
                        .build();
            };
        } catch (Exception e) {
            return NoteSearchResponseObject.builder()
                    .responseResultCode(ResponseResultCode.UNEXPECTED_ERROR)
                    .build();
        }
    }
}