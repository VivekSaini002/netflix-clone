package com.netflixClone.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.netflixClone.service.FileUploadService;
import com.netflixClone.util.FileHandlerUtil;

import jakarta.annotation.PostConstruct;

@Service
public class FileUploadServiceImpl implements FileUploadService {
	
	private Path videoStorageLocation;
	private Path imageStorageLocation;
	
	@Value("${files.upload.video-dir:uploads/videos}")
	private String videoDir;
	
	@Value("${files.upload.image-dir:uploads/images}")
	private String imageDir;
	
	@PostConstruct
	public void init() {
		this.videoStorageLocation = Paths.get(videoDir).toAbsolutePath().normalize();
		this.imageStorageLocation = Paths.get(imageDir).toAbsolutePath().normalize();
		
		try {
			Files.createDirectories(this.videoStorageLocation);
			Files.createDirectories(this.imageStorageLocation);
		} catch (Exception e) {
			throw new RuntimeException("Could not create the directories where the upload files will be stored!", e);
		}
	}

	@Override
	public String storeVideoFile(MultipartFile file) {
		return storeFile(file, videoStorageLocation);
	}

	private String storeFile(MultipartFile file, Path storageLocation) {
		String fileExtension = FileHandlerUtil.extractFileExtension(file.getOriginalFilename());
		String uuid = UUID.randomUUID().toString();
		String fileName = uuid + fileExtension;
		
		try {
			if(file.isEmpty()) {
				throw new RuntimeException("Failed to store empty file " + fileName);
			}
			
			Path targetLocation = storageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return uuid;
			
		} catch(IOException ex) {
			throw new RuntimeException("Failed to stored file " + fileName, ex);
		}
	}

	@Override
	public String storeImageFile(MultipartFile file) {
		return storeFile(file, imageStorageLocation);
	}

	@Override
	public ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader) {
		try {
			Path filePath = FileHandlerUtil.findFileByUuid(videoStorageLocation, uuid);
			Resource resource = FileHandlerUtil.createFullResource(filePath);
			String fileName = resource.getFilename();
			String contentType = FileHandlerUtil.detectVideoContentType(fileName);
			long fileLength = resource.contentLength();
			
			if(isFullContentRequested(rangeHeader)) {
				return buildFullVideoResponse(resource, contentType,fileName, fileLength);
			}
			
			return buildPartialVideoResponse(filePath, rangeHeader, contentType, fileName, fileLength);
			
		} catch(Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	private ResponseEntity<Resource> buildPartialVideoResponse(Path filePath, String rangeHeader, String contentType,
			String fileName, long fileLength) throws IOException {
		long[] ranges = FileHandlerUtil.parseRangeHeader(rangeHeader, fileLength);
		long rangeStart = ranges[0];
		long rangeEnd = ranges[1];
		
		if(!isValidRange(rangeStart, rangeEnd, fileLength)) {
			return buildRangeNotSatisfiableResponse(fileLength);
		}
		
		long contentLength = rangeEnd - rangeStart + 1;
		Resource rangeResource = FileHandlerUtil.createRangeResource(filePath, rangeStart, contentLength);
		
		return ResponseEntity.status(206)
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
				.header(HttpHeaders.ACCEPT_RANGES, "bytes")
				.header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength)
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
				.body(rangeResource);
	}
	

	private ResponseEntity<Resource> buildRangeNotSatisfiableResponse(long fileLength) {
		return ResponseEntity.status(416)
				.header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
				.build();
	}

	private boolean isValidRange(long rangeStart, long rangeEnd, long fileLength) {
		return rangeStart <= rangeEnd && rangeStart >= 0 && rangeEnd < fileLength;
	}

	private ResponseEntity<Resource> buildFullVideoResponse(Resource resource, String contentType, String fileName,
			long fileLength) {
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
				.header(HttpHeaders.ACCEPT_RANGES, "bytes")
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
				.body(resource);
	}

	private boolean isFullContentRequested(String rangeHeader) {
		return rangeHeader == null || rangeHeader.isEmpty();
	}

	@Override
	public ResponseEntity<Resource> serveImage(String uuid) {
		try {
			
			Path filePath = FileHandlerUtil.findFileByUuid(imageStorageLocation, uuid);
			Resource resource = FileHandlerUtil.createFullResource(filePath);
			String fileName = resource.getFilename();
			String contentType = FileHandlerUtil.detectImageContentType(fileName);
			
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
					.body(resource);
			
		} catch(Exception e) {
			return ResponseEntity.notFound().build();
		}
		
	}

}
