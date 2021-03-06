package com.jd.poporder.slots.flow;

import com.jd.poporder.factory.PopNamedThreadFactory;
import com.jd.poporder.listener.PopPropertyListener;
import com.jd.poporder.property.PopDynamicProperty;
import com.jd.poporder.property.Property;
import com.jd.poporder.slots.rule.PopFlowRule;
import com.jd.poporder.utils.PopFlowRuleUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @ClassName FlowRuleManager
 * @Description FlowRuleManager
 * @Author liudianfei3
 * @Date 2020/9/4 11:11
 * @Version 1.0
 */
public class PopFlowRuleManager {
    private static final Map<String, List<PopFlowRule>> flowRules = new ConcurrentHashMap<>();
    private static final PopPropertyListener LISTENER = new PopFlowPropertyListener();
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1,new PopNamedThreadFactory("pop-metrics-record-task",true));
    private static final Property<List<PopFlowRule>> currentProperty = new PopDynamicProperty<>();
    public static Map<String, List<PopFlowRule>> getFlowRuleMap(){
        return flowRules;
    }
    static {
        currentProperty.addListener(LISTENER);
        // 先留个入口，方便之后接入集群模式，限流规则的同步
        // SCHEDULER.scheduleAtFixedRate()
    }
    private static final class PopFlowPropertyListener implements PopPropertyListener<List<PopFlowRule>> {

        @Override
        public void configUpdate(List<PopFlowRule> value) {
            // 动态更新
        }

        @Override
        public void configLoad(List<PopFlowRule> value) {
            Map<String, List<PopFlowRule>> ruleMap = PopFlowRuleUtil.buildPopFlowRuleMap(value);
            if (ruleMap != null){
                flowRules.clear();
                flowRules.putAll(ruleMap);
            }
        }
    }
}
