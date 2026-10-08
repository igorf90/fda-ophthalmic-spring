package com.fda.ophthalmic.dto;
import java.time.Instant;
import java.util.List;
public record FdaPageCheckDto(String source, String url, String finalUrl, SourceStatus status,
 int upstreamHttpStatus, String contentType, Instant checkedAt, List<Hop> redirects,
 String message) {
 public record Hop(int httpStatus, String url, String location) {}
}
