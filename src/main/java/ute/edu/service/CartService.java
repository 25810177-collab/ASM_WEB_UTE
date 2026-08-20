package ute.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ute.edu.dto.CartDetailDto;
import ute.edu.dto.CartDto;
import ute.edu.model.DonHang;
import ute.edu.model.ChiTietDonHang;
import ute.edu.model.KhachHang;
import ute.edu.model.SanPhamQuanAo;
import ute.edu.repository.ChiTietDonHangRepository;
import ute.edu.repository.KhachHangRepository;
import ute.edu.repository.SanPhamQuanAoRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private SanPhamQuanAoRepository spRepository; // Đổi tên biến cho đúng với repository

    @Autowired
    private KhachHangRepository khachHangRepository;


    @Autowired
    private DonHangService donHangService;
    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    public boolean checkBeforeInsert(CartDto cart) {
        if (cart.getTongsl() == 0) {
            return false;
        }
        return insertHoaDon(cart);
    }
    @Transactional
    public boolean insertHoaDon(CartDto cartDto) {
        DonHang donHang = mappingDtoToOrderEntity(cartDto);
        List<ChiTietDonHang> listDHCT = mappingDtoToOrderDetailEntity(cartDto.getDetail(), donHang);                     
        return  donHangService.insertCart(donHang, listDHCT);
        
    }


    public CartDto updateCart(CartDto cart, int masp, int soLuong,String size, String mauSac, boolean isUpdate) {
    	 SanPhamQuanAo product = spRepository.findById(masp).orElse(null); // Tìm sản phẩm

    	 HashMap<Integer, CartDetailDto> cartDetail = cart.getDetail();

         if (cartDetail.containsKey(masp)) {
             if (soLuong < 1) {
                 cartDetail.remove(masp);
             } else {
                 if (isUpdate) {
                     cartDetail.get(masp).setSlMua(soLuong);
                 } else {
                     int slCu = cartDetail.get(masp).getSlMua();
                     cartDetail.get(masp).setSlMua(slCu + soLuong);
                 }
             }
         } else {
             CartDetailDto cartDetailDto = new CartDetailDto();
             cartDetailDto.setTensp(product.getTenSanPham());
             cartDetailDto.setGiasp(product.getGia());
             cartDetailDto.setMasp(product.getMaSanPham());
             cartDetailDto.setHinh(product.getDuongDanHinhAnh());
             cartDetailDto.setSlMua(soLuong);
             cartDetailDto.setSize(size); // Thêm size vào DTO
             cartDetailDto.setMauSac(mauSac); // Thêm màu sắc vào DTO
             cartDetail.put(masp, cartDetailDto);
         }

         cart.setTongsl(getTongSL(cart));
         cart.setTongtien(getTongTien(cart));

         return cart;
     }

    public double getTongTien(CartDto cart) {
        HashMap<Integer, CartDetailDto> cartDetail = cart.getDetail();
        Double tongTien = 0D;
        for (CartDetailDto cartDetailDto : cartDetail.values()) {
            SanPhamQuanAo product = spRepository.findById(cartDetailDto.getMasp()).orElse(null);
            if (product != null) {
                tongTien += product.getGia() * cartDetailDto.getSlMua();
            }
        }
        return tongTien;
    }

    public int getTongSL(CartDto cart) {
        HashMap<Integer, CartDetailDto> cartDetail = cart.getDetail();
        int tongSL = 0;
        for (CartDetailDto productDto : cartDetail.values()) {
            tongSL += productDto.getSlMua();
        }
        return tongSL;
    }

    private DonHang mappingDtoToOrderEntity(CartDto cartDto) {
        DonHang donHang = new DonHang();
        donHang.setDiaChi(cartDto.getDiachi());
        donHang.setDienThoai(cartDto.getDienthoai());
        donHang.setTongTien(cartDto.getTongtien());
        KhachHang user = khachHangRepository.findById(cartDto.getUserId()).orElse(null);
        if (user == null) {
            System.err.println("Người dùng không tồn tại với ID: " + cartDto.getUserId());
        }
        donHang.setKhachHang(user);
        return donHang;
    }

    private List<ChiTietDonHang> mappingDtoToOrderDetailEntity(HashMap<Integer, CartDetailDto> details, DonHang donHang) {
        List<ChiTietDonHang> result = new ArrayList<>();
        details.forEach((key, ctdh) -> {
            ChiTietDonHang chitietdonhang = new ChiTietDonHang();
            SanPhamQuanAo sanpham = spRepository.findById(ctdh.getMasp()).orElse(null);
            if (sanpham == null) {
                System.err.println("Sản phẩm không tồn tại với ID: " + ctdh.getMasp());
                return; // Bỏ qua sản phẩm này nếu không tồn tại
            }
            chitietdonhang.setSanPhamQuanAo(sanpham);
            chitietdonhang.setGia(ctdh.getGiasp());
            chitietdonhang.setSoLuong(ctdh.getSlMua());
            chitietdonhang.setSize(ctdh.getSize()); // Set kích thước
            chitietdonhang.setMauSac(ctdh.getMauSac()); // Set màu sắc
            chitietdonhang.setDonHang(donHang);
            result.add(chitietdonhang);
        });
        return result;
    }
}
