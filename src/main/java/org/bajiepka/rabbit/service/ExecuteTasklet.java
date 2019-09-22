package org.bajiepka.rabbit.service;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExecuteTasklet implements Tasklet {

    @Autowired
    private BatchService batchService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String type = chunkContext.getStepContext().getJobParameters().get("type").toString();
        batchService.executeJobManually(type);
        return RepeatStatus.FINISHED;
    }

}
