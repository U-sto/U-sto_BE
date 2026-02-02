package com.usto.api.common.utils;

import com.usto.api.g2b.application.G2bSyncApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class G2bDailySyncJob {

    private final G2bSyncApplication g2bSyncApplication; // 또는 Daily 전용 서비스

    // 매일 00:10 (Asia/Seoul)
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    public void runDaily() {
        g2bSyncApplication.syncDaily();
    }

    // 3개월에 한 번 내용연수를 업데이트 하면 될거같다.
    @Scheduled(cron = "0 0 2 1 */3 *", zone = "Asia/Seoul")  // 3개월
    public void syncContentYear() {
        g2bSyncApplication.sync3Months();
    }
}
