package org.skyhigh.notessearchservice.service;


import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.shyhigh.grpc.notes.NoteSearchRequest;
import org.shyhigh.grpc.notes.NoteSearchResponse;
import org.shyhigh.grpc.notes.NoteSearchServiceGrpc;
import org.skyhigh.notessearchservice.common.NoteMapper;
import org.skyhigh.notessearchservice.validation.exception.GrpcResponseException;

@GrpcService
@RequiredArgsConstructor
public class NoteSearchServiceImpl extends NoteSearchServiceGrpc.NoteSearchServiceImplBase {
    private final ValidationService validationService;
    private final InternalNoteSearchService internalNoteSearchService;
    private final NoteMapper noteMapper;

    @Override
    public void searchNotes(NoteSearchRequest request, StreamObserver<NoteSearchResponse> responseObserver) {
        try {
            validationService.validateNoteSearch(request);
            responseObserver.onNext(noteMapper.map(internalNoteSearchService
                    .searchNotes(noteMapper.map(request))));
            responseObserver.onCompleted();
        } catch (GrpcResponseException e) {
            responseObserver.onNext(NoteSearchResponse.newBuilder()
                    .setResponseResultCode(e.getResponseResultCode())
                    .build());
            responseObserver.onCompleted();
        }
    }
}
