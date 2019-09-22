package org.bajiepka.rabbit.service;

import org.springframework.stereotype.Service;

@Service
public class MyBatchService implements BatchService {

    @Override
    public void executeJobManually(String type) {
        System.out.printf("Job execution started.");
    }
}
