package org.skyhigh.notessearchservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notessearchservice.model.entity.MediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MediaIdRepository extends JpaRepository<MediaId, Long> {
    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "UPDATE public.media_ids SET media_id = ?1 WHERE media_id = ?2", nativeQuery = true)
    void updateMediaIdByMediaId(UUID newMediaId, UUID oldMediaId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "DELETE FROM public.media_ids WHERE media_id = ?1", nativeQuery = true)
    void deleteByMediaId(UUID mediaId);

    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "DELETE FROM public.media_ids WHERE note_id = ?1", nativeQuery = true)
    void deleteByNoteId(Long noteId);

    List<MediaId> findByNoteId(Long noteId);
}
