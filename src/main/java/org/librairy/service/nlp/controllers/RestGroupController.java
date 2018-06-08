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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groups")
@Api(tags = "/groups", description = "handle bag of tokens from a text")
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

    @ApiOperation(value = "create annotations and group by frequency from a given text", nickname = "postGroup", response= GroupsResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = GroupsResult.class),
    })
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<GroupsResult> group(@RequestBody GroupsRequest request)  {
        try {
            GroupsResult groups = new GroupsResult(service.groups(request.getText(), request.getFilter(), request.getMultigrams(), request.getReferences()).stream().map(t -> new Group(t)).collect(Collectors.toList()));
            return new ResponseEntity( groups, HttpStatus.OK);
        } catch (AvroRemoteException e) {
            return new ResponseEntity("internal service seems down", HttpStatus.FAILED_DEPENDENCY);
        } catch (Exception e){
            return new ResponseEntity("internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
