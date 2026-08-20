package ute.edu.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = { "hibernateLazyInitializer" })
public class DanhMucSanPhamQuanAo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maDanhMuc;

    @Column(nullable = false)
    private String tenDanhMuc;

    // Quan hệ một-nhiều với SanPhamQuanAo
//    @OneToMany(mappedBy = "danhMucSanPham", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private List<SanPhamQuanAo> sanPhamQuanAos;

    // Phương thức thêm sản phẩm vào danh mục
//    public SanPhamQuanAo addSanPhamQuanAo(SanPhamQuanAo sanPham) {
//        getSanPhamQuanAos().add(sanPham);
//        sanPham.setDanhMucSanPham(this);
//        return sanPham;
//    }
//
//    // Phương thức xóa sản phẩm khỏi danh mục
//    public SanPhamQuanAo removeSanPhamQuanAo(SanPhamQuanAo sanPham) {
//        getSanPhamQuanAos().remove(sanPham);
//        sanPham.setDanhMucSanPham(null);
//        return sanPham;
//    }
}
