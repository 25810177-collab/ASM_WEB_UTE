package ute.edu.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*; // Import các annotation của Lombok

/**
 * The persistent class for the KhachHang database table.
 * 
 */
@Entity
@Table(name = "khach_hang")
@NamedQuery(name = "KhachHang.findAll", query = "SELECT k FROM KhachHang k")
@Data // Annotation Lombok để tự động sinh các phương thức getter, setter, toString, equals, và hashCode
@NoArgsConstructor // Annotation Lombok để tự động sinh constructor không tham số
@AllArgsConstructor // Annotation Lombok để tự động sinh constructor có tham số đầy đủ
@JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"})
public class KhachHang implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khach_hang") // Đảm bảo tên này khớp với tên cột trong cơ sở dữ liệu
    private int maKhachHang;

    @Column(name = "ten_khach_hang")
    private String tenKhachHang;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    // Quan hệ nhiều-một với TaiKhoan
    @ManyToOne
    @JoinColumn(name = "ma_tai_khoan", referencedColumnName = "ma_tai_khoan") // Ánh xạ đến bảng tai_khoan
    @JsonIgnoreProperties(value = {"applications", "hibernateLazyInitializer"}) // Bỏ qua các thuộc tính gây vòng lặp khi serialize/deserialize
    private TaiKhoan taiKhoan;

   
	// Quan hệ một-nhiều với DonHang
	@OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DonHang> donHangs;

	// Phương thức thêm DonHang vào danh sách
	public DonHang addDonHang(DonHang donHang) {
		getDonHangs().add(donHang);
		donHang.setKhachHang(this);

		return donHang;
	}

	// Phương thức xóa DonHang khỏi danh sách
	public DonHang removeDonHang(DonHang donHang) {
		getDonHangs().remove(donHang);
		donHang.setKhachHang(null);

		return donHang;
	}
}
