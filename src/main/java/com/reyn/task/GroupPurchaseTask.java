package com.reyn.task;

import com.reyn.service.GroupPurchaseRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GroupPurchaseTask {

    @Autowired
    private GroupPurchaseRequestService requestService;

    /**
     * 每小时检查一次过期的求购请求
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkExpiredRequests() {
        try {
            log.info("开始检查过期的求购请求...");
            requestService.processExpiredRequests();
            log.info("过期求购请求处理完成");
        } catch (Exception e) {
            log.error("处理过期求购请求时发生错误: ", e);
        }
    }
}
