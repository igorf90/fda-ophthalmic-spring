package com.fda.ophthalmic.external;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fda.ophthalmic.dto.SourceStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.ResourceAccessException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
@Repository
public class HttpExternalRepository {
 private final RestClient client;
 private final ObjectMapper objectMapper;
 public HttpExternalRepository(RestClient.Builder builder, ObjectMapper objectMapper,
    @Value("${integration.user-agent}") String userAgent,
    @Value("${integration.timeout-seconds:20}") int timeoutSeconds) {
  this.objectMapper=objectMapper;
  var factory=new org.springframework.http.client.SimpleClientHttpRequestFactory();
  factory.setConnectTimeout(Duration.ofSeconds(Math.min(timeoutSeconds,10)));
  factory.setReadTimeout(Duration.ofSeconds(timeoutSeconds));
  this.client=builder.requestFactory(factory).defaultHeader(HttpHeaders.USER_AGENT,userAgent).build();
 }
 public <T> ExternalResult<T> get(String source, URI uri, JavaType type) {
  Instant now=Instant.now();
  try {
   var response=client.get().uri(uri).accept(MediaType.ALL).exchange((request,res)->{
    int code=res.getStatusCode().value();
    String contentType=res.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
    byte[] bytes=res.getBody().readNBytes(4_000_001);
    if(bytes.length>4_000_000) return new RawResponse(code,contentType,null,"Response exceeds 4MB limit");
    return new RawResponse(code,contentType,bytes,null);
   });
   int code=response.code();
   SourceStatus status=code==404?SourceStatus.NOT_FOUND:code==401||code==403?SourceStatus.BLOCKED:code==429?SourceStatus.RATE_LIMITED:code>=200&&code<300?SourceStatus.AVAILABLE:SourceStatus.ERROR;
   if(status!=SourceStatus.AVAILABLE) return new ExternalResult<>(source,uri.toString(),status,code,now,null,"Upstream HTTP "+code);
   if(response.error()!=null) return new ExternalResult<>(source,uri.toString(),SourceStatus.ERROR,code,now,null,response.error());
   String ctype=response.contentType()==null?"":response.contentType().toLowerCase(Locale.ROOT);
   if(!ctype.contains("json")) return new ExternalResult<>(source,uri.toString(),SourceStatus.ERROR,code,now,null,"Expected JSON; received "+ctype+" (possible interstitial)");
   T data=objectMapper.readValue(response.bytes(),type);
   return new ExternalResult<>(source,uri.toString(),SourceStatus.AVAILABLE,code,now,data,null);
  }catch(ResourceAccessException e){return new ExternalResult<>(source,uri.toString(),SourceStatus.ERROR,null,now,null,"Network error or timeout: "+e.getClass().getSimpleName());}
   catch(Exception e){return new ExternalResult<>(source,uri.toString(),SourceStatus.ERROR,null,now,null,"Unusable upstream response: "+e.getClass().getSimpleName());}
 }
 private record RawResponse(int code,String contentType,byte[] bytes,String error){}
}
