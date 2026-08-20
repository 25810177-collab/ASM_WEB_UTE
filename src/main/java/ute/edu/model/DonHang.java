package ute.edu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*; // Import các annotation của Lombok

/**
 * The persistent class for the DonHang database table.
 * 
 */
@Table(name = "don_hang")
@Entity
@NamedQuery(name = "DonHang.findAll", query = "SELECT d FROM DonHang d")
@Data // Annotation Lombok để tự động sinh các phương thức getter, setter, toString, equals, và hashCode
@NoArgsConstructor // Annotation Lombok để tự động sinh constructor không tham số
@AllArgsConstructor // Annotation Lombok để tự động sinh constructor có tham số đầy đủ
@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
public class DonHang implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ma_don_hang")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maDonHang;

    @Column(name = "tong_tien")
    private double tongTien;

    @Column(name = "dien_thoai")
    private String dienThoai;
    
    @Column(name = "dia_chi")
    private String diaChi;

    @Temporal(TemporalType.DATE)
    @Column(name = "ngay_tao")
    private Date ngayTao; // Thêm thuộc tính ngày tạo

    // Quan hệ nhiều-một với NguoiDung
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "ma_khach_hang", referencedColumnName = "ma_khach_hang") // Chỉnh sửa theo ma_khach_hang
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"}) // Bỏ qua các thuộc tính gây vòng lặp khi serialize/deserialize
    private KhachHang khachHang;

    @PrePersist
    protected void onCreate() {
        ngayTao = new Date(); // Gán ngày hiện tại trước khi lưu
    }
    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<ChiTietDonHang> chiTietDonHangs;
}
