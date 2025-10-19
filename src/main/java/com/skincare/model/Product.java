//package com.skincare.model;
//
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Product {
//    private Integer id;
//    private String label;
//    private String brand;
//    private String name;
//    private Double price;
//    private Double rank;
//    @JsonProperty("allergen_count")
//    private Integer allergenCount;
//
//    @JsonProperty("beneficial_count")
//    private Integer beneficialCount;
//
//    @JsonProperty("quality_score")
//    private Double qualityScore;
//
//    @JsonProperty("value_score")
//    private Double valueScore;
//    private Integer dry;
//    private Integer normal;
//    private Integer oily;
//    private Integer sensitive;
//    private Integer combination;
//    private String ingredients;
//
//    public boolean isSuitableForSkinType(String skinType) {
//        switch (skinType.toLowerCase()) {
//            case "dry": return this.dry == 1;
//            case "normal": return this.normal == 1;
//            case "oily": return this.oily == 1;
//            case "sensitive": return this.sensitive == 1;
//            case "combination": return this.combination == 1;
//            default: return false;
//        }
//    }
//}
//
