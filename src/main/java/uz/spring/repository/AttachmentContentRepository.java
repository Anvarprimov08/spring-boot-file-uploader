package uz.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.spring.model.AttachmentContent;

import java.util.Optional;

public interface AttachmentContentRepository extends JpaRepository<AttachmentContent, Integer> {
    Optional<AttachmentContent> findByAttachment_Id(int id);
}
