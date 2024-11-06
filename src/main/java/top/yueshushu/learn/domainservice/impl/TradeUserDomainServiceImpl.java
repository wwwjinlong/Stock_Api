package top.yueshushu.learn.domainservice.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import top.yueshushu.learn.domain.TradeUserDo;
import top.yueshushu.learn.domainservice.TradeUserDomainService;
import top.yueshushu.learn.mapper.TradeUserDoMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description TODO
 * @Author yuejianli
 * @Date 2022/5/21 6:19
 **/
@Service
public class TradeUserDomainServiceImpl extends ServiceImpl<TradeUserDoMapper, TradeUserDo>
        implements TradeUserDomainService {
    @Override
    public TradeUserDo getByUserId(Integer userId) {

        List<TradeUserDo> list = this.lambdaQuery()
                .eq(
                        TradeUserDo::getUserId, userId
                ).list();
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Integer> listUserIds() {
        return this.lambdaQuery()
                .list()
                .stream().map(
                        TradeUserDo::getUserId
                ).collect(Collectors.toList());
    }
}
