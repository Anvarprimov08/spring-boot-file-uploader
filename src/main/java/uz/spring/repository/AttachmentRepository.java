package uz.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.spring.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
}
