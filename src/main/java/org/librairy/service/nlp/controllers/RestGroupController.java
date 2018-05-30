package org.librairy.service.nlp.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.NlpService;
import org.librairy.service.nlp.facade.rest.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group")
@Api(tags = "/group", description = "bag of tokens from a text")
public class RestGroupController {

    private static final Logger LOG = LoggerFactory.getLogger(RestGroupController.class);

    @Autowired
    NlpService service;

    @PostConstruct
    public void setup(){

    }

    @PreDestroy
    public void destroy(){

    }

    @ApiOperation(value = "filter words by PoS and return them in a specific form", nickname = "postGroup", response= GroupResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = GroupResult.class),
    })
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public GroupResult group(@RequestBody GroupRequest request)  {
        try {
            return new GroupResult(service.group(request.getText(), request.getFilter()).stream().map(t -> new Token(t)).collect(Collectors.toList()));
        } catch (AvroRemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
