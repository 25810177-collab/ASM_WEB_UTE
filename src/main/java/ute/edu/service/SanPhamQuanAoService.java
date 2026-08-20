package ute.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ute.edu.model.SanPhamQuanAo;
import ute.edu.repository.SanPhamQuanAoRepository;

import java.util.List;

@Service
public class SanPhamQuanAoService {

    @Autowired
    private SanPhamQuanAoRepository sanPhamQuanAoRepository;

    public List<SanPhamQuanAo> getAllSanPham() {
        return sanPhamQuanAoRepository.findAll();
    }

    public SanPhamQuanAo saveSanPham(SanPhamQuanAo sanPham) {
        return sanPhamQuanAoRepository.save(sanPham);
    }

    public SanPhamQuanAo findById(int id) {
        return sanPhamQuanAoRepository.findById(id).orElse(null);
    }

    public void deleteSanPham(int id) {
        sanPhamQuanAoRepository.deleteById(id);
    }

    // Phương thức cập nhật sản phẩm
    public SanPhamQuanAo updateSanPham(SanPhamQuanAo sanPham) {
        return sanPhamQuanAoRepository.save(sanPham); // Gọi phương thức save để cập nhật
    }
}
