package uz.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Attachment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fileOriginalName;
    private Long size;
    private String contentType;
    private String directoryName;

    @Override
    public String toString() {
        return "id=" + id + "\n" +
                "fileOriginalName='" + fileOriginalName + "\'\n" +
                "size=" + size + "\n" +
                "contentType='" + contentType + "\'\n" +
                "directoryName='" + directoryName + "\'\n\n";
    }
}
