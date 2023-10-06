package com.habbatul.challange4.entity;

import com.habbatul.challange4.enums.MerchantStatus;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
}


