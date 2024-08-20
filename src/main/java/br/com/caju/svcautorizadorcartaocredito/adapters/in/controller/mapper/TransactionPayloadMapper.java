package br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.mapper;

import br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.request.TransactionPayloadRequest;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.TransactionPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionPayloadMapper {
    @Mapping(target = "id", ignore = true)
    TransactionPayload toTransactionPayload(TransactionPayloadRequest transactionPayloadRequest);
}
