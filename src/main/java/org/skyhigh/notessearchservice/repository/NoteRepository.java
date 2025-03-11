package org.skyhigh.notessearchservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notessearchservice.model.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "INSERT INTO public.notes VALUES (" +
            "?1,?2,?3,?4,?5,?6,?7,?8,?9,?10," +
            "to_tsvector('public.ru',?3)," +
            "to_tsvector('public.ru',?10)," +
            "to_tsvector('public.ru',?3||' '||coalesce(?10,''))" +
            ")",
            nativeQuery = true
    ) void saveEntity(
            Long id,
            Long userId,
            String name,
            Long categoryId,
            String categoryName,
            String textExtraction,
            UUID mediaId,
            ZonedDateTime createdDate,
            ZonedDateTime lastChangeDate,
            String noteContent
    );

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.notes " +
            "SET name=?2,note_category_id=?3,note_category_name=?4," +
            "last_change_date=?5,tsvector_name=to_tsvector('public.ru',?2) " +
            "WHERE id = ?1",
            nativeQuery = true
    ) void updateNoteNameAndCategoryAndLastChangeDate(
            Long id,
            String newNoteName,
            Long newCategoryId,
            String newCategoryName,
            ZonedDateTime lastChangeDate
    );

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.notes " +
            "SET note_category_name=?2, last_change_date=?3 WHERE id = ?1",
            nativeQuery = true
    ) void updateNoteCategoryNameAndLastChangeDate(
            Long id,
            String newCategoryName,
            ZonedDateTime lastChangeDate
    );

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.notes " +
            "SET note_category_name=?2 WHERE id = ?1",
            nativeQuery = true
    ) void updateNoteCategoryName(
            Long id,
            String newCategoryName
    );

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE public.tags SET tag_name = ?3 WHERE note_id = ?1 AND tag_id = ?2", nativeQuery = true)
    void updateNoteTagName(
            Long id,
            Long tagId,
            String newTagName
    );

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.notes " +
            "SET text_extraction=?2,media_id=?3,note_content=?4," +
            "tsvector_content=to_tsvector('public.ru',?4),last_change_date=?5 " +
            "WHERE id = ?1",
            nativeQuery = true
    ) void updateNoteTextExtractionAndMediaIdAndNoteContentAndLastChangeDate(
            Long id,
            String textExtraction,
            UUID mediaId,
            String noteContent,
            ZonedDateTime lastChangeDate
    );

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.notes " +
            "SET last_change_date=?2 " +
            "WHERE id = ?1",
            nativeQuery = true
    ) void updateNoteLastChangeDate(
            Long id,
            ZonedDateTime lastChangeDate
    );

    @Query(value = "SELECT n.id, n.user_id, n.name, n.note_category_id, n.note_category_name, n.text_extraction, n.media_id, n.created_date, n.last_change_date, n.note_content " +
            "FROM public.notes n " +
            "WHERE n.user_id=?1 " +
            "AND n.tsvector_name_content @@ websearch_to_tsquery('public.ru',?2) " +
            "ORDER BY ts_rank(n.tsvector_name_content, websearch_to_tsquery('public.ru',?2)) DESC",
            nativeQuery = true
    ) List<Note> findNoteByUserIdAndQuery(Long userId, String query);

    @Query(value = "SELECT n.id, n.user_id, n.name, n.note_category_id, n.note_category_name, n.text_extraction, n.media_id, n.created_date, n.last_change_date, n.note_content " +
            "FROM public.notes n WHERE n.id = ?1", nativeQuery = true)
    Note findNoteById(Long noteId);

    @Query(value = "SELECT n.id, n.user_id, n.name, n.note_category_id, n.note_category_name, n.text_extraction, n.media_id, n.created_date, n.last_change_date, n.note_content " +
            "FROM public.notes n WHERE n.user_id = ?1 AND (n.name LIKE ?2 OR n.note_content LIKE ?2)", nativeQuery = true)
    List<Note> findNoteByPartEqualityNonNormalized(Long userId, String query);

    @Query(value = "SELECT n.id, n.user_id, n.name, n.note_category_id, n.note_category_name, n.text_extraction, n.media_id, n.created_date, n.last_change_date, n.note_content " +
            "FROM public.notes n WHERE n.user_id = ?1 AND (n.name ILIKE ?2 OR n.note_content ILIKE ?2)", nativeQuery = true)
    List<Note> findNoteByPartEqualityNormalized(Long userId, String query);
}
