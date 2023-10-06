package com.habbatul.challange4.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "merchant_code")
    private Merchant merchant;
}
