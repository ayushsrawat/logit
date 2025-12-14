package com.ayushsrawat.logit.payload.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchTimeRange {

  private String start;
  private String end;

}