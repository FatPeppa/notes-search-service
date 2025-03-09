package org.skyhigh.notessearchservice.repository;

import jakarta.transaction.Transactional;
import org.skyhigh.notessearchservice.model.entity.NormalizedNoteSearchInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NormalizedNoteSearchInfoRepository extends JpaRepository<NormalizedNoteSearchInfo, Long> {
    @Transactional
    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query(value = "DELETE FROM public.normalized_note_search_info WHERE note_id = ?1", nativeQuery = true)
    void deleteByNoteId(Long noteId);

    @Query(value = "SELECT nnsi.id, nnsi.note_id, nnsi.user_id, nnsi.normalized_name, nnsi.normalized_note_content " +
            "FROM public.normalized_note_search_info nnsi " +
            "WHERE nnsi.user_id=?1 " +
            "AND nnsi.tsvector_name_content @@ websearch_to_tsquery('public.ru',?2) " +
            "ORDER BY ts_rank(nnsi.tsvector_name_content, websearch_to_tsquery('public.ru',?2)) DESC",
            nativeQuery = true
    )
    List<NormalizedNoteSearchInfo> findNormalizedNoteSearchInfoByUserIdAndQuery(Long userId, String query);

    @Query(value = "SELECT nnsi.id, nnsi.note_id, nnsi.user_id, nnsi.normalized_name, nnsi.normalized_note_content " +
            "FROM public.normalized_note_search_info nnsi WHERE n.id = ?1", nativeQuery = true)
    NormalizedNoteSearchInfo findNormalizedNoteSearchInfoById(Long noteId);

    @Query(value = "SELECT nnsi.id, nnsi.note_id, nnsi.user_id, nnsi.normalized_name, nnsi.normalized_note_content " +
            "FROM public.normalized_note_search_info nnsi WHERE nnsi.user_id = ?1 AND (nnsi.normalized_name ILIKE ?2 OR nnsi.normalized_note_content ILIKE ?2)", nativeQuery = true)
    List<NormalizedNoteSearchInfo> findNormalizedNoteSearchInfoByPartEquality(Long userId, String query);
}
