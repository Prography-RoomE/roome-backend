package com.sevenstars.roome.domain.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import com.sevenstars.roome.global.common.exception.CustomServerErrorException;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.sevenstars.roome.global.common.response.Result.*;

@RequiredArgsConstructor
@Service
public class StorageService {

    private final AmazonS3 amazonS3;
    private final Tika tika = new Tika();
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void validateImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (!StringUtils.hasText(originalFilename) || !isImage(file)) {
            throw new CustomClientErrorException(INVALID_IMAGE_FILE);
        }

        String extension = getExtension(originalFilename);
        if (!isValidImageExtension(extension)) {
            throw new CustomClientErrorException(INVALID_IMAGE_FILE_EXTENSION);
        }
    }

    public String saveImage(String path, MultipartFile file) {
        validateImage(file);

        String fileName = generateFileName(file);
        ObjectMetadata metadata = getObjectMetadata(file);

        try {
            uploadFileToS3(path, file, fileName, metadata);
        } catch (IOException exception) {
            throw new CustomServerErrorException(FILE_UPLOAD_FAILED);
        }

        return getFileUrl(path, fileName);
    }

    public void deleteImage(String path, String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return;
        }

        String fileName = extractFileName(imageUrl);
        if (fileName != null) {
            deleteFileFromS3(path, fileName);
        }
    }

    private boolean isImage(MultipartFile file) {
        try {
            String mimeType = tika.detect(file.getInputStream());
            return mimeType.startsWith("image/");
        } catch (IOException exception) {
            return false;
        }
    }

    private String getExtension(String filename) {
        int index = filename.lastIndexOf(".");
        if (index == -1 || !StringUtils.hasText(filename.substring(index + 1))) {
            throw new CustomClientErrorException(FILE_EXTENSION_DOES_NOT_EXIST);
        }
        return filename.substring(index + 1).toLowerCase();
    }

    private boolean isValidImageExtension(String extension) {
        return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png");
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        return UUID.randomUUID() + "." + extension;
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }

    private void uploadFileToS3(String path, MultipartFile file, String fileName, ObjectMetadata metadata) throws IOException {
        String bucketUrl = bucket + path;
        PutObjectRequest request = new PutObjectRequest(bucketUrl, fileName, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(request);
    }

    private String getFileUrl(String path, String fileName) {
        String bucketUrl = bucket + path;
        return amazonS3.getUrl(bucketUrl, fileName).toString();
    }

    private String extractFileName(String imageUrl) {
        int index = imageUrl.lastIndexOf("/");
        return index == -1 ? null : imageUrl.substring(index + 1);
    }

    private void deleteFileFromS3(String path, String fileName) {
        String bucketUrl = bucket + path;
        amazonS3.deleteObject(bucketUrl, fileName);
    }
}
