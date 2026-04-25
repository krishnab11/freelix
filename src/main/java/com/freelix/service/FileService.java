package com.freelix.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.freelix.entity.FileRecord;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.enums.FileType;
import com.freelix.repository.FileRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FileService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private FileRecordRepository fileRecordRepository;

    public FileRecord uploadFile(MultipartFile file, FileType fileType, User uploader, Project project) throws IOException {
        String folder = "freelix/" + fileType.name().toLowerCase();
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "auto"
                )
        );
        String url = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        FileRecord record = new FileRecord();
        record.setFileName(file.getOriginalFilename());
        record.setCloudinaryUrl(url);
        record.setPublicId(publicId);
        record.setFileType(fileType);
        record.setUploader(uploader);
        record.setProject(project);
        return fileRecordRepository.save(record);
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public List<FileRecord> getFilesByUploader(User uploader) {
        return fileRecordRepository.findByUploader(uploader);
    }

    public List<FileRecord> getFilesByProject(Project project) {
        return fileRecordRepository.findByProject(project);
    }
}
