package com.skincare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
//  [2.ON PROCESS, 0.CANCELED, 1.SUCCESS ]
public class TransactionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_status_id")
    private Integer id;

    @Column(name = "status")
    private String name;
}
