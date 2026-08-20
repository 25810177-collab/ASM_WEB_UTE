package ute.edu.model;

import java.io.Serializable;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "chi_tiet_don_hang")
@Entity
@NamedQuery(name = "ChiTietDonHang.findAll", query = "SELECT c FROM ChiTietDonHang c")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "applications", "hibernateLazyInitializer" })
public class ChiTietDonHang implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ma_chi_tiet_don_hang")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int maChiTietDonHang;

	@Column(name = "so_luong")
	private int soLuong;

	@Column(name = "gia")
	private double gia;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "ma_don_hang", referencedColumnName = "ma_don_hang")
	private DonHang donHang;

	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "ma_san_pham", referencedColumnName = "ma_san_pham")
	private SanPhamQuanAo sanPhamQuanAo;

	@Column(name = "size")
	private String size;
	@Column(name = "mau_sac", columnDefinition = "NVARCHAR(50)")
	private String mauSac;

}
