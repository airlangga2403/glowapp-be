package com.skincare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skin_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkinType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skin_type_id")
    private Integer skinTypeId;


    @Column(name = "skin_type_name")
    private String skinTypeName;
}
