package top.yueshushu.learn.crawler.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.yueshushu.learn.crawler.crawler.CrawlerService;
import top.yueshushu.learn.crawler.service.RealTimePriceService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新浪接口获取股票的价格
 *
 * @author yuejianli
 * @date 2023-01-12
 */
@Service("realTimePriceService3")
@Slf4j
public class XueQiuRealTimePriceServiceImpl implements RealTimePriceService {
    @Resource
    private CrawlerService crawlerService;

    @Override
    public BigDecimal getNowPrice(String code, String fullCode) {
        return batchGetNowPrice(Collections.singletonList(code), Collections.singletonList(fullCode)).get(code);
    }

    @Override
    public Map<String, BigDecimal> batchGetNowPrice(List<String> codeList, List<String> fullCodeList) {
        List<String> upperList = fullCodeList.stream().map(n -> n.toUpperCase()).collect(Collectors.toList());
        return crawlerService.xueQiuGetPrice(upperList);
    }
}
