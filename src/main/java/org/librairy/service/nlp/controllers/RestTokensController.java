package org.librairy.service.nlp.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.avro.AvroRemoteException;
import org.librairy.service.nlp.facade.model.NlpService;
import org.librairy.service.nlp.facade.rest.model.TokensRequest;
import org.librairy.service.nlp.facade.rest.model.TokensResult;
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

@RestController
@RequestMapping("/tokens")
@Api(tags = "/tokens", description = "handle tokens from a text")
public class RestTokensController {

    private static final Logger LOG = LoggerFactory.getLogger(RestTokensController.class);

    @Autowired
    NlpService service;

    @PostConstruct
    public void setup(){

    }

    @PreDestroy
    public void destroy(){

    }

    @ApiOperation(value = "create tokens from a given text", nickname = "postProcess", response=TokensResult.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = TokensResult.class),
    })
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<TokensResult> analyze(@RequestBody TokensRequest request)  {
        try {
            return new ResponseEntity(new TokensResult(service.tokens(request.getText(), request.getFilter(), request.getForm(), request.getMultigrams())),HttpStatus.OK);
        } catch (AvroRemoteException e) {
            return new ResponseEntity("internal service seems down", HttpStatus.FAILED_DEPENDENCY);
        } catch (Exception e){
            return new ResponseEntity("internal error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
