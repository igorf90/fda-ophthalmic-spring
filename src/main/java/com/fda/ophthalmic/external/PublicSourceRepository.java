package com.fda.ophthalmic.external;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.Map;
@Repository
public class PublicSourceRepository {
 private final HttpExternalRepository http;private final ObjectMapper mapper;
 public PublicSourceRepository(HttpExternalRepository http,ObjectMapper mapper){this.http=http;this.mapper=mapper;}
 public ExternalResult<JsonNode> fetch(String key){
  String url = switch(key){
   case "federal-register" -> "https://www.federalregister.gov/api/v1/documents.json?per_page=1";
   case "ecfr" -> "https://www.ecfr.gov/api/versioner/v1/versions/title-21.json?part=820";
   case "guidance" -> "https://www.fda.gov/files/api/datatables/static/search-for-guidance.json";
   case "fda-510k-page" -> "https://www.accessdata.fda.gov/scripts/cdrh/cfdocs/cfpmn/pmn.cfm?ID=K251848";
   default -> throw new IllegalArgumentException("Unknown source");
  };
  return http.get(key,URI.create(url),mapper.constructType(JsonNode.class));
 }
}
