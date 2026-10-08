package com.fda.ophthalmic.external;
import com.fda.ophthalmic.dto.FdaPageCheckDto;
import com.fda.ophthalmic.dto.SourceStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
@Repository
public class FdaPageRepository {
 private final HttpClient client;
 private final String userAgent;
 public FdaPageRepository(@Value("${integration.user-agent}") String userAgent) {
  this.userAgent=userAgent;
  this.client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
   .followRedirects(HttpClient.Redirect.NEVER).build();
 }
 public FdaPageCheckDto check(String kNumber) {
  URI start=URI.create("https://www.accessdata.fda.gov/scripts/cdrh/cfdocs/cfpmn/pmn.cfm?ID="+kNumber);
  URI current=start;
  List<FdaPageCheckDto.Hop> hops=new ArrayList<>();
  Instant checkedAt=Instant.now();
  try {
   for(int count=0;count<=8;count++) {
    HttpRequest req=HttpRequest.newBuilder(current).timeout(Duration.ofSeconds(30))
     .header("User-Agent",userAgent).header("Accept","text/html,*/*")
     .GET().build();
    HttpResponse<java.io.InputStream> res=client.send(req,HttpResponse.BodyHandlers.ofInputStream());
    // Close without needing to parse HTML; avoid downloading an unbounded body.
    try(var ignored=res.body()) {}
    int code=res.statusCode();
    String contentType=res.headers().firstValue("content-type").orElse("");
    String location=res.headers().firstValue("location").orElse(null);
    hops.add(new FdaPageCheckDto.Hop(code,current.toString(),location));
    if(code>=300 && code<400 && location!=null) {
     URI next=current.resolve(location);
     if(!next.getScheme().equalsIgnoreCase("https") ||
        !next.getHost().toLowerCase(Locale.ROOT).endsWith(".fda.gov")) {
      return result(start,current,SourceStatus.ERROR,code,contentType,checkedAt,hops,"Unsafe redirect destination");
     }
     current=next;
     continue;
    }
    boolean blocked=current.toString().contains("abuse-detection") ||
      hops.stream().anyMatch(h->h.location()!=null && h.location().contains("abuse-detection"));
    SourceStatus status=blocked || code==401 || code==403 ? SourceStatus.BLOCKED :
     code==429?SourceStatus.RATE_LIMITED:code==404?SourceStatus.NOT_FOUND:
     code>=200 && code<300 && contentType.toLowerCase(Locale.ROOT).contains("html")?
     SourceStatus.AVAILABLE:SourceStatus.ERROR;
    String msg=blocked?"FDA/Akamai abuse-detection redirect detected":
     status==SourceStatus.AVAILABLE?"HTML page reachable (content not parsed)":"Upstream HTTP "+code;
    return result(start,current,status,code,contentType,checkedAt,hops,msg);
   }
   return result(start,current,SourceStatus.ERROR,0,"",checkedAt,hops,"Too many redirects");
  } catch(Exception e) {
   if(e instanceof InterruptedException) Thread.currentThread().interrupt();
   return result(start,current,SourceStatus.ERROR,0,"",checkedAt,hops,
     "Connection or timeout failure: "+e.getClass().getSimpleName());
  }
 }
 private FdaPageCheckDto result(URI start,URI finalUrl,SourceStatus status,int code,String type,
  Instant checkedAt,List<FdaPageCheckDto.Hop> hops,String message) {
  return new FdaPageCheckDto("fda-510k-page",start.toString(),finalUrl.toString(),
   status,code,type,checkedAt,List.copyOf(hops),message);
 }
}
