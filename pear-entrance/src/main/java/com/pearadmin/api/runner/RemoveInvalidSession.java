package com.pearadmin.api.runner;

import com.pearadmin.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.impl.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiky
 * @Date: 2020/12/17 15:34
 * @Description:
 */

@Slf4j
@Component
public class RemoveInvalidSession implements CommandLineRunner {

    @Autowired
    @Qualifier("manageSessionThreadPool")
    private ScheduledThreadPoolExecutor manageSessionThreadPool;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Value("${server.servlet.session.timeout}")
    private Duration duration;

    @Override
    public void run(String... args) throws Exception {
        manageSessionThreadPool.scheduleWithFixedDelay(() -> {
            List<Object> principals = sessionRegistry.getAllPrincipals();
            for (Object principal : principals) {
                SysUser userDetails = (SysUser) principal;
                // 获取用户最近一次的登录时间
                LocalDateTime lastTime = userDetails.getLastTime();
                // 获取session的目标过期时间
                LocalDateTime expiredTime = lastTime.plusSeconds(duration.getSeconds());
                // 若session过期
                if (Duration.between(LocalDateTime.now(), expiredTime).toMinutes() <= 0) {
                    List<SessionInformation> sessionInformationList = sessionRegistry.getAllSessions(userDetails, false);
                    if (CollectionUtil.isNotEmpty(sessionInformationList)) {
                        for (SessionInformation sessionInformation : sessionInformationList) {
                            // 清除已经过期的session
                            sessionInformation.expireNow();
                            sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
                            log.info(String.format("HttpSessionId[%s]------>已从sessionRegistry中移除", sessionInformation.getSessionId()));
                        }
                    }
                } else {
                    log.info("目前sessionRegistry中，没有已经过期的httpSession");
                }
            }
        }, 60, 10, TimeUnit.SECONDS);
    }
}
