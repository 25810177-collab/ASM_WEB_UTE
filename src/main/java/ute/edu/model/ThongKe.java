package ute.edu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thong_ke")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKe implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_thong_ke")
    private int maThongKe;

    @Column(name = "diem_danh_gia_trung_binh")
    private BigDecimal diemDanhGiaTrungBinh;

    @Column(name = "doanh_thu")
    private BigDecimal doanhThu;

    @Column(name = "ngay_thong_ke")
    @Temporal(TemporalType.DATE)
    private Date ngayThongKe = new Date(); // Đặt giá trị mặc định là ngày hiện tại

    @Column(name = "tong_don_hang")
    private int tongDonHang;

    @Column(name = "tong_khach_hang")
    private int tongKhachHang;

    @Column(name = "tong_san_pham")
    private int tongSanPham;
}
