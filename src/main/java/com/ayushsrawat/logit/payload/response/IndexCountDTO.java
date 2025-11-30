package com.ayushsrawat.logit.payload.response;

import lombok.Builder;

@Builder
public record IndexCountDTO(String index, Integer count) {}
