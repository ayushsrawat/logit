package com.ayushsrawat.logit.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

  private String index;
  private String query;
  private Integer topN;
  private List<String> fields;
  private SearchTimeRange timeRange;
  private boolean stem = true;

  @Override
  public String toString() {
    return "SearchRequest{" + "index='" + index + '\'' + ", query='" + query + '\'' + ", topN=" + topN + ", fields=" + fields + ", timeRange =" + timeRange +  ", stem=" + stem + '}';
  }

}
