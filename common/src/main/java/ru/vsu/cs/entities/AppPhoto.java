package ru.vsu.cs.entities;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_photo")
@Entity
public class AppPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telegramFileId;

    @OneToOne
    private BinaryContent binaryContent;

    private Integer fileSize;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppPhoto appPhoto = (AppPhoto) o;
        return telegramFileId != null && Objects.equals(telegramFileId, appPhoto.telegramFileId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
