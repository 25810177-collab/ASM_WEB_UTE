package ute.edu.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int masp;
    private String tensp;
    private double giasp;
    private String hinh;
    private int slMua;
    private String size;
    private String mauSac;

    // Phương thức setHinh nhận vào một danh sách và chuyển thành chuỗi
    public void setHinh(List<String> hinh) {
        this.hinh = String.join(",", hinh);
    }

    // Phương thức getHinh trả về danh sách chuỗi từ chuỗi đã tách
    public List<String> getHinh() {
        return Arrays.asList(hinh.split(","));
    }
}
