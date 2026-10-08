package com.fda.ophthalmic.dto;
import java.time.Instant;
public record ApiResult<T>(String source, String url, SourceStatus status, Integer upstreamHttpStatus, Instant checkedAt, T data, String message) {}
