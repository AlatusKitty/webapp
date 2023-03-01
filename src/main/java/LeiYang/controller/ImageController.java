package LeiYang.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import com.amazonaws.regions.Regions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

@RestController
@RequestMapping("/v1")
public class ImageController {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private AmazonS3 s3client;

    //调用withCredentials()方法将一个InstanceProfileCredentialsProvider对象传递给AmazonS3ClientBuilder对象。
    //InstanceProfileCredentialsProvider从Amazon EC2实例元数据服务中获取IAM角色的凭证
    public ImageController() {
        this.s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    @PostMapping(value = "/product/{product_id}/image")
    public ResponseEntity<String> uploadProductImage(@PathVariable("product_id") String productId,
                                                     @RequestParam("file") MultipartFile file) throws IOException {

        // Check if file is empty
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        // Get file extension
        String extension = getFileExtension(file);

        // Generate unique file name
        String fileName = UUID.randomUUID().toString() + "." + extension;

        // Create file in temporary directory
        File tempFile = Files.createTempFile(fileName, extension).toFile();
        file.transferTo(tempFile);

        // Upload file to S3 bucket
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, productId + "/" + fileName, tempFile)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        s3client.putObject(putObjectRequest);

        // Delete temporary file
        tempFile.delete();

        // Generate URL for uploaded file
        String fileUrl = s3client.getUrl(bucketName, productId + "/" + fileName).toString();

        return ResponseEntity.status(HttpStatus.OK).body(fileUrl);
    }

    private String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
    }

}
