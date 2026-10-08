package com.fda.ophthalmic.external;
import com.fda.ophthalmic.dto.SourceStatus;
import java.time.Instant;
public record ExternalResult<T>(String source, String url, SourceStatus status, Integer httpStatus, Instant checkedAt, T data, String message) {}
