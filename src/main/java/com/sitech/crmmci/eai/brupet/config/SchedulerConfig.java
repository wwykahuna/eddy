package com.sitech.crmpd.quant.config;

import com.sitech.crmpd.quant.async.ExceptionHandlingAsyncTaskExecutor;
import com.sitech.crmpd.quant.config.properties.AsyncTaskProperty;
import com.sitech.crmpd.quant.config.properties.SchedulerProperty;
import com.sitech.crmpd.quant.factory.QuantThreadFactory;
import com.sitech.crmpd.quant.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.lang.reflect.Method;
import java.util.concurrent.*;


@EnableAsync
@EnableScheduling
@Configuration
@EnableConfigurationProperties({SchedulerProperty.class, AsyncTaskProperty.class})
public class SchedulerConfig implements SchedulingConfigurer, AsyncConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfig.class);

    private final SchedulerProperty schedulerProperty;

    private final AsyncTaskProperty asyncTaskProperty;

    private static final int AWAIT_TIME = 60;

    @Autowired
    public SchedulerConfig(SchedulerProperty schedulerProperty, AsyncTaskProperty asyncTaskProperty) {
        this.schedulerProperty = schedulerProperty;
        this.asyncTaskProperty = asyncTaskProperty;
    }

    @Bean(name = "taskScheduler", destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {
        LogUtil.info("b=[{}]", 20);
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(schedulerProperty.getPoolSize());
        scheduler.setThreadFactory(new QuantThreadFactory("scheduler-"));
        scheduler.setAwaitTerminationSeconds(300);
        scheduler.setWaitForTasksToCompleteOnShutdown(false);
        scheduler.setRejectedExecutionHandler(new PostponedRunsPolicy("scheduler-temp-"));
        return scheduler;
    }

    @Bean("executor")
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncTaskProperty.getCorePoolSize());
        executor.setMaxPoolSize(asyncTaskProperty.getMaxPoolSize());
        executor.setQueueCapacity(asyncTaskProperty.getQueueCapacity());
        executor.setKeepAliveSeconds(asyncTaskProperty.getKeepAliveSeconds());
        executor.setThreadNamePrefix(asyncTaskProperty.getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setRejectedExecutionHandler(new NewThreadRunsPolicy(asyncTaskProperty.getThreadNamePrefix() + "-temp-"));
        executor.initialize();
        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }


    @Override
    public Executor getAsyncExecutor() {
        return new ExceptionHandlingAsyncTaskExecutor(taskExecutor());
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                LOGGER.error(String.format("异步任务执行异常,异常方法:%s,异常原因:%s", method.getName(), ex.getMessage()));
                ex.printStackTrace();
            }
        };
//        return new SimpleAsyncUncaughtExceptionHandler();
    }

    private static final class NewThreadRunsPolicy implements RejectedExecutionHandler {

        private String threadNamePrefix;

        public NewThreadRunsPolicy(String threadNamePrefix) {
            super();
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                try {
                    final Thread thread = new Thread(r, threadNamePrefix);
                    thread.start();
                } catch (Throwable e) {
                    throw new RejectedExecutionException("Failed to start a new thread", e);
                }
            }
        }
    }

    private static final class PostponedRunsPolicy implements RejectedExecutionHandler {

        private String threadNamePrefix;

        public PostponedRunsPolicy(String threadNamePrefix) {
            super();
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                try {
                    executor.getQueue().offer(r, AWAIT_TIME, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RejectedExecutionException("Interrupted waiting for worker");
                }
                throw new RejectedExecutionException("Timed out while attempting to enqueue task");
            }
        }
    }
}
