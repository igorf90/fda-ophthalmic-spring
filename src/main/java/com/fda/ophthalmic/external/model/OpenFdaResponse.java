package com.fda.ophthalmic.external.model;
import java.util.List;
public record OpenFdaResponse<T>(List<T> results) {}
