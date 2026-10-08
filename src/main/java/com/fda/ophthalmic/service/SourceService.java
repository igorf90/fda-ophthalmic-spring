package com.fda.ophthalmic.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fda.ophthalmic.dto.ApiResult;
import com.fda.ophthalmic.external.PublicSourceRepository;
import org.springframework.stereotype.Service;
@Service public class SourceService {
 private final PublicSourceRepository repository;
 public SourceService(PublicSourceRepository repository){this.repository=repository;}
 public ApiResult<JsonNode> check(String key){
  var r=repository.fetch(key);
  return new ApiResult<>(r.source(),r.url(),r.status(),r.httpStatus(),r.checkedAt(),r.data(),r.message());
 }
}
