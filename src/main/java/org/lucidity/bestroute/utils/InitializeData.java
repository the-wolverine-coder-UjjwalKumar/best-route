package org.lucidity.bestroute.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.lucidity.bestroute.entity.request.DeliverOrderRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class InitializeData {

    private static DeliverOrderRequest deliverOrderRequest;

    @PostConstruct
    public void init() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            String testCaseValue = new String(FileCopyUtils
                    .copyToByteArray(new ClassPathResource("test_case.json").getInputStream()),
                    StandardCharsets.UTF_8);

            deliverOrderRequest = objectMapper.readValue(testCaseValue, DeliverOrderRequest.class);


        } catch (Exception e) {
            log.error("Unable to initialize data :: ", e);
        }
    }

    public DeliverOrderRequest getLocalData() {
        if (deliverOrderRequest == null) {
            init();
        }
        return deliverOrderRequest;
    }

}
