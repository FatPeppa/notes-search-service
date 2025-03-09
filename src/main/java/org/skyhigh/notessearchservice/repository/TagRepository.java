package org.skyhigh.notessearchservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notessearchservice.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.tags SET tag_name = ?1 WHERE note_id = ?2 AND tag_id = ?3", nativeQuery = true)
    void updateTagNameByNoteIdAndTagId(String tagName, Long noteId, Long tagId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "DELETE FROM public.tags WHERE note_id = ?1 AND tag_id = ?2", nativeQuery = true)
    void deleteTagByNoteIdAndTagId(Long noteId, Long tagId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "DELETE FROM public.tags WHERE note_id = ?1", nativeQuery = true)
    void deleteByNoteId(Long noteId);

    @Query(value = "SELECT * FROM public.tags t WHERE t.note_id = ?1 AND t.tag_id = ?2", nativeQuery = true)
    Tag findTagByNoteIdAndTagId(Long noteId, Long tagId);

    @Query(value = "SELECT * FROM public.tags t WHERE t.note_id = ?1", nativeQuery = true)
    List<Tag> findTagsByNoteId(Long noteId);
}
