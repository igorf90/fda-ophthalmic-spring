
package com.fda.ophthalmic.external;

import com.fda.ophthalmic.external.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Repository
public class FdaExternalRepository {

    private static final String API = "https://api.fda.gov";

    private final HttpExternalRepository http;
    private final ObjectMapper mapper;

    public FdaExternalRepository(
            HttpExternalRepository http,
            ObjectMapper mapper
    ) {
        this.http = http;
        this.mapper = mapper;
    }

    private URI uri(String path, String key, String value) {
        return UriComponentsBuilder
                .fromUriString(API + path)
                .queryParam(key, value)
                .build()
                .encode()
                .toUri();
    }

    private <T> JavaType openType(Class<T> type) {
        return mapper.getTypeFactory()
                .constructParametricType(OpenFdaResponse.class, type);
    }

    public ExternalResult<OpenFdaResponse<K510External>> k510(String number) {
        return http.get(
                "openFDA 510k",
                uri("/device/510k.json", "search", "k_number:\"" + number + "\""),
                openType(K510External.class)
        );
    }

    public ExternalResult<OpenFdaResponse<PmaExternal>> pma(String number) {
        return http.get(
                "openFDA PMA",
                uri("/device/pma.json", "search", "pma_number:\"" + number + "\""),
                openType(PmaExternal.class)
        );
    }

    public ExternalResult<OpenFdaResponse<ClassificationExternal>> classification(String code) {
        return http.get(
                "openFDA classification",
                uri("/device/classification.json", "search", "product_code:\"" + code + "\""),
                openType(ClassificationExternal.class)
        );
    }

    public ExternalResult<OpenFdaResponse<CountExternal>> udiCodes(String number) {
        URI uri = UriComponentsBuilder
                .fromUriString(API + "/device/udi.json")
                .queryParam(
                        "search",
                        "premarket_submissions.submission_number:\"" + number + "\""
                )
                .queryParam("count", "product_codes.code.exact")
                .build()
                .encode()
                .toUri();

        return http.get(
                "openFDA UDI",
                uri,
                openType(CountExternal.class)
        );
    }

    public ExternalResult<JsonNode> manifest() {
        return http.get(
                "openFDA bulk manifest",
                URI.create(API + "/download.json"),
                mapper.constructType(JsonNode.class)
        );
    }
}
