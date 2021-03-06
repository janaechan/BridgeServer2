package org.sagebionetworks.bridge.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import org.sagebionetworks.bridge.models.files.FileMetadata;

public class FileMetadataValidator implements Validator {
    public static final FileMetadataValidator INSTANCE = new FileMetadataValidator();
    
    @Override
    public boolean supports(Class<?> clazz) {
        return FileMetadata.class.isAssignableFrom(clazz);
    }
    
    @Override
    public void validate(Object object, Errors errors) {
        FileMetadata file = (FileMetadata)object;
        
        if (StringUtils.isBlank(file.getName())) {
            errors.rejectValue("name", "is required");
        }
        if (StringUtils.isNotBlank(file.getMimeType())) {
            try {
                MimeTypeUtils.parseMimeType(file.getMimeType());
            } catch(InvalidMimeTypeException e) {
                errors.rejectValue("mimeType", "is not recognizable as a valid mime type");    
            }
        }
    }

}
