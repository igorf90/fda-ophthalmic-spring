package com.fda.ophthalmic.service;
import com.fda.ophthalmic.dto.*;
import com.fda.ophthalmic.external.*;
import com.fda.ophthalmic.external.model.*;
import com.fda.ophthalmic.mapper.*;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
@Service
public class FdaService {
 private final FdaExternalRepository repository;private final FdaMapper mapper;
 public FdaService(FdaExternalRepository repository,FdaMapper mapper){this.repository=repository;this.mapper=mapper;}
 private <S,T> ApiResult<T> envelope(ExternalResult<S> result,T data){return new ApiResult<>(result.source(),result.url(),result.status(),result.httpStatus(),result.checkedAt(),data,result.message());}
 private <S,D> ApiResult<List<D>> mapped(ExternalResult<OpenFdaResponse<S>> result,Function<S,D> converter){
  List<D> list=result.data()==null||result.data().results()==null?null:result.data().results().stream().map(converter).toList();
  return envelope(result,list);
 }
 public ApiResult<List<K510Dto>> k510(String id){return mapped(repository.k510(id),mapper::toDto);}
 public ApiResult<List<PmaDto>> pma(String id){return mapped(repository.pma(id),mapper::toDto);}
 public ApiResult<List<ClassificationDto>> classification(String code){return mapped(repository.classification(code),mapper::toDto);}
 public ApiResult<List<ProductCodeCountDto>> udiCodes(String id){return mapped(repository.udiCodes(id),mapper::toDto);}
 public ApiResult<JsonNode> manifest(){var result=repository.manifest(); return envelope(result,result.data());}
}
