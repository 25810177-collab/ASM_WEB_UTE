package ute.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ute.edu.model.TaiKhoan;
import ute.edu.repository.TaiKhoanRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaiKhoanService {

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // Lấy tất cả tài khoản
    public List<TaiKhoan> getAllTaiKhoan() {
        return taiKhoanRepository.findAll();
    }

    // Lưu tài khoản
    public TaiKhoan saveTaiKhoan(TaiKhoan taiKhoan) {
        return taiKhoanRepository.save(taiKhoan);
    }

    // Tìm tài khoản theo ID
    public Optional<TaiKhoan> findById(int id) {
        return taiKhoanRepository.findById(id);
    }

    // Tìm tài khoản theo email
    public TaiKhoan findByEmail(String email) {
        return taiKhoanRepository.findByEmail(email);
    }
   

    // Xóa tài khoản theo ID
    public void deleteTaiKhoan(int id) {
        taiKhoanRepository.deleteById(id);
    }

    // Có thể thêm các phương thức khác nếu cần
}
