package LeiYang.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Table(name = "image")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Image {
    @Id
    @Column(name = "image_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long image_id;

    @Column(name = "product_id", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long productId;

    @Column(name = "file_name", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long fileName;

    @CreatedDate
    @Column(name = "date_added")
    private Date dateAdded;

    @LastModifiedDate
    @Column(name = "date_last_updated")
    private Date dateUpdated;

    public Long getId() {
        return image_id;
    }

    public void setId(Long id) {
        this.image_id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getFileName() {
        return fileName;
    }

    public void setFileName(Long fileName) {
        this.fileName = fileName;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
