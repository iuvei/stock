package com.cgs.service.graphs;

import com.cgs.cache.TrendCache;
import com.cgs.entity.graphs.MarketPrice;
import com.cgs.service.graphs.interfaces.IHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrendService implements IHandler {

  @Autowired
  private TrendCache trendCache;

  @Override
  public void handle(MarketPrice marketPrice) {
    trendCache.put(marketPrice);
  }
}
