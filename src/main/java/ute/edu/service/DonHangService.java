package ute.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ute.edu.model.DonHang;
import ute.edu.model.ChiTietDonHang;
import ute.edu.repository.ChiTietDonHangRepository;
import ute.edu.repository.DonHangRepository;

import java.util.List;

@Service
public class DonHangService {

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    public boolean insertCart(DonHang donHang, List<ChiTietDonHang> listCTDH) {
        try {
            // Lưu đơn hàng
            donHang = donHangRepository.save(donHang);
            System.out.println("Saved DonHang with ID: " + donHang.getMaDonHang());
            
            // Lưu chi tiết đơn hàng
            for (ChiTietDonHang chiTiet : listCTDH) {
            	
                chiTiet.setDonHang(donHang); // Liên kết chi tiết với đơn hàng
                chiTietDonHangRepository.save(chiTiet); // Lưu chi tiết đơn hàng
                System.out.println("Saved ChiTietDonHang with ID: " + chiTiet.getMaChiTietDonHang());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi chi tiết
            return false;
        }
    }

    // Tìm đơn hàng theo ID
    public DonHang findById(int id) {
        return donHangRepository.findById(id).orElse(null);
    }

    // Lấy tất cả đơn hàng
    public List<DonHang> findAll() {
        return donHangRepository.findAll();
    }

    // Cập nhật đơn hàng
    public int update(DonHang donHang) {
        return donHangRepository.save(donHang) != null ? 1 : -1;
    }

    // Xóa đơn hàng theo ID
    public int delete(int id) {
        try {
            donHangRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
