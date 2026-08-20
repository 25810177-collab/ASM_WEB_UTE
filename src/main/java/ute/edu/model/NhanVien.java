package ute.edu.model;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*; // Import các annotation của Lombok

/**
 * Lớp NhanVien ánh xạ đến bảng nhan_vien trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "nhan_vien")
@Data // Tự động sinh getter, setter, toString, equals, hashCode
@NoArgsConstructor // Tự động sinh constructor không tham số
@AllArgsConstructor // Tự động sinh constructor có tham số đầy đủ
public class NhanVien implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nhan_vien")
    private int maNhanVien;

    @Column(name = "ten_nhan_vien")
    private String tenNhanVien;

    @Column(name = "dia_chi_nhan_vien")
    private String diaChiNhanVien;

    @Column(name = "so_dien_thoai_nhan_vien")
    private String soDienThoaiNhanVien;

    @ManyToOne
    @JoinColumn(name = "ma_tai_khoan", referencedColumnName = "ma_tai_khoan")
    private TaiKhoan taiKhoan; // Quan hệ với lớp TaiKhoan
}
