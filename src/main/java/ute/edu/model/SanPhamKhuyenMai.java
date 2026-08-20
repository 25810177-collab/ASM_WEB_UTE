package ute.edu.model;
import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "san_pham_khuyen_mai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamKhuyenMai implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_san_pham_khuyen_mai")
    private int maSanPhamKhuyenMai;

    @Column(name = "ma_san_pham")
    private int maSanPham;

    @Column(name = "ma_khuyen_mai")
    private int maKhuyenMai;

    // Mối quan hệ với SanPham
    @ManyToOne
    @JoinColumn(name = "ma_san_pham", referencedColumnName = "ma_san_pham", insertable = false, updatable = false)
    private SanPhamQuanAo sanPham;

    // Mối quan hệ với KhuyenMai
    @ManyToOne
    @JoinColumn(name = "ma_khuyen_mai", referencedColumnName = "ma_khuyen_mai", insertable = false, updatable = false)
    private KhuyenMai khuyenMai;
}
