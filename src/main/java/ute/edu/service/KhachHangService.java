package ute.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ute.edu.model.KhachHang;
import ute.edu.model.TaiKhoan;
import ute.edu.repository.KhachHangRepository;

import java.util.List;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    public KhachHang saveKhachHang(KhachHang khachHang) {
        return khachHangRepository.save(khachHang);
    }

    public KhachHang findById(int maKhachHang) {
        return khachHangRepository.findById(maKhachHang).orElse(null);
    }

    public void deleteKhachHang(int maKhachHang) {
        khachHangRepository.deleteById(maKhachHang);
    }
    public KhachHang findByTaiKhoan(TaiKhoan taiKhoan) {
        return khachHangRepository.findByTaiKhoan(taiKhoan);
    }
    public KhachHang findBySoDienThoai(String sdt) {
        return khachHangRepository.findBySoDienThoai(sdt);
    }
    public KhachHang findByTaiKhoanId(int maTaiKhoan) {
        return khachHangRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan);
    }


    
}
