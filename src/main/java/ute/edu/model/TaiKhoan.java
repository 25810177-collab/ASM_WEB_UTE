package ute.edu.model;

import java.io.Serializable;
import jakarta.persistence.*;
import lombok.*; // Import các annotation của Lombok

/**
 * Lớp TaiKhoan ánh xạ đến bảng TaiKhoan trong cơ sở dữ liệu.
 */
@Entity
@Table(name = "tai_khoan")
@Data // Tự động sinh getter, setter, toString, equals, hashCode
@NoArgsConstructor // Tự động sinh constructor không tham số
@AllArgsConstructor // Tự động sinh constructor có tham số đầy đủ
public class TaiKhoan implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tai_khoan")
    private int maTaiKhoan;

    @Column(name = "email")
    private String email;

    @Column(name = "mat_khau")
    private String matKhau;
    
//    @Column(name = "vai_tro", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'khach_hang'")
//    private String vaiTro = "khach_hang"; // Thiết lập vai trò mặc định
    @Column(name = "vai_tro", nullable = false)
    private String vaiTro; // Không gán giá trị mặc định trong mã


    // Quan hệ một-một với KhachHang
    @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL, orphanRemoval = true)
    private KhachHang khachHang;

   
    @OneToOne(mappedBy = "taiKhoan", cascade = CascadeType.ALL, orphanRemoval = true)
    private NhanVien nhanVien;

    // Phương thức thiết lập quan hệ với KhachHang
    
}
