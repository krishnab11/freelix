package com.freelix.repository;

import com.freelix.entity.FileRecord;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import com.freelix.enums.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    List<FileRecord> findByUploader(User uploader);
    List<FileRecord> findByProject(Project project);
    List<FileRecord> findByUploaderAndFileType(User uploader, FileType fileType);
}
