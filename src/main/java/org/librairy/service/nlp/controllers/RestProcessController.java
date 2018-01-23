package org.librairy.service.nlp.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.NlpService;
import org.librairy.service.nlp.facade.rest.model.ProcessRequest;
import org.librairy.service.nlp.facade.rest.model.ProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@RestController
@RequestMapping("/process")
@Api(tags = "/process", description = "sequence of tokens from a text")
public class RestProcessController {

    private static final Logger LOG = LoggerFactory.getLogger(RestProcessController.class);

    @Autowired
    NlpService service;

    @PostConstruct
    public void setup(){

    }

    @PreDestroy
    public void destroy(){

    }

    @ApiOperation(value = "filter words by PoS and return them in a specific form", nickname = "postProcess", response=ProcessResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = ProcessResult.class),
    })
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ProcessResult analyze(@RequestBody ProcessRequest processRequest)  {
        try {
            return new ProcessResult(service.process(processRequest.getText(), processRequest.getFilter(), processRequest.getForm()));
        } catch (AvroRemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
