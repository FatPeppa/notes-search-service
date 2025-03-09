package org.skyhigh.notessearchservice.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.shyhigh.grpc.notes.*;
import org.skyhigh.notessearchservice.common.NoteMapper;
import org.skyhigh.notessearchservice.validation.exception.GrpcResponseException;

@GrpcService
@RequiredArgsConstructor
public class NoteDataModifyingServiceImpl extends NoteDataModifyingServiceGrpc.NoteDataModifyingServiceImplBase {
    private final NoteMapper noteMapper;
    private final ValidationService validationService;
    private final InternalNoteDataModifyingService internalNoteDataModifyingService;

    @Override
    public void createNote(NoteCreateRequest request, StreamObserver<DataModifyingResponse> responseObserver) {
        try {
            validationService.validateNoteCreation(request);
            internalNoteDataModifyingService.createNote(noteMapper.map(request));
            responseObserver.onNext(DataModifyingResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.CREATE_SUCCESS)
                    .build());
            responseObserver.onCompleted();
        } catch (GrpcResponseException e) {
            responseObserver.onNext(DataModifyingResponse.newBuilder()
                    .setResponseResultCode(e.getResponseResultCode())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateNote(NoteUpdateRequest request, StreamObserver<DataModifyingResponse> responseObserver) {
        try {
            validationService.validateNoteUpdate(request);
            internalNoteDataModifyingService.updateNote(noteMapper.map(request));
            responseObserver.onNext(DataModifyingResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.UPDATE_SUCCESS)
                    .build());
            responseObserver.onCompleted();
        } catch (GrpcResponseException e) {
            responseObserver.onNext(DataModifyingResponse.newBuilder()
                    .setResponseResultCode(e.getResponseResultCode())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteNote(NoteDeleteRequest request, StreamObserver<DataModifyingResponse> responseObserver) {
        try {
            validationService.validateNoteDelete(request);
            internalNoteDataModifyingService.deleteNote(noteMapper.map(request));
            responseObserver.onNext(DataModifyingResponse.newBuilder()
                    .setResponseResultCode(ResponseResultCode.DELETE_SUCCESS)
                    .build());
            responseObserver.onCompleted();
        } catch (GrpcResponseException e) {
            responseObserver.onNext(DataModifyingResponse.newBuilder()
                    .setResponseResultCode(e.getResponseResultCode())
                    .build());
            responseObserver.onCompleted();
        }
    }
}
