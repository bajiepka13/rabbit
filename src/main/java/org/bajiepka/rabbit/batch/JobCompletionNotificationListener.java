package org.bajiepka.rabbit.batch;

import org.bajiepka.rabbit.mappers.ProductMapper;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Autowired
    private ProductMapper mapper;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            mapper.selectAll()
                    .stream()
                    .forEach(p -> System.out.println(p.getName() + " " + p.getDescription()));
        }
    }
}
