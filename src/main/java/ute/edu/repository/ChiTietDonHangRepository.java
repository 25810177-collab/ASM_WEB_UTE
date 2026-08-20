package ute.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ute.edu.model.ChiTietDonHang;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer> {
    // Có thể thêm các phương thức truy vấn tùy chỉnh ở đây nếu cần
	List<ChiTietDonHang> findByDonHangMaDonHang(int donHangId);
}
