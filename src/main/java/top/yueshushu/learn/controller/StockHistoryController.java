package top.yueshushu.learn.controller;


import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.yueshushu.learn.business.StockHistoryBusiness;
import top.yueshushu.learn.common.ResultCode;
import top.yueshushu.learn.mode.ro.StockDayStatRo;
import top.yueshushu.learn.mode.ro.StockRo;
import top.yueshushu.learn.response.OutputResult;
import top.yueshushu.learn.response.PageResponse;

/**
 * <p>
 * 股票的历史交易记录表 我是自定义的
 * </p>
 *
 * @author 岳建立
 * @date 2022-01-02
 */
@RestController
@RequestMapping("/stockHistory")
@Api("查询股票的历史")
public class StockHistoryController {
    @Resource
    private StockHistoryBusiness stockHistoryBusiness;
    @ApiOperation("查询股票的历史记录")
    @PostMapping("/history")
    public OutputResult history(@RequestBody StockRo stockRo){
        if (!StringUtils.hasText(stockRo.getCode())){
            return OutputResult.buildSucc(
                    PageResponse.emptyPageResponse()
            );
        }
        return stockHistoryBusiness.listHistory(stockRo);
    }
    @ApiOperation("查看天范围统计的历史记录")
    @PostMapping("/listDayRange")
    public OutputResult listDayRange(@RequestBody StockDayStatRo stockDayStatRo){
        if (!StringUtils.hasText(stockDayStatRo.getCode())){
            return OutputResult.buildSucc(
                    PageResponse.emptyPageResponse()
            );
        }
        if (!StringUtils.hasText(stockDayStatRo.getStartDate())){
            return OutputResult.buildAlert(
                    ResultCode.HISTORY_START_DATE
            );
        }
        if (!StringUtils.hasText(stockDayStatRo.getEndDate())){
            return OutputResult.buildAlert(
                    ResultCode.HISTORY_END_DATE
            );
        }
        if (stockDayStatRo.getStartNum() == null) {
            return OutputResult.buildAlert(
                    ResultCode.HISTORY_START_DAY_NUM
            );
        }
        if (stockDayStatRo.getEndDayNum() == null) {
            return OutputResult.buildAlert(
                    ResultCode.HISTORY_END_DAY_NUM
            );
        }
        if (stockDayStatRo.getMonth() != null) {
            if (stockDayStatRo.getMonth() < 0 || stockDayStatRo.getMonth() > 12) {
                return OutputResult.buildAlert(ResultCode.ALERT);
            }
            if (stockDayStatRo.getMonth() == 0) {
                stockDayStatRo.setMonth(null);
            }
        }
        return stockHistoryBusiness.listDayRange(stockDayStatRo);
    }


}
