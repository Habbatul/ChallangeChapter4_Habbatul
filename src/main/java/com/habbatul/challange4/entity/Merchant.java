package com.habbatul.challange4.entity;

import com.habbatul.challange4.enums.MerchantStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "merchant")
public class Merchant {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "merchant_code")
    private String merchantCode;

    @Column(unique = true)
    private String merchantName;

    private String merchantLocation;

    @Enumerated(EnumType.STRING)
    private MerchantStatus open;

    //untuk proses bisnis sementara pesan cascade = CascadeType.REMOVE ke orderDetail
    //karena order masih ambigu, apakah order yang completed akan menjadi log history atau tidak
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Product> products;
}


