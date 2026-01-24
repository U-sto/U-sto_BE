package com.usto.api.common.utils;

import com.usto.api.g2b.application.G2bSyncServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class G2bDailySyncJob {

    private final G2bSyncServiceImpl g2bSyncServiceImpl; // 또는 Daily 전용 서비스

    // 매일 00:01 (Asia/Seoul)
    @Scheduled(cron = "0 01 0 * * *", zone = "Asia/Seoul")
    public void runDaily() {
        g2bSyncServiceImpl.syncDaily();
    }
}
