package top.yueshushu.learn.crawler.parse.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.yueshushu.learn.crawler.entity.*;
import top.yueshushu.learn.crawler.parse.StockInfoParser;
import top.yueshushu.learn.enumtype.DBStockType;
import top.yueshushu.learn.enumtype.StockCodeType;
import top.yueshushu.learn.enumtype.StockPoolQsMessage;
import top.yueshushu.learn.enumtype.StockPoolType;
import top.yueshushu.learn.util.BigDecimalUtil;
import top.yueshushu.learn.util.MyDateUtil;
import top.yueshushu.learn.util.StockUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * 股票转换信息实现
 * @author 12905
 */
@Component("defaultStockInfoParser")
public class DefaultStockInfoParser implements StockInfoParser {

    @Override
    public List<DownloadStockInfo> parseStockInfoList(String content) {
        //将内容转换成json
        JSONObject jsonObject = JSONObject.parseObject(content);
        //获取里面的data.diff 内容，是个列表对象
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");
        //处理内容
        List<DownloadStockInfo> result = new ArrayList<>(32);
        jsonArray.stream().forEach(
                n->{
                    JSONObject tempObject = JSONObject.parseObject(n.toString());
                    DownloadStockInfo downloadStockInfo = new DownloadStockInfo();
                    downloadStockInfo.setCode(tempObject.getString("f12"));
                    downloadStockInfo.setName(tempObject.getString("f14"));

                    //处理类型  1为上海   0为深圳
                    int type=tempObject.getInteger("f13");
                    //进行处理
                    downloadStockInfo.setExchange(type);
                    //设置股票的全称
                    downloadStockInfo.setFullCode(StockUtil.getFullCode(type, downloadStockInfo.getCode()));

                    result.add(downloadStockInfo);
                }
        );
        return result;
    }


    @Override
    public List<BKInfo> parseBkInfoList(String content) {
        //将内容转换成json
        JSONObject jsonObject = JSONObject.parseObject(content);
        //获取里面的data.diff 内容，是个列表对象
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");
        //处理内容
        List<BKInfo> result = new ArrayList<>(32);
        jsonArray.stream().forEach(
                n -> {
                    JSONObject tempObject = JSONObject.parseObject(n.toString());
                    BKInfo bkInfo = new BKInfo();
                    bkInfo.setCode(tempObject.getString("f12"));
                    bkInfo.setName(tempObject.getString("f14"));
                    result.add(bkInfo);
                }
        );
        return result;
    }

    @Override
    public List<BKMoneyInfo> parseTodayBKMoneyInfoList(String content) {
        //将内容转换成json
        JSONObject jsonObject = JSONObject.parseObject(content);
        //获取里面的data.diff 内容，是个列表对象
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");
        //处理内容
        Date now = DateUtil.date();
        List<BKMoneyInfo> result = new ArrayList<>(32);
        jsonArray.stream().forEach(
                n -> {
                    JSONObject tempObject = JSONObject.parseObject(n.toString());
                    BKMoneyInfo bkMoneyInfo = new BKMoneyInfo();
                    bkMoneyInfo.setBkCode(tempObject.getString("f12"));
                    bkMoneyInfo.setBkName(tempObject.getString("f14"));
                    bkMoneyInfo.setCurrentDate(now);
                    bkMoneyInfo.setBkNowPrice(tempObject.getString("f2"));
                    bkMoneyInfo.setBkNowProportion(tempObject.getString("f13"));
                    bkMoneyInfo.setMarket(tempObject.getInteger("f13"));
                    bkMoneyInfo.setTodayMainInflow(tempObject.getString("f62"));
                    bkMoneyInfo.setTodayMainInflowProportion(tempObject.getString("f184"));
                    bkMoneyInfo.setTodaySuperInflow(tempObject.getString("f66"));
                    bkMoneyInfo.setTodaySuperInflowProportion(tempObject.getString("f69"));
                    bkMoneyInfo.setTodayMoreInflow(tempObject.getString("f72"));
                    bkMoneyInfo.setTodayMoreInflowProportion(tempObject.getString("f75"));
                    bkMoneyInfo.setTodayMiddleInflow(tempObject.getString("f78"));
                    bkMoneyInfo.setTodayMiddleInflowProportion(tempObject.getString("f81"));
                    bkMoneyInfo.setTodaySmallInflow(tempObject.getString("f84"));
                    bkMoneyInfo.setTodaySmallInflowProportion(tempObject.getString("f87"));
                    bkMoneyInfo.setTodayMainInflowCode(tempObject.getString("f205"));
                    bkMoneyInfo.setTodayMainInflowName(tempObject.getString("f204"));
                    result.add(bkMoneyInfo);
                }
        );
        return result;
    }

    @Override
    public List<DBStockInfo> parseDbStockInfoList(String content, DBStockType dbStockType) {
        if (dbStockType == null) {
            return Collections.emptyList();
        }
        //将内容转换成json
        JSONObject jsonObject = JSONObject.parseObject(content);
        //获取里面的data.diff 内容，是个列表对象
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");
        //处理内容
        List<DBStockInfo> result = new ArrayList<>(32);
        jsonArray.stream().forEach(
                n -> {
                    JSONObject tempObject = JSONObject.parseObject(n.toString());
                    // 获取股票的编码
                    String code = tempObject.getString("f12");
                    // 获取股票的类型
                    StockCodeType typeByStockCode = StockCodeType.getTypeByStockCode(code);
                    if (typeByStockCode != null) {
                        // 类型是否包含此信息.
                        if (dbStockType.contains(typeByStockCode)) {
                            DBStockInfo dbStockInfo = new DBStockInfo();
                            dbStockInfo.setCode(code);
                            dbStockInfo.setAmplitude(tempObject.getInteger("f3"));
                            dbStockInfo.setName(tempObject.getString("f14"));
                            dbStockInfo.setLimitPrice(tempObject.getInteger("f350"));
                            dbStockInfo.setNowPrice(tempObject.getInteger("f2"));
                            result.add(dbStockInfo);
                        }
                    }
                }
        );
        return result;
    }

    @Override
    public List<StockBKStockInfo> parseBkStockList(String content, String code) {
        //将内容转换成json
        JSONObject jsonObject = JSONObject.parseObject(content);
        //获取里面的data.diff 内容，是个列表对象
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");
        //处理内容
        List<StockBKStockInfo> result = new ArrayList<>(32);
        jsonArray.stream().forEach(
                n -> {
                    JSONObject tempObject = JSONObject.parseObject(n.toString());

                    StockBKStockInfo stockBKStockInfo = new StockBKStockInfo();
                    stockBKStockInfo.setStockCode(code);
                    stockBKStockInfo.setAmplitude(tempObject.getInteger("f3"));
                    stockBKStockInfo.setBkCode(tempObject.getString("f12"));
                    stockBKStockInfo.setBkName(tempObject.getString("f14"));
                    result.add(stockBKStockInfo);
                }
        );
        return result;
    }

    @Override
    public List<StockPoolInfo> parsePoolInfoList(String content, StockPoolType stockPoolType, Date currentDate) {
        //将内容转换成json
        JSONObject jsonObject = JSONObject.parseObject(content);
        //获取里面的data.diff 内容，是个列表对象
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("pool");
        if (jsonArray.size() <= 0) {
            return Collections.emptyList();
        }
        //处理内容
        List<StockPoolInfo> result = new ArrayList<>(32);
        String dateStr = DateUtil.format(currentDate, DatePattern.PURE_DATE_PATTERN);
        jsonArray.stream().forEach(
                n -> {
                    JSONObject tempObject = JSONObject.parseObject(n.toString());
                    StockPoolInfo stockPoolInfo = new StockPoolInfo();
                    stockPoolInfo.setCode(tempObject.getString("c"));
                    stockPoolInfo.setName(tempObject.getString("n"));
                    stockPoolInfo.setCurrDate(currentDate);
                    stockPoolInfo.setStockPoolType(stockPoolType);
                    stockPoolInfo.setNowPrice(BigDecimalUtil.convertTwo(new BigDecimal(tempObject.getInteger("p") / 1000.00)));
                    stockPoolInfo.setAmplitude(BigDecimalUtil.convertTwo(new BigDecimal(tempObject.getString("zdp"))));
                    stockPoolInfo.setTradingValue(new BigDecimal("0"));
                    stockPoolInfo.setFloatMarket(BigDecimalUtil.convertZero(new BigDecimal(tempObject.getString("ltsz"))));
                    stockPoolInfo.setTotalMarket(BigDecimalUtil.convertTwo(new BigDecimal(tempObject.getString("tshare"))));
                    stockPoolInfo.setChangingProportion(BigDecimalUtil.convertTwo(new BigDecimal(tempObject.getString("hs"))));
                    stockPoolInfo.setSealingMoney(StringUtils.hasText(tempObject.getString("fund")) ? BigDecimalUtil.convertZero(new BigDecimal(tempObject.getString("fund"))) : null);
                    stockPoolInfo.setStartTime(MyDateUtil.convertToTodayDate(dateStr, tempObject.getString("fbt")));
                    stockPoolInfo.setEndTime(MyDateUtil.convertToTodayDate(dateStr, tempObject.getString("lbt")));
                    stockPoolInfo.setZbCount(tempObject.getInteger("zbc"));
                    stockPoolInfo.setLbCount(tempObject.getInteger("lbc"));
                    stockPoolInfo.setBkName(tempObject.getString("hybk"));
                    String zttJContent = tempObject.getString("zttj");
                    if (StringUtils.hasText(zttJContent)) {
                        // 转换成 Map
                        JSONObject tempZttjObject = JSONObject.parseObject(zttJContent);
                        stockPoolInfo.setStatCount(tempZttjObject.getInteger("ct"));
                        stockPoolInfo.setStatDay(tempZttjObject.getInteger("days"));
                    }
                    switch (stockPoolType) {
                        case DT: {
                            stockPoolInfo.setDays(tempObject.getInteger("days"));
                            stockPoolInfo.setOcCount(tempObject.getInteger("oc"));
                            break;
                        }
                        case YES_ZT: {
                            stockPoolInfo.setZfProportion(BigDecimalUtil.convertTwo(new BigDecimal(tempObject.getString("zf"))));
                            break;
                        }
                        case QS: {
                            stockPoolInfo.setNg(tempObject.getInteger("nh"));
                            stockPoolInfo.setLb(BigDecimalUtil.convertTwo(new BigDecimal(tempObject.getString("lb"))));
                            stockPoolInfo.setReason(Optional.ofNullable(StockPoolQsMessage.getPoolQsType(tempObject.getInteger("cc"))).map(cc -> cc.getDesc()).orElse(""));
                            break;
                        }
                        case CX: {
                            stockPoolInfo.setOds(tempObject.getInteger("ods"));
                            stockPoolInfo.setStartDate(MyDateUtil.convertDateNum(tempObject.getString("ipod")));
                            stockPoolInfo.setOpenDate(MyDateUtil.convertDateNum(tempObject.getString("od")));
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    result.add(stockPoolInfo);
                }
        );
        return result;
    }
}
