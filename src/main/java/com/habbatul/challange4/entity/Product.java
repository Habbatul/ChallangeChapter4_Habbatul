package com.habbatul.challange4.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name ="product_code")
    private String productCode;

    @Column(unique = true)
    private String productName;

    private Double price;

    private LocalDateTime addedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_code")
    private Merchant merchant;

    //untuk proses bisnis sementara pesan cascade = CascadeType.REMOVE ke orderDetail
    //karena order masih ambigu, apakah order yang completed akan menjadi log history atau tidak
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<OrderDetail> orderDetail;
}
