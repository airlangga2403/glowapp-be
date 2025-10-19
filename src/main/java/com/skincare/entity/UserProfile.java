package com.skincare.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skincare.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "user_profile")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column()
    private String name;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    @Column()
    private String password;

    private String phone;

    private Integer genderId;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "skin_type_id", length = 50)
    private Integer skinTypeId;

    @Column(name = "has_allergy")
    private Boolean hasAllergy = false;

    @Column(name = "allergy_notes", columnDefinition = "TEXT")
    private String allergyNotes;

    @Column(name = "budget_min", precision = 10, scale = 2)
    private BigDecimal budgetMin;

    @Column(name = "budget_max", precision = 10, scale = 2)
    private BigDecimal budgetMax;

    @Column(name = "preferred_category", length = 100)
    private String preferredCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id", referencedColumnName = "gender_id",
            insertable = false, updatable = false)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skin_type_id", referencedColumnName = "skin_type_id",
            insertable = false, updatable = false)
    private SkinType skinType;
}

