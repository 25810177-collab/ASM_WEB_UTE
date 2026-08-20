package ute.edu.model;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "danh_gia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhGia implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_danh_gia")
    private int maDanhGia;

    private String binhLuan;

    @Column(name = "diem_danh_gia")
    private int diemDanhGia;

    // Mối quan hệ với KhachHang
    @ManyToOne
    @JoinColumn(name = "ma_khach_hang", referencedColumnName = "ma_khach_hang", insertable = false, updatable = false)
    private KhachHang khachHang;

    // Mối quan hệ với SanPham
    @ManyToOne
    @JoinColumn(name = "ma_san_pham", referencedColumnName = "ma_san_pham", insertable = false, updatable = false)
    private SanPhamQuanAo sanPhamQuanAo;

    @Column(name = "ngay_danh_gia")
    @Temporal(TemporalType.DATE)
    private Date ngayDanhGia = new Date(); // Đặt giá trị mặc định là ngày hiện tại
}
