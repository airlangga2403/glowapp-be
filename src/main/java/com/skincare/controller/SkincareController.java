//package com.skincare.controller;
//
//import com.skincare.dto.RecommendationRequest;
//import com.skincare.dto.RecommendationResponse;
//import com.skincare.model.Product;
//import com.skincare.service.uji_coba.MLModelService;
//import com.skincare.service.uji_coba.RecomendarService;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/skincare")
//@CrossOrigin(origins = "*")
//@Log4j2
//public class SkincareController {
//
//    @Autowired
//    private RecomendarService recomendarService;
//
//    @Autowired
//    private MLModelService mlModelService;
//
//    @PostMapping("/recommend")
//    public ResponseEntity<RecommendationResponse> getRecommendations(
//            @RequestBody RecommendationRequest request) {
//
//        log.info("POST /api/skincare/recommend - Request: {}", request);
//
//        RecommendationResponse response = recomendarService.getTopRecommendations(request);
//
//        log.info("Response: success={}, totalFound={}",
//                response.getSuccess(), response.getTotalFound());
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/products")
//    public ResponseEntity<List<Product>> getAllProducts() {
//        log.info("GET /api/skincare/products");
//        return ResponseEntity.ok(mlModelService.getAllProducts());
//    }
//
//    @GetMapping("/health")
//    public ResponseEntity<String> healthCheck() {
//        return ResponseEntity.ok("Skincare Recommendation API is running!");
//    }
//}