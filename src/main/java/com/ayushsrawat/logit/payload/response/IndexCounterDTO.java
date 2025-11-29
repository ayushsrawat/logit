package com.ayushsrawat.logit.payload.response;

import lombok.Builder;

@Builder
public record IndexCounterDTO(String index, Integer count) {}
