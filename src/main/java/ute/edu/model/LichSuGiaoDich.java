package ute.edu.model;

import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lich_su_giao_dich")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuGiaoDich implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_giao_dich")
    private int maGiaoDich;

    @Column(name = "ma_don_hang")
    private int maDonHang;

    @Column(name = "ngay_giao_dich")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayGiaoDich = new Date(); // Đặt giá trị mặc định là ngày hiện tại

    // Mối quan hệ với DonHang
    @ManyToOne
    @JoinColumn(name = "ma_don_hang", referencedColumnName = "ma_don_hang", insertable = false, updatable = false)
    private DonHang donHang;
}
