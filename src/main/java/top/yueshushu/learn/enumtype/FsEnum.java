package top.yueshushu.learn.enumtype;

public enum FsEnum {

    BK_Info("m:90+t:{0}+f:!50", "", "板块", "板块类型"),
    BK_STOCK("b:{0}+f:!50", "", "板块成分股", "板块代码小写"),
    ALL_STOCK("m:0+t:6,m:0+t:13,m:0+t:80,m:1+t:2,m:1+t:23,b:MK0021,b:MK0022,b:MK0023,b:MK0024",
            "f2,f12,f13,f14", "所有的股票信息", ""),
    SYNC_EASY_MONEY_TYPE("syncEasyMoneyType", "", "同步东财数据类型", "同步东财数据类型"),
    ;
    public final String code;
    public final String field;
    public final String desc;
    public final String param;

    FsEnum(String code, String field, String desc, String param) {
        this.code = code;
        this.field = field;
        this.desc = desc;
        this.param = param;
    }
}
