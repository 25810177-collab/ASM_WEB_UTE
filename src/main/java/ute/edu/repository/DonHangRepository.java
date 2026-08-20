package ute.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ute.edu.model.DonHang;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

	@Query("SELECT dh.maDonHang, kh.tenKhachHang, dh.diaChi, dh.dienThoai, " +
		       "sp.tenSanPham, ctdh.mauSac, ctdh.size, ctdh.soLuong, ctdh.gia, " +
		       "(ctdh.soLuong * ctdh.gia) AS tongTienChiTiet, dh.ngayTao " +
		       "FROM DonHang dh " +
		       "JOIN dh.khachHang kh " +  // Chỉnh sửa tên thuộc tính
		       "JOIN dh.chiTietDonHangs ctdh " + // Chỉnh sửa tên thuộc tính
		       "JOIN ctdh.sanPhamQuanAo sp " +  // Chỉnh sửa tên thuộc tính
		       "ORDER BY dh.ngayTao DESC")
		List<Object[]> findAllOrderDetails();

}
