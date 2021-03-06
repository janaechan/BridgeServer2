package org.sagebionetworks.bridge.validators;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpStatus;
import org.sagebionetworks.bridge.exceptions.BridgeServiceException;
import org.sagebionetworks.bridge.json.BridgeObjectMapper;
import org.sagebionetworks.bridge.models.upload.UploadRequest;
import org.springframework.validation.Validator;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UploadValidatorTest {
    
    // The other tests in this class don't go through the controller; we want to do that
    // because we want to verify that we get the right type back. This could easily
    // be broken since there's no external test and we could switch the object internally.
    @Test
    public void uploadRequestHasCorrectType() {
        BridgeObjectMapper mapper = new BridgeObjectMapper();
        
        JsonNode node = mapper.valueToTree(new UploadRequest());
        assertEquals("UploadRequest", node.get("type").asText(), "Type is UploadRequest");
    }

    @Test
    public void testValidateRequest() {
        Validator validator = new UploadValidator();
        
        // A valid case
        final String message = "testValidateRequest";
        {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("name", this.getClass().getSimpleName());
            node.put("contentType", "text/plain");
            node.put("contentLength", message.getBytes().length);
            node.put("contentMd5", Base64.encodeBase64String(DigestUtils.md5(message)));
            UploadRequest uploadRequest = UploadRequest.fromJson(node);
            
            Validate.entityThrowingException(validator, uploadRequest);
        }

        try {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("contentType", "text/plain");
            node.put("contentLength", message.getBytes().length);
            node.put("contentMd5", Base64.encodeBase64String(DigestUtils.md5(message)));
            UploadRequest uploadRequest = UploadRequest.fromJson(node);
            
            Validate.entityThrowingException(validator, uploadRequest);
        } catch (BridgeServiceException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Name missing");
        }

        try {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("name", this.getClass().getSimpleName());
            node.put("contentLength", message.getBytes().length);
            node.put("contentMd5", Base64.encodeBase64String(DigestUtils.md5(message)));
            UploadRequest uploadRequest = UploadRequest.fromJson(node);
            
            Validate.entityThrowingException(validator, uploadRequest);
        } catch (BridgeServiceException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Content type missing");
        }

        try {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("name", this.getClass().getSimpleName());
            node.put("contentType", "text/plain");
            node.put("contentMd5", Base64.encodeBase64String(DigestUtils.md5(message)));
            UploadRequest uploadRequest = UploadRequest.fromJson(node);
            
            Validate.entityThrowingException(validator, uploadRequest);
        } catch (BridgeServiceException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Content length missing");
        }

        try {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("name", this.getClass().getSimpleName());
            node.put("contentType", "text/plain");
            node.put("contentLength", 51000000L);
            node.put("contentMd5", Base64.encodeBase64String(DigestUtils.md5(message)));
            UploadRequest uploadRequest = UploadRequest.fromJson(node);
            
            Validate.entityThrowingException(validator, uploadRequest);
        } catch (BridgeServiceException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST, "Content length > 10 MB");
        }

        try {
            ObjectNode node = JsonNodeFactory.instance.objectNode();
            node.put("name", this.getClass().getSimpleName());
            node.put("contentType", "text/plain");
            node.put("contentLength", message.getBytes().length);
            node.put("contentMd5", DigestUtils.md5(message));
            UploadRequest uploadRequest = UploadRequest.fromJson(node);
            
            Validate.entityThrowingException(validator, uploadRequest);
        } catch (BridgeServiceException e) {
            assertEquals(e.getStatusCode(), HttpStatus.SC_BAD_REQUEST, "MD5 not base64 encoded");
        }
    }
}
