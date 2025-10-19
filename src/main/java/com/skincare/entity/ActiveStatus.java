package com.skincare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "active_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "active_status_id")
    private Integer activeStatusId;

    @Column(name = "status")
    private String name;
}
